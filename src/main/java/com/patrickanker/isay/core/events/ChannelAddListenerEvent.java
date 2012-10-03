/*
 * ChannelAddListenerEvent.java
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

public final class ChannelAddListenerEvent extends ChannelEvent {

    private final String listener;
    
    public ChannelAddListenerEvent(Channel channel, String newListener)
    {
        super(channel);
        this.listener = newListener;
    }
    
    public String getListener()
    {
        return listener;
    }
}
