/*
 * MessageDeniedException.java
 *
 * Project: iSay
 *
 * Copyright (C) Patrick Anker 2011 - 2012. All rights reserved.
 * 
 * iSay by Patrick Anker is licensed under a Creative Commons 
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 */

package com.patrickanker.isay.core.exceptions;


public class MessageDeniedException extends ChannelException {
    private static final long serialVersionUID = -8518723771L;
    
    public MessageDeniedException(String error)
    {
        super(error);
    }
}
