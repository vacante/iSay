/*
 * ChannelMessageEvent.java
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

public final class ChannelMessageEvent extends ChannelEvent {
    
    private String message;
    private final String messenger;

    public ChannelMessageEvent(Channel channel, String message, String messenger)
    {
        super(channel);
        this.message = message;
        this.messenger = messenger;
    }
    
    public void setMessage(String msg)
    {
        message = msg;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public String getMessenger()
    {
        return messenger;
    }
}
