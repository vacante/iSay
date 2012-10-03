/*
 * DispatchControlCenter.java
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

import com.patrickanker.isay.core.protocol.ProtocolName;
import com.patrickanker.isay.core.events.ChannelMessageEvent;
import com.patrickanker.isay.core.events.ChannelRegisterEvent;
import com.patrickanker.isay.core.events.ChannelUnregisterEvent;
import com.patrickanker.isay.core.exceptions.IncorrectChannelTypeException;
import com.patrickanker.lib.logging.ConsoleLogger;
import com.patrickanker.lib.util.MutableDictionary;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;


public final class DispatchControlCenter {
    
    private final Map<Class<?>, List<Object>> registeredChannels = new HashMap<Class<?>, List<Object>>();
    private final List<Class<?>> registeredTypes = new LinkedList<Class<?>>();
    
    private final MutableDictionary aliases = new MutableDictionary();
    
    public void registerProtocols(Class<?>... clazzes)
    {
        for (Class<?> clazz : clazzes) {
            try {
                registerType(clazz);
            } catch (IncorrectChannelTypeException ex) {
                ConsoleLogger.log("Attempted to register incorrectly implemented channel type", 1);
            }
        }
    }
    
    public void registerType(Class<?> cls) throws IncorrectChannelTypeException
    {
        // Check if type is already registered
        if (registeredTypes.contains(cls))
            return;
        
        // Check if type implements Channel
        if (!cls.isAssignableFrom(Channel.class))
            throw new IncorrectChannelTypeException();
        
        // Check if type is available for I/O
        if (!cls.isAnnotationPresent(ProtocolName.class))
            throw new IncorrectChannelTypeException();
        
        registeredTypes.add(cls);
    }
    
    public void registerChannels(Object... args)
    {
        for (Object obj : args) {
            registerChannel(obj);
        }
    }
    
    public void registerChannel(Object obj)
    {
        Class cls = obj.getClass();
        
        // Check if type is registered
        if (!registeredTypes.contains(cls))
            return;
        
        // Given previous check, assert that type has been checked
        ProtocolName type = (ProtocolName) cls.getAnnotation(ProtocolName.class);
        assert type != null;
        
        // Check if channel is already registered
        if (getRegisteredChannelsForType(cls).contains(obj))
            return;
        
        // CONFIRM WITH EXTERNAL SYSTEMS
        ChannelRegisterEvent event = new ChannelRegisterEvent((Channel) obj);
        Bukkit.getPluginManager().callEvent(event);
        
        if (event.isCancelled())
            return;
        
        registeredChannels.put(cls, injectChannelForType(obj, cls));
        aliases.put(type.value(), obj);
    }
    
    public void unregisterChannel(String name)
    {
        if (!aliases.hasKey(name))
            return;
        
        Object obj = aliases.get(name);
        
        // CONFIRM WITH EXTERNAL SYSTEMS
        ChannelUnregisterEvent event = new ChannelUnregisterEvent((Channel) obj);
        Bukkit.getPluginManager().callEvent(event);
        
        if (event.isCancelled())
            return;
        
        registeredChannels.put(obj.getClass(), removeChannelForType(obj, obj.getClass()));
        aliases.remove(name);
    }
    
    public void routeMessage(String destination, String messenger, String message, Object... args)
    {
        if (aliases.hasKey(destination)) {
            Channel channelDest = (Channel) aliases.get(destination);
            
            // CONFIRM WITH EXTERNAL SYSTEMS
            ChannelMessageEvent event = new ChannelMessageEvent(channelDest, message, messenger);
            Bukkit.getPluginManager().callEvent(event);
            
            if (event.isCancelled())
                return;
            
            if (args == null)
                channelDest.dispatch(message);
            else
                channelDest.dispatch(message, args);
        }
    }
    
    private List<Object> getRegisteredChannelsForType(Class<?> type)
    {
        if (!registeredChannels.containsKey(type))
            return null;
        
        return registeredChannels.get(type);
    }
    
    private List<Object> injectChannelForType(Object obj, Class<?> type)
    {
        if (!registeredChannels.containsKey(type))
            return null;
        
        List<Object> l = registeredChannels.get(type);
        l.add(obj);
        
        return l;
    }
    
    private List<Object> removeChannelForType(Object obj, Class<?> type)
    {
        if (!registeredChannels.containsKey(type))
            return null;
        
        List<Object> l = registeredChannels.get(type);
        l.remove(obj);
        
        return l;
    }
}
