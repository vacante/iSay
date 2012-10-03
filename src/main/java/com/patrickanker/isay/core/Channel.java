/*
 * Channel.java
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

import com.patrickanker.isay.core.events.ChannelAddListenerEvent;
import com.patrickanker.isay.core.events.ChannelRemoveListenerEvent;

public interface Channel {
    
    //--------------------------------------------------------
    // Get name of channel
    //--------------------------------------------------------
    public String getName();
    
    //--------------------------------------------------------
    // Get type of channel
    //--------------------------------------------------------
    public Type getType();

    //--------------------------------------------------------
    // Add a listener
    //--------------------------------------------------------
    public void addListener(String name);

    //--------------------------------------------------------
    // Remove a listener
    //--------------------------------------------------------
    public void removeListener(String name);

    //--------------------------------------------------------
    // Checks if someone is a listener
    //--------------------------------------------------------
    public boolean isListener(String name);

    //--------------------------------------------------------
    // Find a listener
    //--------------------------------------------------------
    public String findListener(String name);
    
    //--------------------------------------------------------
    // Gets listener count
    //--------------------------------------------------------
    public int getListenerCount();

    //--------------------------------------------------------
    // Checks if a person can listen
    //--------------------------------------------------------
    public boolean canListen(String... args);
    
    //--------------------------------------------------------
    // Checks if a person can send messages to the channel
    //--------------------------------------------------------
    public boolean canMessage(String... args);
    
    //--------------------------------------------------------
    // Dispatches a message to the channel listeners
    //--------------------------------------------------------
    public void dispatch(String message, Object... args);
    
    //--------------------------------------------------------
    // Receives call for handling ChannelAddListener events
    //--------------------------------------------------------
    public void onAddListenerRequest(ChannelAddListenerEvent event);
    
    //--------------------------------------------------------
    // Receives call for handling ChannelRemoveListener events
    //--------------------------------------------------------
    public void onRemoveListenerRequest(ChannelRemoveListenerEvent event);
    
    public enum Type {
        
        BROADCAST,              // Broadcasts to everyone
        BROADCAST_TO_LISTENERS, // Broadcasts to a select few listeners
        PERMISSION_MESSAGE,     // Channel where permission is needed to speak
        OPEN_MESSAGE,           // Channel open for everyone to speak
        CUSTOM                  // Custom type
    }
}
