package com.thevoxelbox.isay.channels;

import com.thevoxelbox.isay.ChatPlayer;
import com.thevoxelbox.isay.ISMain;
import java.util.HashMap;

public abstract class Channel {

    protected String name;
    protected HashMap<String, Boolean> listeners = new HashMap();
    protected ISMain is = ISMain.getInstance();
    protected ChannelManager channelManager = ISMain.getChannelManager();

    protected Channel(String str)
    {
        this.name = str;
    }

    public void addListener(String player)
    {
        if (!this.listeners.containsKey(player)) {
            this.listeners.put(player, Boolean.valueOf(false));
        }
    }

    public void addListener(String player, boolean focus)
    {
        if (!this.listeners.containsKey(player)) {
            this.listeners.put(player, Boolean.valueOf(focus));
        }
    }

    public boolean hasListener(String player)
    {
        return this.listeners.containsKey(player);
    }

    public boolean hasFocus(String player)
    {
        return (hasListener(player)) && (((Boolean) this.listeners.get(player)).booleanValue());
    }

    public void assignFocus(String player, boolean focus)
    {
        if (this.listeners.containsKey(player)) {
            this.listeners.put(player, Boolean.valueOf(focus));
        }
    }

    public void removeListener(String player)
    {
        if (this.listeners.containsKey(player)) {
            this.listeners.remove(player);
        }
    }

    public String getName()
    {
        String ret = this.name;
        return ret;
    }

    protected void setName(String str)
    {
        this.name = str;
    }

    public abstract void connect(String player);

    public abstract void dispatch(ChatPlayer player, String message);

    public abstract void disconnect(String player);

    public abstract void load();

    public abstract void dump();
}