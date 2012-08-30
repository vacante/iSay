package com.thevoxelbox.isay.listeners;

import com.thevoxelbox.isay.ChatPlayer;
import com.thevoxelbox.isay.ISMain;
import com.thevoxelbox.isay.channels.Channel;
import com.thevoxelbox.isay.channels.ChatChannel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        if (event.isCancelled()) {
            return;
        }

        if (event.getMessage().startsWith("u00a7")) {
            event.setCancelled(true);
            return;
        }

        if (event.getPlayer().getItemInHand() != null) {
            String prefix = ISMain.getItemAliasManager().getAliasForItem(event.getPlayer().getItemInHand().getTypeId());

            if (prefix != null) {
                event.getPlayer().chat(prefix + " " + event.getMessage());
                event.setCancelled(true);
                return;
            }
        }

        Channel channel = ISMain.getChannelManager().getFocus(event.getPlayer().getName());

        if (channel != null) {
            ChatPlayer cp = ISMain.getRegisteredPlayer(event.getPlayer());

            ChatChannel cc = (ChatChannel) channel;
            cc.dispatch(cp, event.getMessage());
            event.setCancelled(true);
        } else {
            event.getPlayer().sendMessage("§cYou do not have a channel focus.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        ISMain.getGroupManager().verifyPlayerGroupExistence(event.getPlayer());

        ChatPlayer cp = ISMain.registerPlayer(event.getPlayer());
        ISMain.getChannelManager().onPlayerLogin(cp);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        ChatPlayer cp = ISMain.getRegisteredPlayer(event.getPlayer());

        ISMain.getChannelManager().disconnectFromAllChannels(cp);
        ISMain.unregisterPlayer(event.getPlayer());
        cp.save();
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event)
    {
        ChatPlayer cp = ISMain.getRegisteredPlayer(event.getPlayer());

        ISMain.getChannelManager().disconnectFromAllChannels(cp);
        ISMain.unregisterPlayer(event.getPlayer());
        cp.save();
    }
}