package com.patrickanker.isay.channels;

import com.patrickanker.isay.ChatPlayer;
import com.patrickanker.isay.ISMain;
import com.patrickanker.isay.Statistician;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

public class ChannelManager {

    protected static HashMap<Channel, Boolean> channels = new HashMap();
    protected Channel def = null;
    protected Channel helpop = null;

    private DebugChannel debug;
    
    private static final File configFile = new File("plugins/iSay/channels.yml");

    public ChannelManager()
    {
        registerChannel(new DebugChannel());

        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
                ISMain.log("No channel config found... Created new config & internally writing default channels");
            } catch (IOException ex) {
                ISMain.log("Could not create channel config file: " + ex.getMessage(), 2);
            }

            writeDefaults();
        } else {
            YamlConfiguration channelConfig = ISMain.getChannelConfig();

            try {
                channelConfig.load(configFile);
            } catch (Throwable t) {
                ISMain.log("Could not load channels: " + t.getMessage(), 2);
                writeDefaults();
            }

            List<String> _channelList = channelConfig.getStringList("channels");

            for (String _channel : _channelList) {
                ChatChannel cc = new ChatChannel(_channel);
                registerChannel(cc);
            }

            for (Map.Entry<Channel, Boolean> entry : channels.entrySet()) {
                if (((ChatChannel) entry.getKey()).isDefault()) {
                    this.def = entry.getKey();
                    break;
                }
            }

            for (Map.Entry<Channel, Boolean> entry : channels.entrySet()) {
                if (((ChatChannel) entry.getKey()).isHelpOp()) {
                    this.helpop = entry.getKey();
                    break;
                }
            }
        }

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(ISMain.getInstance(), new MPMThread(), 0L, 1200L);
    }

    public void registerChannel(Channel channel)
    {
        if (channel instanceof ChatChannel) {
            ChatChannel _channel = (ChatChannel) channel;
            channels.put(_channel, _channel.isEnabled());
        }

        if (channel instanceof DebugChannel) {
            debug = (DebugChannel) channel;
        }
    }

    public void unregisterChannel(Channel channel)
    {
        if (channel instanceof ChatChannel) {
            if (channels.containsKey(channel)) {
                channels.remove(channel);
            }
        }
    }

    public boolean isRegistered(Channel channel)
    {
        if (channel instanceof ChatChannel)
            return channels.containsKey(channel);
        else if (channel instanceof DebugChannel) {
            DebugChannel db = (DebugChannel) channel;

            return debug.getSessionUUID().equals(db.getSessionUUID());
        }

        return false;
    }

    // ChatChannel stuff
    public void removeChannel(ChatChannel channel)
    {
        unregisterChannel(channel);

        for (Map.Entry<String, Boolean> listener : channel.listeners.entrySet()) {
            if (listener.getValue()) {
                if (!def.hasListener(listener.getKey())) {
                    def.addListener(listener.getKey(), true);
                } else {
                    def.assignFocus(listener.getKey(), true);
                }
            }
        }

        List<String> l = new LinkedList<String>();

        for (Map.Entry<Channel, Boolean> entry : channels.entrySet()) {
            if (entry.getKey().getName().equals(channel.getName()))
                continue;

            l.add(entry.getKey().getName());
        }

        ISMain.getChannelConfig().set("channels", l);
        ISMain.getChannelConfig().set(channel.getName(), null);

    }

    public List<Channel> matchChannel(String name)
    {
        List ret = new ArrayList();
        ret.clear();

        for (Map.Entry entry : channels.entrySet()) {
            if ((((Channel) entry.getKey()).getName().toLowerCase().startsWith(name.toLowerCase()))
                    && (!ret.contains(entry.getKey()))) {
                ret.add(entry.getKey());
            }
        }
        return ret;
    }
    
    public void saveChannels()
    {
        List<String> l = new LinkedList<String>();
        
        for (Map.Entry<Channel, Boolean> entry : channels.entrySet()) {
            l.add(entry.getKey().getName());
            entry.getKey().dump();
        }
        
        ISMain.getChannelConfig().set("channels", l);
        
        try {
            ISMain.getChannelConfig().save(configFile);
        } catch (IOException ex) {
            ISMain.log("Could not save channel config: " + ex.getMessage(), 2);
        }
    }
    
    public void reloadChannels()
    {
        for (Map.Entry<Channel, Boolean> entry : channels.entrySet()) {
            entry.getKey().load();
        }
    }

    public void shutDown()
    {
        saveChannels();
        
        for (Channel channel : getList())
            channel.listeners.clear();
        
        channels.clear();
    }

    public Channel getDefaultChannel()
    {
        return this.def;
    }

    public Channel getHelpOpChannel()
    {
        return this.helpop;
    }

    public Channel getFocus(String player)
    {
        for (Map.Entry channel : getMap().entrySet()) {
            if (((Channel) channel.getKey()).hasFocus(player)) {
                return (Channel) channel.getKey();
            }
        }

        return null;
    }

    public HashMap<Channel, Boolean> getMap()
    {
        return channels;
    }

    public List<Channel> getList()
    {
        List l = new LinkedList();
        l.clear();

        for (Map.Entry entry : getMap().entrySet()) {
            if (!l.contains(entry.getKey())) {
                l.add(entry.getKey());
            }
        }
        return l;
    }
    
    private void writeDefaults()
    {
        channels.clear();
        
        ChatChannel pub = new ChatChannel("Public");
        pub.setDefault(true);
        pub.setGhostFormat("&8[&aPublic&8] $group&8: &7$message");
        registerChannel(pub);
        this.def = pub;

        ChatChannel _helpop = new ChatChannel("HelpOp");
        _helpop.setHelpOp(true);
        _helpop.setGhostFormat("&4[&cHELPOP&4] &7$name&f:&d $message");
        registerChannel(_helpop);
        this.helpop = _helpop;
    }

    // DebugChannel stuff
    public DebugChannel getDebugChannel()
    {
        return debug;
    }

    // Events
    public void onPlayerLogin(ChatPlayer cp)
    {
        ((ChatChannel) getDefaultChannel()).connectWithoutBroadcast(cp.getPlayer().getName());

        if (cp.hasAutoJoin()) {
            if (cp.isJoinAllAvailableEnabled()) {
                ISMain.getChannelManager().joinAllAvailableChannels(cp);
            } else {
                List<String> channelNames = cp.getAutoJoinList();
                Iterator<String> it = channelNames.listIterator();

                while (it.hasNext()) {
                    String channelName = (String) it.next();
                    List l = matchChannel(channelName);

                    if ((l.size() == 1) && (cp.canConnect((Channel) l.get(0), ""))) {
                        ((ChatChannel) l.get(0)).connectWithoutBroadcast(cp.getPlayer().getName());
                    }
                }
            }
        }

        getDefaultChannel().assignFocus(cp.getPlayer().getName(), true);

        if ((cp.getPlayer().isOp()) && (cp.canConnect(ISMain.getChannelManager().getHelpOpChannel(), ""))) {
            getHelpOpChannel().addListener(cp.getPlayer().getName(), false);
        }
    }

    public void joinAllAvailableChannels(ChatPlayer cp)
    {
        for (Map.Entry channel : getMap().entrySet()) {
            if (cp.canConnect((Channel) channel.getKey(), "")) {
                ChatChannel c = (ChatChannel) channel.getKey();
                c.connectWithoutBroadcast(cp.getPlayer().getName());
            }
        }

        getDefaultChannel().assignFocus(cp.getPlayer().getName(), true);
    }

    public void disconnectFromAllChannels(ChatPlayer cp)
    {
        for (Map.Entry entry : getMap().entrySet()) {
            ChatChannel c = (ChatChannel) entry.getKey();
            c.silentDisconnect(cp.getPlayer().getName());
        }
    }
    
    class MPMThread implements Runnable {

        private int lastCount = 0;
        
        @Override
        public void run()
        {
            Statistician stats = Statistician.getStats();
            
            int count = stats.fetchInt(ChatChannel.STATS_CURRENT_MESSAGE_COUNT);
            
            if (lastCount == 0) {
                lastCount = count;
                return;
            }
            
            int diff = count - lastCount;
            stats.updateInt(ChatChannel.STATS_MPM, diff);
            
            lastCount = count;
        }
    }
}