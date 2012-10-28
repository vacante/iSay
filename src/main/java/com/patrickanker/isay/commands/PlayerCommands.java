/*
 * PlayerCommands.java
 *
 * Project: iSay
 *
 * Copyright (C) Patrick Anker 2011 - 2012. All rights reserved.
 *
 * iSay by Patrick Anker is licensed under a Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 */

package com.patrickanker.isay.commands;

import com.patrickanker.isay.ChatPlayer;
import com.patrickanker.isay.ISMain;
import com.patrickanker.isay.channels.Channel;
import com.patrickanker.isay.channels.ChatChannel;
import com.patrickanker.lib.commands.Command;
import com.patrickanker.lib.commands.CommandPermission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PlayerCommands {

    @Command(aliases = {"autojoin"},
             bounds = {0, 1},
             help = "§c/autojoin §ftoggles if you wish to enable autojoin\n"
                    + "§c/autojoin <channel> §fwill toggle if you wish to autojoin that channel or not (if you can)\n"
                    + "§c/autojoin all §fwill toggle if you wish to autojoin all channels that you can join\n"
                    + "§c/autojoin [list, -l] §fshows your autojoin list",
             playerOnly = true)
    @CommandPermission("isay.players.autojoin")
    public void autojoin(CommandSender cs, String[] args)
    {
        ChatPlayer cp = ISMain.getRegisteredPlayer((Player) cs);

        if (args.length == 0) {
            cp.setAutoJoinEnable(!cp.hasAutoJoin());
            cp.sendMessage("§7Autojoin is " + (cp.hasAutoJoin() ? "§aenabled" : "§6disabled") + "§7.");
            return;
        }

        if (args[0].equalsIgnoreCase("all")) {
            cp.setJoinAllAvailable(!cp.isJoinAllAvailableEnabled());
            cp.sendMessage("§7You " + (cp.isJoinAllAvailableEnabled() ? "§awill" : "§6will not") + " §7auto-join all channels that you can join.");
        } else if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("-l")) {
            if (cp.isJoinAllAvailableEnabled()) {
                cp.sendMessage("§7You are auto-joining §aevery possible channel you can join.");
            } else {
                if (cp.getAutoJoinList().isEmpty()) {
                    cp.sendMessage("§cAuto-join list is empty.");
                    return;
                }

                List<String> ajl = cp.getAutoJoinList();
                Collections.sort(ajl);

                cp.sendMessage("§8====================");
                cp.sendMessage("§6Auto-join List");
                cp.sendMessage("§8");

                for (String channel : ajl) {
                    cp.sendMessage("§8- §7" + channel);
                }

                cp.sendMessage("§8====================");
            }
        } else {
            List<Channel> l = ISMain.getChannelManager().matchChannel(args[0]);

            if (l.isEmpty()) {
                cp.sendMessage("§cNo channel found with that name.");
            } else if (l.size() > 1) {
                cp.sendMessage("§cMultiple channels found with that name.");
            } else {
                ChatChannel cc = (ChatChannel) l.get(0);

                if (!cp.getAutoJoinList().contains(cc.getName())) {
                    cp.getAutoJoinList().add(cc.getName());
                    cp.sendMessage("§7Added channel §a" + cc.getName() + " §7to auto-join list.");
                } else {
                    cp.getAutoJoinList().remove(cc.getName());
                    cp.sendMessage("§7Removed channel §a" + cc.getName() + " §7from auto-join list.");
                }
            }
        }
    }
}
