package com.patrickanker.isay.channels;

import com.patrickanker.lib.logging.ConsoleLogger;
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
    protected boolean verbose = true;
    protected String ghostformat = "&8[&f" + this.name + "&8] $group&f $message";
    protected String password = "";
    
    public static final String STATS_CURRENT_MESSAGE_COUNT = "chatchannel-message-current-message-count";
    public static final String STATS_MPM = "chatchannel-message-mps";

    public ChatChannel(String name)
    {
        super(name);
        load();
    }

    public ChatChannel(String name, String password)
    {
        super(name);
        this.password = password;
    }

    @Override
    public void connect(String player)
    {
        if (!hasListener(player)) {
            addListener(player, true);

            for (Map.Entry l : this.listeners.entrySet()) {
                Player pl = Bukkit.getPlayer((String) l.getKey());

                if (((Boolean) l.getValue()).booleanValue() && verbose) {
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

    public void setListenerType(String player, boolean focused)
    {
        if (!hasListener(player)) {
            addListener(player, focused);
        } else {
            assignFocus(player, focused);
        }
    }

    @Override
    public void dispatch(ChatPlayer cp, String message)
    {
        List<String> oldListeners = new LinkedList<String>();
        
        String copy = message;
        
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

            if ((_cp.isIgnoring(cp)) || ((ISMain.getRegisteredPlayer(pl).isMuted()) && (!isHelpOp()))) {
                continue;
            }

            if (((Boolean) l.getValue()).booleanValue()) {
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
        
        ConsoleLogger.getLogger("iSay").log(Formatter.stripColors(getName() + "-> " + cp.getPlayer().getName() + ": " + message));
    }

    @Override
    public void disconnect(String player)
    {
        if (hasListener(player)) {
            for (Map.Entry l : this.listeners.entrySet()) {
                Player pl = Bukkit.getPlayer((String) l.getKey());

                if (((Boolean) l.getValue()).booleanValue() && verbose) {
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
        
        if (ISMain.getChannelConfig().contains(this.name + ".ghostformat")) {
            this.ghostformat = ISMain.getChannelConfig().getString(this.name + ".ghostformat");
        }

        if (ISMain.getChannelConfig().contains(this.name + ".password")) {
            this.password = ISMain.getChannelConfig().getString(this.name + ".password");
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
        ISMain.getChannelConfig().set(this.name + ".ghostformat", this.ghostformat);
        ISMain.getChannelConfig().set(this.name + ".password", this.password);
    }

    public void setDefault(boolean bool)
    {
        this.def = Boolean.valueOf(bool);
    }

    public void setEnabled(boolean bool)
    {
        this.enabled = Boolean.valueOf(bool);
    }

    public void setHelpOp(boolean bool)
    {
        this.helpop = Boolean.valueOf(bool);
    }

    public void setLocked(boolean bool)
    {
        this.locked = Boolean.valueOf(bool);
    }
    
    public void setVerbose(boolean bool)
    {
        this.verbose = Boolean.valueOf(bool);
    }

    public void setGhostFormat(String str)
    {
        this.ghostformat = str;
    }

    public void setPassword(String str)
    {
        this.password = str;
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

    public String getGhostFormat()
    {
        return this.ghostformat;
    }

    public String getPassword()
    {
        return this.password;
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
        HashMap<String, Boolean> map = listeners;
        return map;
    }
}