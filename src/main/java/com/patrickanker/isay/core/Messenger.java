/*
 * Messenger.java
 *
 * Project: iSay
 *
 * Copyright (C) Patrick Anker 2011 - 2012. All rights reserved.
 * 
 * iSay by Patrick Anker is licensed under a Creative Commons 
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 */

package com.patrickanker.isay.core;

import com.patrickanker.isay.core.exceptions.ChannelException;

public interface Messenger {
    
    //--------------------------------------------------------
    // Attempt to listen to a channel
    //--------------------------------------------------------
    public void listen(String channel) throws ChannelException;
    
    //--------------------------------------------------------
    // Attempt to message the focus channel, if applicable
    //--------------------------------------------------------
    public void message(String message) throws ChannelException;
    
    //--------------------------------------------------------
    // Attempt to message a specified channel
    //--------------------------------------------------------
    public void message(String message, Object target) throws ChannelException;

}
