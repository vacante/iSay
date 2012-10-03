/*
 * Protocol.java
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


public abstract class Protocol {
    
    private final ProtocolCommandProcessor protocolCommandProcessor;
    
    public Protocol(ProtocolCommandProcessor processor)
    {
        this.protocolCommandProcessor = processor;
        this.protocolCommandProcessor.registerProtocols(protocolCommandProcessor);
    }
    
    public boolean dispatchToProtocol(ChatPlayer session, String message)
    {
        if (protocolCommandProcessor.isProtocolCommand(message)) {
            if (protocolCommandProcessor.commandSubscribesToParentProtocol(session, message)) {
                return protocolCommandProcessor.process(session, message);
            } else {
                return false;
            }
        } else {
            handleMessage(session, message);
            return true;
        }
               
    }
    
    public abstract void handleMessage(ChatPlayer session, String message);
}
