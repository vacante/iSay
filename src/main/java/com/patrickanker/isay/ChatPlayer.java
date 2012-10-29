package com.patrickanker.isay;

import com.patrickanker.lib.permissions.PermissionsManager;
import com.patrickanker.isay.channels.Channel;
import com.patrickanker.isay.channels.ChatChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.entity.Player;

public class ChatPlayer {

    private final Player p;
    private boolean adminMute = false;
    private String adminMuteTimeout = "";
    private String format = "$group";
    private String nameAlias = null;
    private boolean ping = true;
    private boolean joinAllAvailable = false;
    private boolean autoJoin = false;
    private List<String> autoJoinList = new LinkedList<String>();
    private List<String> ignoreList = new LinkedList<String>();
    private boolean muted = false;
    private String muteTimeout = "";
    private ChatPlayer converser;

    String groupFormat = "$name:";

    public ChatPlayer(Player p)
    {
        this.p = p;
        load();
    }

    private void load()
    {
        if (ISMain.getPlayerConfig().contains(this.p.getName() + ".adminmute")) {
            this.adminMute = ISMain.getPlayerConfig().getBoolean(this.p.getName() + ".adminmute");
        }
        
        if (ISMain.getPlayerConfig().contains(this.p.getName() + ".adminmutetimeout")) {
            this.adminMuteTimeout = ISMain.getPlayerConfig().getString(this.p.getName() + ".adminmutetimeout");
        }
        
        if (ISMain.getPlayerConfig().contains(this.p.getName() + ".format")) {
            this.format = ISMain.getPlayerConfig().getString(this.p.getName() + ".format");
        }
        
        if (ISMain.getPlayerConfig().contains(this.p.getName() + ".namealias")) {
            this.nameAlias = ISMain.getPlayerConfig().getString(this.p.getName() + ".namealias");
        }
        
        if (ISMain.getPlayerConfig().contains(this.p.getName() + ".joinallavailable")) {
            this.joinAllAvailable = ISMain.getPlayerConfig().getBoolean(this.p.getName() + ".joinallavailable", false);
        }
        
        if ((ISMain.getPlayerConfig().contains(this.p.getName() + ".autojoinlistenable"))
                && (ISMain.getPlayerConfig().getBoolean(this.p.getName() + ".autojoinlistenable", false)) && (!ISMain.getPlayerConfig().getStringList(".autojoinlist").isEmpty())) {
            List l = ISMain.getPlayerConfig().getStringList(this.p.getName() + ".autojoinlist");
            this.autoJoinList.addAll(l);
            this.autoJoin = true;
        }

        if (ISMain.getPlayerConfig().contains(this.p.getName() + ".ping")) {
            this.ping = ISMain.getPlayerConfig().getBoolean(this.p.getName() + ".ping", true);
        }

        if (ISMain.getPlayerConfig().contains(this.p.getName() + ".ignorelist")) {
            List l = ISMain.getPlayerConfig().getStringList(this.p.getName() + ".ignorelist");
            this.ignoreList.addAll(l);
        }
    }

    public void save()
    {
        ISMain.getPlayerConfig().set(this.p.getName() + ".format", this.format);
        ISMain.getPlayerConfig().set(this.p.getName() + ".namealias", this.nameAlias);
        ISMain.getPlayerConfig().set(this.p.getName() + ".adminmute", this.adminMute);
        ISMain.getPlayerConfig().set(this.p.getName() + ".adminmutetimeout", this.adminMuteTimeout);
        ISMain.getPlayerConfig().set(this.p.getName() + ".ping", this.ping);
        ISMain.getPlayerConfig().set(this.p.getName() + ".joinallavailable", this.joinAllAvailable);
        ISMain.getPlayerConfig().set(this.p.getName() + ".autojoinlistenable", this.autoJoin);
        ISMain.getPlayerConfig().set(this.p.getName() + ".autojoinlist", this.autoJoinList);
        ISMain.getPlayerConfig().set(this.p.getName() + ".ignorelist", this.ignoreList);
    }

    public Player getPlayer()
    {
        return this.p;
    }

    public boolean canConnect(Channel channel, String password)
    {
        if (!(channel instanceof ChatChannel)) {
            return false;
        }

        ChatChannel cc = (ChatChannel) channel;

        if (PermissionsManager.getHandler().hasPermission(this.p.getName(), "isay.admin")) {
            return true;
        }

        if (cc.isBanned(this.p.getName()))
            return false;

        if (ISMain.getConfigData().getBoolean("disable-crossworld-chat")) {
            return (PermissionsManager.getHandler().hasPermission(this.p.getWorld().getName(), this.p.getName(), "isay.channel." + cc.getName().toLowerCase() + ".join"))
                    && (password.equals(cc.getPassword()));
        }

        return (PermissionsManager.getHandler().hasPermission(this.p.getName(), "isay.channel." + cc.getName().toLowerCase() + ".join"))
                && (password.equals(cc.getPassword()));
    }

    public boolean isMuted()
    {
        return this.muted;
    }

    public void setMuted(boolean bool)
    {
        this.muted = bool;
    }
    
    public String getMuteTimeout()
    {
        return muteTimeout;
    }
    
    public boolean muteTimedOut()
    {
        if (muteTimeout.equals(""))
            return true;
        
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        
        try {
            Date timeout = sdf.parse(muteTimeout);
            
            if (now.after(timeout)) {
                muteTimeout = "";
                return true;
            }
        } catch (ParseException ex) {
            // Continue...
        }
        
        return false;
    }
    
    public void setMuteTimeout(String time)
    {
        this.muteTimeout = time;
    }

    public String getFormat()
    {
        return this.format;
    }

    public void setFormat(String str)
    {
        this.format = str;
    }

    public String getGroupFormat()
    {
        try {
            String ret = ISMain.getGroupManager().getGroupConfiguration(PermissionsManager.getHandler().getGroups(this.p.getName())[0]).getString("format");
            return ret;
        } catch (Throwable t) {
            
        }
        
        return this.groupFormat;
    }
    
    public void setNameAlias(String str)
    {
        this.nameAlias = str;
    }
    
    public String getNameAlias()
    {
        return nameAlias;
    }

    public boolean isAdminMuted()
    {
        return this.adminMute;
    }

    public boolean isPingEnabled()
    {
        return this.ping;
    }

    public boolean isJoinAllAvailableEnabled()
    {
        return this.joinAllAvailable;
    }

    public boolean hasAutoJoin()
    {
        return this.autoJoin;
    }

    public List<String> getAutoJoinList()
    {
        return this.autoJoinList;
    }

    public void setAdminMute(boolean bool)
    {
        this.adminMute = bool;
    }
    
    public String getAdminMuteTimeout()
    {
        return this.adminMuteTimeout;
    }
    
    public boolean adminMuteTimedOut()
    {
        if (adminMuteTimeout.equals(""))
            return true;
        
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        
        try {
            Date timeout = sdf.parse(adminMuteTimeout);
            
            if (now.after(timeout)) {
                adminMuteTimeout = "";
                return true;
            }
        } catch (ParseException ex) {
            // Continue...
        }
        
        return false;
    }
    
    public void setAdminMuteTimeout(String time)
    {
        this.adminMuteTimeout = time;
    }

    public void setPing(boolean bool)
    {
        this.ping = bool;
    }

    public void setJoinAllAvailable(boolean bool)
    {
        this.joinAllAvailable = bool;
    }

    public void setAutoJoinEnable(boolean bool)
    {
        this.autoJoin = bool;
    }

    public void addChannelToAutoJoinList(ChatChannel c)
    {
        if (!this.autoJoinList.contains(c.getName())) {
            this.autoJoinList.add(c.getName());
        }
    }

    public void removeChannelFromAutoJoinList(ChatChannel c)
    {
        if (this.autoJoinList.contains(c.getName())) {
            this.autoJoinList.remove(c.getName());
        }
    }

    public void clearAutoJoinList(ChatChannel c)
    {
        this.autoJoinList.clear();
    }

    public void setConversationWith(ChatPlayer cp)
    {
        this.converser = cp;
    }

    public ChatPlayer getConverser()
    {
        return this.converser;
    }

    public void ignore(Player p)
    {
        this.ignoreList.add(p.getName());
    }

    public void unignore(Player p)
    {
        if (isIgnoring(p)) {
            this.ignoreList.remove(p.getName());
        }
    }

    public boolean isIgnoring(Player p)
    {
        return this.ignoreList.contains(p.getName());
    }

    public boolean isIgnoring(ChatPlayer cp)
    {
        return isIgnoring(cp.getPlayer());
    }

    public void sendMessage(String message)
    {
        this.p.sendMessage(message);
    }

    public void sendMessages(String[] messages)
    {
        for (String str : messages) {
            sendMessage(str);
        }
    }
}