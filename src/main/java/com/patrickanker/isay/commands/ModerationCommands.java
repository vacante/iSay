/*
 * ModerationCommands.java
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
import com.patrickanker.isay.MessageFormattingServices;
import com.patrickanker.isay.MuteServices;
import com.patrickanker.lib.commands.Command;
import com.patrickanker.lib.commands.CommandPermission;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ModerationCommands {

    @Command(aliases={"mute"},
            bounds={1, -1},
            help="§c/mute <player> (reason) §fmutes a player with an optional reason\n"
            + "You can put in the argument \"-si\" in the reason to not broadcast the mute\n"
            + "§c/mute -t <time> <player> (reason) §fwill mute the player for at least the amount of time specified")
    @CommandPermission("isay.moderation.mute")
    public void mute(CommandSender cs, String[] args)
    {
        boolean broadcast = true;
        
        if (args[0].equalsIgnoreCase("-t") && MessageFormattingServices.isDate(args[1])) {
            List<Player> l = Bukkit.matchPlayer(args[2]);
            
            if (l.isEmpty()) {
                cs.sendMessage("§cNo player found with that name.");
            } else if (l.size() > 1) {
                cs.sendMessage("§cMultiple players found with that name.");
            } else {
                ChatPlayer cp = ISMain.getRegisteredPlayer(l.get(0));
                cp.setMuted(!cp.isMuted());
                
                if (cp.isMuted()) {
                    cp.setMuteTimeout(MessageFormattingServices.getDateString(args[1]));
                    
                    if (args.length >= 4) {
                        String concat = "";
                        
                        for (int i = 3; i < args.length; ++i) {
                            if (args[i].equalsIgnoreCase("-si")) {
                                broadcast = false;
                                break;
                            }
                            
                            concat += args[i] + " ";
                        }
                        
                        concat = concat.trim();
                        
                        if (broadcast) {
                            MuteServices.broadcastMute(cs.getName(), cp.getPlayer().getName(), concat);
                        }
                        
                    } else {
                        MuteServices.broadcastMute(cs.getName(), cp.getPlayer().getName());
                    }
                } else {
                    cp.setMuteTimeout("");
                    cs.sendMessage("§a" + cp.getPlayer().getName() + " §7has been unmuted.");
                    MuteServices.unmuteAnnounce(cp);
                }
            }
        } else if (args.length >= 1) {
            List<Player> l = Bukkit.matchPlayer(args[0]);
            
            if (l.isEmpty()) {
                cs.sendMessage("§cNo player found with that name.");
            } else if (l.size() > 1) {
                cs.sendMessage("§cMultiple players found with that name.");
            } else {
                ChatPlayer cp = ISMain.getRegisteredPlayer(l.get(0));
                cp.setMuted(!cp.isMuted());
                
                if (cp.isMuted()) {
                    if (args.length >= 2) {
                        String concat = "";
                        
                        for (int i = 1; i < args.length; ++i) {
                            if (args[i].equalsIgnoreCase("-si")) {
                                broadcast = false;
                                break;
                            }
                            
                            concat += args[i] + " ";
                        }
                        
                        concat = concat.trim();
                        
                        if (broadcast) {
                            MuteServices.broadcastMute(cs.getName(), cp.getPlayer().getName(), concat);
                        }
                        
                    } else {
                        MuteServices.broadcastMute(cs.getName(), cp.getPlayer().getName());
                    }
                } else {
                    cs.sendMessage("§a" + cp.getPlayer().getName() + " §7has been unmuted.");
                    MuteServices.unmuteAnnounce(cp);
                }
            }
        } else {
            cs.sendMessage("§cIncorrect format. See \"/mute help\"");
        }
    }
}
