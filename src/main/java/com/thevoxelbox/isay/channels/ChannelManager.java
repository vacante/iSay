package com.thevoxelbox.isay.channels;

import com.thevoxelbox.isay.ChatPlayer;
import com.thevoxelbox.isay.ISMain;
import java.io.File;
import java.util.*;

public class ChannelManager {

    public static HashMap<Channel, Boolean> channels = new HashMap();
    public static List<Channel> PMs = new LinkedList();
    protected Channel def = null;
    protected Channel helpop = null;

    public void registerChannel(Channel channel)
    {
        if ((channel instanceof ChatChannel)) {
            ChatChannel _channel = (ChatChannel) channel;
            channels.put(_channel, Boolean.valueOf(_channel.isEnabled()));
        }
    }

    public void unregisterChannel(Channel channel)
    {
        if (!(channel instanceof ChatChannel)) {
            return;
        }
        if (channels.containsKey(channel)) {
            channels.remove(channel);
        }
    }

    public boolean isRegistered(Channel channel)
    {
        if (!(channel instanceof ChatChannel)) {
            return false;
        }

        return channels.containsKey(channel);
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

    public List<Channel> matchChannel(String key, Object value)
    {
        List<Channel> ret = new ArrayList<Channel>();
        ret.clear();

        for (Map.Entry entry : channels.entrySet()) {
            ChatChannel c = (ChatChannel) entry.getKey();

            if ((key.equalsIgnoreCase("default"))
                    && (c.isDefault() == ((Boolean) value).booleanValue())
                    && (!ret.contains(c))) {
                ret.add(c);
            }
            if ((key.equalsIgnoreCase("enabled"))
                    && (c.isEnabled() == ((Boolean) value).booleanValue())
                    && (!ret.contains(c))) {
                ret.add(c);
            }
            if ((key.equalsIgnoreCase("helpop"))
                    && (c.isHelpOp() == ((Boolean) value).booleanValue())
                    && (!ret.contains(c))) {
                ret.add(c);
            }
            if ((key.equalsIgnoreCase("locked"))
                    && (c.isLocked() == ((Boolean) value).booleanValue())
                    && (!ret.contains(c))) {
                ret.add(c);
            }
            if ((key.equalsIgnoreCase("ghostformat"))
                    && (c.getGhostFormat().equals(value.toString()))
                    && (!ret.contains(c))) {
                ret.add(c);
            }
            if ((key.equalsIgnoreCase("password"))
                    && (c.getPassword().equals(value.toString()))
                    && (!ret.contains(c))) {
                ret.add(c);
            }
        }
        return ret;
    }
    
    public void reloadChannels()
    {
        for (Map.Entry<Channel, Boolean> entry : channels.entrySet()) {
            entry.getKey().load();
        }
    }

    public ChannelManager()
    {
        File d = new File("plugins/iSay/channels/");

        if (!d.exists()) {
            d.mkdir();
            ISMain.log("No channel directory found... Created new directory & internally writing default channels");

            ChatChannel pub = new ChatChannel("public");
            pub.setDefault(true);
            pub.setGhostFormat("&8[&apublic&8] $group &7$message");
            registerChannel(pub);
            this.def = pub;

            ChatChannel _helpop = new ChatChannel("HelpOp");
            _helpop.setHelpOp(true);
            _helpop.setGhostFormat("&4[&cHELPOP&4] &7$name&f:&d $message");
            registerChannel(_helpop);
            this.helpop = _helpop;
        } else if (d.list().length == 0) {
            ISMain.log("No channels found... Internally writing default channels");

            ChatChannel pub = new ChatChannel("public");
            pub.setDefault(true);
            pub.setGhostFormat("&8[&apublic&8] $group &7$message");
            registerChannel(pub);
            this.def = pub;

            ChatChannel _helpop = new ChatChannel("HelpOp");
            _helpop.setHelpOp(true);
            _helpop.setGhostFormat("&4[&cHELPOP&4] &7$name&f:&d $message");
            registerChannel(_helpop);
            this.helpop = _helpop;
        } else {
            String[] strs = d.list();

            for (String str : strs) {
                if (str.contains(".properties")) {
                    registerChannel(new ChatChannel(str.replace(".properties", "")));
                }
            }
            
            List<Channel> l = matchChannel("default", Boolean.TRUE);
            if ((l != null) && (l.size() == 1)) {
                this.def = ((Channel) l.get(0));
            }
            
            l = matchChannel("helpop", Boolean.TRUE);
            if ((l != null) && (l.size() == 1)) {
                this.helpop = ((Channel) l.get(0));
            }
        }
    }

    public void shutDown()
    {
        for (Map.Entry entry : channels.entrySet()) {
            ChatChannel c = (ChatChannel) entry.getKey();

            c.dump();
        }

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

    public void onPlayerLogin(ChatPlayer cp)
    {
        ((ChatChannel) getDefaultChannel()).connectWithoutBroadcast(cp.getPlayer().getName());

        if (cp.isJoinAllAvailableEnabled()) {
            ISMain.getChannelManager().joinAllAvailableChannels(cp);
        } else if (cp.hasAutoJoin()) {
            List channelNames = cp.getAutoJoinList();
            Iterator it = channelNames.listIterator();

            while (it.hasNext()) {
                String channelName = (String) it.next();
                List l = matchChannel(channelName);

                if ((!l.isEmpty()) && (l.size() <= 1)
                        && (cp.canConnect((Channel) l.get(0), ""))) {
                    ((ChatChannel) l.get(0)).connectWithoutBroadcast(cp.getPlayer().getName());
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

    public void remove(Channel channel)
    {
        if ((channel instanceof ChatChannel)) {
            ChatChannel c = (ChatChannel) channel;

            File f = new File("plugins/iSay/channels/" + c.getName() + ".properties");

            if (f.exists()) {
                f.delete();
            }
        }
    }
}