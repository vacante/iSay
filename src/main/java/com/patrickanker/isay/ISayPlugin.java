/*
 * ISayPlugin.java
 *
 * Project: iSay
 *
 * Copyright (C) Patrick Anker 2011 - 2012. All rights reserved.
 * 
 * iSay by Patrick Anker is licensed under a Creative Commons 
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 */

package com.patrickanker.isay;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


public class ISayPlugin extends JavaPlugin implements Listener {
    
    private final List<ChatPlayer> chatPlayers = new ArrayList<ChatPlayer>();
    
    @Override
    public void onEnable()
    {
        
    }
}
