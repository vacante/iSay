/*
 * ChatPlayer.java
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

import com.patrickanker.isay.core.protocol.ProtocolNetServerHandler;
import com.patrickanker.isay.core.Messenger;
import com.patrickanker.isay.core.exceptions.ChannelException;
import com.patrickanker.isay.core.protocol.ProtocolName;
import com.patrickanker.lib.logging.ConsoleLogger;
import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;


public final class ChatPlayer implements Messenger {
    
    private final Player player;
    
    private ProtocolName activeProtocol;
    
    // Settings getters and setters
    private boolean protocolMessagesOnly = false;
    
    private static final File playerSettingsFile = new File("plugins/iSay/player-settings.yml");

    public ChatPlayer(Player player)
    {
        this.player = player;
    }

    public void tell(String string)
    {
        player.sendMessage(ProtocolNetServerHandler.PROTOCOL_STRING + string);
    }

    public void tell(String[] strings)
    {
        for (String str : strings) {
            tell(str);
        }
    }

    @Override
    public void listen(String channel) throws ChannelException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void message(String message) throws ChannelException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void message(String message, Object target) throws ChannelException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public ProtocolName getActiveProtocol()
    {
        return activeProtocol;
    }
    
    public void setActiveProtocol(ProtocolName proto)
    {
        activeProtocol = proto;
    }
    
    public Player getPlayer()
    {
        return player;
    }
    
    // Settings getters and setters
    public boolean onlyProtocolMessages()
    {
        return protocolMessagesOnly;
    }
    
    public void setProtocolMessagesOnly(boolean bool)
    {
        protocolMessagesOnly = bool;
    }
    
    // Settings load and save
    
    public void load()
    {
        YamlConfiguration yamlConfig = new YamlConfiguration();
        
        try {
            yamlConfig.load(playerSettingsFile);
            
            protocolMessagesOnly = yamlConfig.getBoolean(player.getName() + ".protocolmessagesonly");
        } catch (Throwable t) {
            ConsoleLogger.getLogger("iSay").log("Could not load player settings file", 2);
        }
    }
    
    public void save()
    {
        YamlConfiguration yamlConfig = new YamlConfiguration();
        
        try {
            yamlConfig.set(player.getName() + ".protocolmessagesonly", protocolMessagesOnly);
            
            yamlConfig.save(playerSettingsFile);
        } catch (Throwable t) {
            ConsoleLogger.getLogger("iSay").log("Could not load player settings file", 2);
        }
    }
}
