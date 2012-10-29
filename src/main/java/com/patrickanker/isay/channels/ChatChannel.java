package com.patrickanker.isay.channels;

import com.patrickanker.lib.logging.ConsoleLogger;
import com.patrickanker.lib.permissions.PermissionsManager;
import com.patrickanker.lib.util.Formatter;
import com.patrickanker.isay.ChatPlayer;
import com.patrickanker.isay.ISMain;
import com.patrickanker.isay.MessageFormattingServices;
import com.patrickanker.isay.Statistician;
import com.patrickanker.isay.formatters.GhostMessageFormatter;
import com.patrickanker.isay.formatters.MessageFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ChatChannel extends Channel {

    protected boolean def = false;
    protected boolean enabled = true;
    protected boolean helpop = false;
    protected boolean locked = false;
    protected boolean promoted = false;
    protected boolean verbose = true;
    protected String ghostformat = "&8[&f" + this.name + "&8] $group&f $message";
    protected String password = "";
    protected List<String> banlist = new LinkedList<String>();
    
    public static final String STATS_CURRENT_MESSAGE_COUNT = "chatchannel-message-current-message-count";
    public static final String STATS_MPM = "chatchannel-message-mps";

    public ChatChannel(String name)
    {
        super(name);
        load();
    }

    @Override
    public void connect(String player)
    {
        if (!hasListener(player)) {
            addListener(player, true);

            for (Map.Entry l : this.listeners.entrySet()) {
                Player pl = Bukkit.getPlayer((String) l.getKey());

                if (((Boolean) l.getValue()) && verbose) {
                    pl.sendMessage(ChatColor.GREEN + player + ChatColor.GRAY + " has joined " + ChatColor.GREEN + this.name + ChatColor.DARK_GRAY + ".");
                }
            }
        }
    }

    public void connectWithoutBroadcast(String player)
    {
        if (!hasListener(player)) {
            addListener(player, true);
        } else if ((hasListener(player)) && (!hasFocus(player))) {
            assignFocus(player, true);
        }
    }

    @Override
    public void dispatch(ChatPlayer cp, String message)
    {
        List<String> oldListeners = new LinkedList<String>();
        
        String copy = message;

        if (promoted && !PermissionsManager.getHandler().hasPermission(cp.getPlayer().getName(), "isay.channels." + name + ".promoted")) {
            cp.sendMessage("§cThis is a promoted channel. You cannot chat without permission.");
            return;
        }
        
        if (MessageFormattingServices.containsURLs(message)) {
            copy = MessageFormattingServices.shortenURLs(message);
        }
        
        String focus = Formatter.selectFormatter(MessageFormatter.class).formatMessage(copy, cp);
        String ghost = Formatter.selectFormatter(GhostMessageFormatter.class).formatMessage(copy, cp, this);

        for (Map.Entry l : this.listeners.entrySet()) {
            OfflinePlayer op = Bukkit.getOfflinePlayer((String) l.getKey());
            
            if (!op.isOnline()) {
                oldListeners.add(op.getName());
                continue;
            }
            
            Player pl = op.getPlayer();
            ChatPlayer _cp = ISMain.getRegisteredPlayer(pl);

            ChatPlayer[] pingees = ISMain.getPingManager().getPingeesFromString(message);

            for (ChatPlayer pingee : pingees) {
                if (ISMain.getPingManager().canPing(cp, pingee)) {
                    ISMain.getPingManager().doPing(cp, pingee);
                }
            }

            if (ISMain.getChannelManager().getDebugChannel().hasListener(_cp.getPlayer().getName())) {
                continue;
            }

            if ((_cp.isIgnoring(cp)) || ((ISMain.getRegisteredPlayer(pl).isMuted()) && (!isHelpOp()))) {
                continue;
            }

            if (((Boolean) l.getValue())) {
                _cp.sendMessage(focus);
                
            } else {
                
                _cp.sendMessage(ghost);
            }
        }
        
        for (String listener : oldListeners) {
            removeListener(listener);
        }
        
        // -- Stats --
        
        Statistician stats = Statistician.getStats();
        
        int count = stats.fetchInt(STATS_CURRENT_MESSAGE_COUNT);
        count += 1;
        
        if (count == 0) {
            count = 1;
        }
        
        stats.updateInt(STATS_CURRENT_MESSAGE_COUNT, count);

        ISMain.log(getName() + "-> " + cp.getPlayer().getName() + ": " + message);
    }

    @Override
    public void disconnect(String player)
    {
        if (hasListener(player)) {
            for (Map.Entry l : this.listeners.entrySet()) {
                Player pl = Bukkit.getPlayer((String) l.getKey());

                if (((Boolean) l.getValue()) && verbose) {
                    pl.sendMessage(ChatColor.GREEN + player + ChatColor.GRAY + " has left " + ChatColor.GREEN + this.name + ChatColor.DARK_GRAY + ".");
                }
            }

            removeListener(player);
        }
    }

    public void silentDisconnect(String player)
    {
        if (hasListener(player)) {
            removeListener(player);
        }
    }

    public List<String> getListenerList()
    {
        List<String> l = new LinkedList<String>();

        for (Map.Entry<String, Boolean> entry : listeners.entrySet()) {
            if (!l.contains(entry.getKey()))
                l.add(entry.getKey());
        }

        return l;
    }

    public HashMap<String, Boolean> getListenerMap()
    {
        return listeners;
    }

    @Override
    public void load()
    {   
        if (ISMain.getChannelConfig().contains(this.name + ".default")) {
            this.def = ISMain.getChannelConfig().getBoolean(this.name + ".default");
        }
        
        if (ISMain.getChannelConfig().contains(this.name + ".enabled")) {
            this.enabled = ISMain.getChannelConfig().getBoolean(this.name + ".enabled");
        }
        
        if (ISMain.getChannelConfig().contains(this.name + ".helpop")) {
            this.helpop = ISMain.getChannelConfig().getBoolean(this.name + ".helpop");
        }
        
        if (ISMain.getChannelConfig().contains(this.name + ".locked")) {
            this.locked = ISMain.getChannelConfig().getBoolean(this.name + ".locked");
        }
        
        if (ISMain.getChannelConfig().contains(this.name + ".verbose")) {
            this.verbose = ISMain.getChannelConfig().getBoolean(this.name + ".verbose");
        }

        if (ISMain.getChannelConfig().contains(this.name + ".promoted")) {
            this.promoted = ISMain.getChannelConfig().getBoolean(this.name + ".promoted");
        }
        
        if (ISMain.getChannelConfig().contains(this.name + ".ghostformat")) {
            this.ghostformat = ISMain.getChannelConfig().getString(this.name + ".ghostformat");
        }

        if (ISMain.getChannelConfig().contains(this.name + ".password")) {
            this.password = ISMain.getChannelConfig().getString(this.name + ".password");
        }

        if (ISMain.getChannelConfig().contains(this.name + ".banlist")) {
            this.banlist = ISMain.getChannelConfig().getStringList(this.name + ".banlist");
        }
    }

    @Override
    public void dump()
    {
        ISMain.getChannelConfig().set(this.name + ".default", this.def);
        ISMain.getChannelConfig().set(this.name + ".enabled", this.enabled);
        ISMain.getChannelConfig().set(this.name + ".helpop", this.helpop);
        ISMain.getChannelConfig().set(this.name + ".locked", this.locked);
        ISMain.getChannelConfig().set(this.name + ".verbose", this.verbose);
        ISMain.getChannelConfig().set(this.name + ".promoted", this.promoted);
        ISMain.getChannelConfig().set(this.name + ".ghostformat", this.ghostformat);
        ISMain.getChannelConfig().set(this.name + ".password", this.password);
        ISMain.getChannelConfig().set(this.name + ".banlist", this.banlist);
    }

    public void setDefault(boolean bool)
    {
        this.def = bool;
    }

    public void setEnabled(boolean bool)
    {
        this.enabled = bool;
    }

    public void setHelpOp(boolean bool)
    {
        this.helpop = bool;
    }

    public void setLocked(boolean bool)
    {
        this.locked = bool;
    }
    
    public void setVerbose(boolean bool)
    {
        this.verbose = bool;
    }

    public void setPromoted(boolean bool)
    {
        this.promoted = bool;
    }

    public void setGhostFormat(String str)
    {
        this.ghostformat = str;
    }

    public void setPassword(String str)
    {
        this.password = str;
    }

    public void addBannedListener(String str)
    {
        if (!banlist.contains(str))
            banlist.add(str);
    }

    public void removeBannedListener(String str)
    {
        if (banlist.contains(str))
            banlist.remove(str);
    }

    public boolean isDefault()
    {
        return this.def;
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public boolean isHelpOp()
    {
        return this.helpop;
    }

    public boolean isLocked()
    {
        return this.locked;
    }
    
    public boolean isVerbose()
    {
        return this.verbose;
    }

    public boolean isPromoted()
    {
        return this.promoted;
    }

    public boolean isBanned(String str)
    {
        return banlist.contains(str);
    }

    public String getGhostFormat()
    {
        return this.ghostformat;
    }

    public String getPassword()
    {
        return this.password;
    }
}