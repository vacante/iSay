/*
 * ISayManager.java
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

import java.util.LinkedList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class ISayManager {
    
    private static ISayManager instance;
    
    private ISayPlugin plugin;
    private final List<ChatPlayer> chatPlayers = new LinkedList<ChatPlayer>();
    
    public static void initManager(Plugin plugin)
    {
        if (!(plugin instanceof ISayPlugin))
            return;
        
        instance = new ISayManager();
        instance.plugin = (ISayPlugin) plugin;
        
        
    }
    
    public static ISayManager getManager()
    {
        return instance;
    }
    
    public ChatPlayer registerChatPlayer(Player player)
    {
        if (!isChatPlayerRegistered(player)) {
            ChatPlayer chatPlayer = new ChatPlayer(player);
            return chatPlayer;
        }
        
        return getChatPlayer(player);
    }
    
    public void unregisterChatPlayer(ChatPlayer chatPlayer)
    {
        if (isChatPlayerRegistered(chatPlayer)) {
            chatPlayers.remove(chatPlayer);
        }
    }
    
    public boolean isChatPlayerRegistered(Player player)
    {
        for (ChatPlayer chatPlayer : chatPlayers) {
            if (chatPlayer.getPlayer().getName().equals(player.getName()))
                return true;
        }
        
        return false;
    }
    
    public boolean isChatPlayerRegistered(ChatPlayer chatPlayer)
    {
        for (ChatPlayer cp : chatPlayers) {
            if (chatPlayer.getPlayer().getName().equals(chatPlayer.getPlayer().getName()))
                return true;
        }
        
        return false;
    }
    
    public List<ChatPlayer> getChatPlayers()
    {
        return chatPlayers;
    }
    
    public ChatPlayer getChatPlayer(Player player)
    {
        for (ChatPlayer chatPlayer : chatPlayers) {
            if (chatPlayer.getPlayer().getName().equals(player.getName()))
                return chatPlayer;
        }
        
        return null;
    }
}
