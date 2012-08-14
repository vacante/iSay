package com.thevoxelbox.isay.channels;

import com.patrickanker.lib.logging.ConsoleLogger;
import com.patrickanker.lib.util.Formatter;
import com.patrickanker.lib.util.JavaPropertiesFileManager;
import com.thevoxelbox.isay.ChatPlayer;
import com.thevoxelbox.isay.ISMain;
import com.thevoxelbox.isay.MessageFormattingServices;
import com.thevoxelbox.isay.formatters.GhostMessageFormatter;
import com.thevoxelbox.isay.formatters.MessageFormatter;
import com.thevoxelbox.voxelguest.AsshatMitigationModule;
import com.thevoxelbox.voxelguest.modules.ModuleManager;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatChannel extends Channel {

    protected Boolean def = Boolean.FALSE;
    protected Boolean enabled = Boolean.TRUE;
    protected Boolean helpop = Boolean.FALSE;
    protected Boolean locked = Boolean.FALSE;
    protected String ghostformat = "&8[&f" + this.name + "&8] $group&f $message";
    protected String password = "";

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

                if (((Boolean) l.getValue()).booleanValue()) {
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
        String copy = message;
        
        if (MessageFormattingServices.containsURLs(message)) {
            copy = MessageFormattingServices.shortenURLs(message);
        }
        
        String focus = Formatter.selectFormatter(MessageFormatter.class).formatMessage(copy, cp);
        String ghost = Formatter.selectFormatter(GhostMessageFormatter.class).formatMessage(copy, cp, this);
        
        try {
            AsshatMitigationModule module = (AsshatMitigationModule) ModuleManager.getManager().getModule(AsshatMitigationModule.class);

            if (module.gagged.contains(cp.getPlayer().getName())) {
                cp.sendMessage("§cYou are gagged. You cannot chat.");
                return;
            }
        } catch (Exception ex) {
            // Continue -- Either Guest is not loaded or Asshat Mitigation is turned off
        }

        for (Map.Entry l : this.listeners.entrySet()) {
            Player pl = Bukkit.getPlayer((String) l.getKey());
            ChatPlayer _cp = ISMain.getRegisteredPlayer(pl);

            ChatPlayer[] pingees = ISMain.getPingManager().getPingeesFromString(message);

            for (ChatPlayer pingee : pingees) {
                if (ISMain.getPingManager().canPing(cp, pingee)) {
                    ISMain.getPingManager().doPing(cp, pingee);
                }
            }

            if (pl == null) {
                removeListener((String) l.getKey());
                continue;
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
        
        ConsoleLogger.getLogger("iSay").log(Formatter.stripColors(getName() + "-> " + cp.getPlayer().getName() + ": " + message));
    }

    @Override
    public void disconnect(String player)
    {
        if (hasListener(player)) {
            for (Map.Entry l : this.listeners.entrySet()) {
                Player pl = Bukkit.getPlayer((String) l.getKey());

                if (((Boolean) l.getValue()).booleanValue()) {
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
        HashMap<String, Object> data = (HashMap<String, Object>) JavaPropertiesFileManager.load(getName(), "/iSay/channels");

        if (data != null) {
            if (data.containsKey("default")) {
                this.def = ((Boolean) data.get("default"));
            }
            if (data.containsKey("enabled")) {
                this.enabled = ((Boolean) data.get("enabled"));
            }
            if (data.containsKey("helpop")) {
                this.helpop = ((Boolean) data.get("helpop"));
            }
            if (data.containsKey("locked")) {
                this.locked = ((Boolean) data.get("locked"));
            }

            if (data.containsKey("ghostformat")) {
                this.ghostformat = data.get("ghostformat").toString();
            }
            if (data.containsKey("password")) {
                this.password = data.get("password").toString();
            }
        }
    }

    @Override
    public void dump()
    {
        HashMap<String, Object> data = new HashMap<String, Object>();

        data.put("default", this.def);
        data.put("enabled", this.enabled);
        data.put("helpop", this.helpop);
        data.put("locked", this.locked);

        data.put("ghostformat", this.ghostformat);
        data.put("password", this.password);

        JavaPropertiesFileManager.save(getName(), data, "/iSay/channels");
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
        return this.def.booleanValue();
    }

    public boolean isEnabled()
    {
        return this.enabled.booleanValue();
    }

    public boolean isHelpOp()
    {
        return this.helpop.booleanValue();
    }

    public boolean isLocked()
    {
        return this.locked.booleanValue();
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