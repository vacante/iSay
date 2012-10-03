/*
 * ProtocolCommandProcessor.java
 *
 * Project: iSay
 *
 * Copyright (C) Patrick Anker 2011 - 2012. All rights reserved.
 * 
 * iSay by Patrick Anker is licensed under a Creative Commons 
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 */

package com.patrickanker.isay.core.protocol;

import com.patrickanker.isay.ChatPlayer;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class ProtocolCommandProcessor {
    
    private final Protocol protocol;
    private final Map<String, Method> protocolCommands = new HashMap<String, Method>();
    private Object owningProcessor = null;
    
    protected ProtocolCommandProcessor(Protocol parent)
    {
        protocol = parent;
    }
    
    protected boolean isProtocolCommand(String message)
    {
        boolean _isProtocolCommand = false;
        
        Pattern externalProtocolCallPattern = Pattern.compile("\\:\\w{2,20}");
        Matcher ex = externalProtocolCallPattern.matcher(message);
        
        if (ex.find(0)) {
            String str = ex.group(0);
            _isProtocolCommand = message.startsWith(str);
        } else {
            Pattern internalProtocolCallPattern = Pattern.compile("\\:\\:\\s");
            Matcher in = internalProtocolCallPattern.matcher(message);
            
            if (in.find(0)) {
                String str = in.group(0);
                _isProtocolCommand = message.startsWith(str);
            }
        }
        
        return _isProtocolCommand;
    }
    
    protected boolean commandSubscribesToParentProtocol(ChatPlayer session, String message)
    {
        ProtocolName channelProtocol = protocol.getClass().getAnnotation(ProtocolName.class);
        
        if (message.startsWith(":: ") && channelProtocol.value().equals(session.getActiveProtocol().value()))
            return true;
        
        String[] split = message.split(" ");
        String _protocolName = split[0].replace(":", "");
        
        if (channelProtocol.value().equals(_protocolName))
            return true;
        
        return false;
        
    }
    
    public boolean process(ChatPlayer session, String message)
    {
        if (!isProtocolCommand(message))
            return false;
        
        if (!commandSubscribesToParentProtocol(session, message))
            return false;
        
        String[] split = message.split(" ");
        
        return invokeMethod(split[1], session, Arrays.copyOfRange(split, 2, message.split(" ").length - 1));
    }
    
    public void registerProtocols(Object obj)
    {
        if (!(obj instanceof ProtocolCommandProcessor))
            return;
        
        for (Method method : obj.getClass().getDeclaredMethods()) {
            
            if (!method.isAnnotationPresent(ProtocolCommand.class))
                continue;
            
            ProtocolCommand pc = method.getAnnotation(ProtocolCommand.class);
            
            if (owningProcessor == null)
                owningProcessor = obj;
            
            if (!protocolCommands.containsKey(pc.value())) {
                protocolCommands.put(pc.value(), method);
            }
        }
    }
    
    private boolean invokeMethod(final String protocolCommand, final ChatPlayer session, final String[] args)
    {
        Method method = protocolCommands.get(protocolCommand);
        
        try {
            method.invoke(owningProcessor, session, args);
        } catch (Throwable t) {
            return false;
        }
        
        return true;
    }
}
