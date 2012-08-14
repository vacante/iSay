package com.thevoxelbox.isay;

import com.patrickanker.lib.permissions.PermissionsManager;
import com.thevoxelbox.isay.channels.Channel;
import com.thevoxelbox.isay.channels.ChatChannel;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.entity.Player;

public class ChatPlayer {

    private final Player p;
    private boolean adminMute = false;
    private String format = "$group";
    private String groupFormat = "$name:";
    private boolean ping = true;
    private boolean joinAllAvailable = false;
    private boolean autoJoin = false;
    private List<String> autoJoinList = new LinkedList();
    private List<String> ignoreList = new LinkedList();
    private boolean muted = false;
    private ChatPlayer converser;

    public ChatPlayer(Player p)
    {
        this.p = p;
        load();
    }

    public void reload()
    {
        save();
        load();
    }

    public void forceReload()
    {
        load();
    }

    private void load()
    {
        if (ISMain.getPlayerConfig().contains(this.p.getName() + ".adminmute")) {
            this.adminMute = ISMain.getPlayerConfig().getBoolean(this.p.getName() + ".adminmute");
        }
        if (ISMain.getPlayerConfig().contains(this.p.getName() + ".format")) {
            this.format = ISMain.getPlayerConfig().getString(this.p.getName() + ".format");
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
        ISMain.getPlayerConfig().set(this.p.getName() + ".adminmute", Boolean.valueOf(this.adminMute));
        ISMain.getPlayerConfig().set(this.p.getName() + ".ping", Boolean.valueOf(this.ping));
        ISMain.getPlayerConfig().set(this.p.getName() + ".joinallavailable", Boolean.valueOf(this.joinAllAvailable));
        ISMain.getPlayerConfig().set(this.p.getName() + ".autojoinlistenable", Boolean.valueOf(this.autoJoin));
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
        if (PermissionsManager.getHandler().hasPermission(this.p.getName(), "isay.admin")) {
            return true;
        }
        if (ISMain.getConfigData().getBoolean("disable-crossworld-chat")) {
            return (PermissionsManager.getHandler().hasPermission(this.p.getWorld().getName(), this.p.getName(), "isay.channel." + channel.getName().toLowerCase() + ".join"))
                    && (password.equals(((ChatChannel) channel).getPassword()));
        }

        return (PermissionsManager.getHandler().hasPermission(this.p.getName(), "isay.channel." + channel.getName().toLowerCase() + ".join"))
                && (password.equals(((ChatChannel) channel).getPassword()));
    }

    public boolean isMuted()
    {
        return this.muted;
    }

    public void setMuted(boolean bool)
    {
        this.muted = bool;
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
        } catch (NullPointerException ex) {
        }
        return this.groupFormat;
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
        this.p.sendMessage(messages);
    }
}