/*
 * ChannelRegisterEvent.java
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

public final class ChannelRegisterEvent extends ChannelEvent {

    public ChannelRegisterEvent(Channel channel)
    {
        super(channel);
    }
}
