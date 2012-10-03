/*
 * ChannelRemoveListenerEvent.java
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


public final class ChannelRemoveListenerEvent extends ChannelEvent {

    private final String listener;
    
    public ChannelRemoveListenerEvent(Channel channel, String oldListener)
    {
        super(channel);
        this.listener = oldListener;
    }
    
    public String getListener()
    {
        return listener;
    }
}
