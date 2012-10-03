/*
 * ProtocolNetServerHandler.java
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

import com.patrickanker.isay.ISayManager;
import net.minecraft.server.*;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;


public class ProtocolNetServerHandler extends NetServerHandler {
    
    public static final String PROTOCOL_STRING = "\u00a7b\u00a7d\u00a70\u00a7b0\u00a7b\u00a7d\u00a70\u00a7bf";
    
    private CraftPlayer cp;
    
    private CraftServer server;
    private MinecraftServer minecraftServer;
    
    public ProtocolNetServerHandler(MinecraftServer server, INetworkManager networkManager, CraftPlayer craftPlayer, EntityPlayer entityPlayer)
    {
        super(server, networkManager, entityPlayer);
        
        this.minecraftServer = server;
        this.networkManager = networkManager;
        this.player = entityPlayer;
        this.cp = craftPlayer;
        
        networkManager.a(this);
        entityPlayer.netServerHandler = this;
        
        this.server = minecraftServer.server;
    }
    
    @Override
    public void sendPacket(Packet packet)
    {
        if (packet instanceof Packet3Chat) {
            Packet3Chat chat = (Packet3Chat) packet;
            
            if (ISayManager.getManager().getChatPlayer(cp).onlyProtocolMessages()) {
                if (chat.message.startsWith(PROTOCOL_STRING)) {
                    chat.message = chat.message.replace(PROTOCOL_STRING, "");
                    super.sendPacket(chat);
                }
            } else {
                super.sendPacket(packet);
            }
        } else {
            super.sendPacket(packet);
        }
    }
}
