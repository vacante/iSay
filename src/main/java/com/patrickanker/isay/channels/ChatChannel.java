/*
 * ChatChannel.java
 *
 * Project: iSay
 *
 * Copyright (C) Patrick Anker 2011 - 2012. All rights reserved.
 * 
 * iSay by Patrick Anker is licensed under a Creative Commons 
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 */

package com.patrickanker.isay.channels;

import com.patrickanker.isay.ChatPlayer;
import com.patrickanker.isay.ISMain;
import com.patrickanker.isay.core.Channel;
import com.patrickanker.isay.core.protocol.ProtocolName;
import com.patrickanker.isay.core.events.ChannelAddListenerEvent;
import com.patrickanker.isay.core.events.ChannelRemoveListenerEvent;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@ProtocolName("chat")
public final class ChatChannel implements Channel {
    
    private final String name;
    private static final Type type = Type.PERMISSION_MESSAGE;
    
    private final List<String> listeners = new ArrayList<String>();
    
    private boolean focusable  = true;
    private boolean locked     = false;
    private boolean verbose    = true;
    
    
    
    public ChatChannel(String n)
    {
        this.name = n;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Type getType()
    {
        return type;
    }

    @Override
    public void addListener(String name)
    {
        if (!listeners.contains(name))
            listeners.add(name);
        
        if (verbose) {
            ChatPlayer cp = getChatPlayer(name);
            
            if (cp != null) {
                cp.tell("§7You started listening to \"§a" + getName() + "§7\"");
            }
        }
    }

    @Override
    public void removeListener(String name)
    {
        if (listeners.contains(name))
            listeners.remove(name);
        
        if (verbose) {
            ChatPlayer cp = getChatPlayer(name);
            
            if (cp != null) {
                cp.tell("§7You stopped listening to \"§a" + getName() + "§7\"");
            }
        }
    }

    @Override
    public boolean isListener(String name)
    {
        return listeners.contains(name);
    }

    @Override
    public String findListener(String name)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getListenerCount()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean canListen(String... args)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean canMessage(String... args)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dispatch(String message, Object... args)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onAddListenerRequest(ChannelAddListenerEvent event)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onRemoveListenerRequest(ChannelRemoveListenerEvent event)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private ChatPlayer getChatPlayer(String name)
    {
        Player foo = Bukkit.getPlayer(name);
        
        if (foo == null)
            return null;
        else
            return ISMain.getRegisteredPlayer(foo);
    }
    
}
