/*
 * ChannelEvent.java
 *
 * Project: iSay
 *
 * Copyright (C) Patrick Anker 2011 - 2012. All rights reserved.
 * 
 * iSay by Patrick Anker is licensed under a Creative Commons 
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 */

package com.patrickanker.isay.core.events;

import com.patrickanker.isay.core.Channel;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class ChannelEvent extends Event implements Cancellable {
    protected static final HandlerList handlers = new HandlerList();
    protected boolean cancelled = false;
    
    protected final Channel channel;

    public ChannelEvent(Channel channel)
    {
        this.channel = channel;
    }
    
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
    
    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean bln)
    {
        cancelled = bln;
    }
    
    public Channel getChannel()
    {
        return channel;
    }
}
