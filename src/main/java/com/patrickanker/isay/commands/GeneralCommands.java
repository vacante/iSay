/*
 * GeneralCommands.java
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
import com.patrickanker.isay.core.Channel;
import com.patrickanker.isay.channels.ChatChannel;
import com.patrickanker.isay.formatters.MessageFormatter;
import com.patrickanker.lib.commands.Command;
import com.patrickanker.lib.commands.CommandPermission;
import com.patrickanker.lib.commands.Subcommands;
import com.patrickanker.lib.permissions.PermissionsManager;
import com.patrickanker.lib.util.Formatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GeneralCommands {
    
    @Command(aliases={"format", "f"},
            bounds={2, -1})
    @Subcommands(arguments={"-p", 
                            "-g", 
                            "-cgf"},
            permission={"isay.general.format.players", 
                        "isay.general.format.groups", 
                        "isay.general.format.channels"})
    public void format(CommandSender cs, String[] args) {
        if (args[0].equalsIgnoreCase("-p")) {
            List<Player> l = Bukkit.matchPlayer(args[1]);
            
            if (l.isEmpty()) {
                cs.sendMessage("§cNo player found with that name.");
            } else if (l.size() > 1) {
                cs.sendMessage("§cMultiple players found with that name.");
            } else {
                ChatPlayer cp = ISMain.getRegisteredPlayer(l.get(0));
                String concat = "";
                
                for (int i = 2; i < args.length; ++i) {
                    concat = concat + args[i] + " ";
                }
                
                cp.setFormat(concat.trim());
                
                cs.sendMessage("§8==============================");
                cs.sendMessage("Formatted §a" + cp.getPlayer().getName() + "§f's name to§8:");
                cs.sendMessage(cp.getFormat());
                cs.sendMessage("§6Preview of new format§8:");
                cs.sendMessage(Formatter.selectFormatter(MessageFormatter.class).formatMessage("Hello world.", cp));
                cs.sendMessage("§8==============================");
                
            }
        } else if (args[0].equalsIgnoreCase("-g")) {
            List<String> l = new ArrayList<String>();
            Iterator<String> it = ISMain.getGroupManager().getRegisteredGroups().listIterator();
            
            while (it.hasNext()) {
                String group = it.next();
                
                if (group.toLowerCase().startsWith(args[1].toLowerCase())) {
                    l.add(group);
                }
            }
            
            if (l.isEmpty()) {
                cs.sendMessage("§cNo group found with that name.");
            } else if (l.size() > 1) {
                cs.sendMessage("§cMultiple groups found with that name.");
            } else {
                String concat = "";
                
                for (int i = 2; i < args.length; ++i) {
                    concat = concat + args[i] + " ";
                }
                
                ISMain.getGroupManager().getGroupConfiguration(l.get(0)).setString("format", concat.trim());
                
                cs.sendMessage("§8==============================");
                cs.sendMessage("Formatted the §a" + l.get(0) + "§f group's name format to§8:");
                cs.sendMessage(concat.trim());
                cs.sendMessage("§8==============================");
            }
            
        } else if (args[0].equalsIgnoreCase("-cgf")) {
            List<Channel> l = ISMain.getChannelManager().matchChannel(args[1]);
            
            if (l.isEmpty()) {
                cs.sendMessage("§cNo channel found with that name.");
            } else if (l.size() > 1) {
                cs.sendMessage("§cMultiple channels found with that name.");
            } else {
                ChatChannel cc = (ChatChannel) l.get(0);
                String concat = "";
                
                for (int i = 2; i < args.length; ++i) {
                    concat = concat + args[i] + " ";
                }
                
                cc.setGhostFormat(concat.trim());
                
                cs.sendMessage("§8==============================");
                cs.sendMessage("Formatted the §a" + l.get(0).getName() + "§f channel's name format to§8:");
                cs.sendMessage(cc.getGhostFormat());
                cs.sendMessage("§8==============================");
            }
        }
    }
    
    @Command(aliases={"helpop", "/."},
            bounds={1, -1},
            help="Send a message to your server administrators with §c/helpop",
            playerOnly=true)
    @CommandPermission("isay.general.helpop.send")
    public void helpop(CommandSender cs, String[] args) {
        Player p = (Player) cs;
        ChatPlayer cp = ISMain.getRegisteredPlayer(p);

        if (!PermissionsManager.getHandler().hasPermission(p.getName(), "isay.general.helpop.read")) {
            String concat = "";

            for (int i = 0; i < args.length; ++i) {
                concat = concat + args[i] + " ";
            }

            ChatChannel helpop = (ChatChannel) ISMain.getChannelManager().getHelpOpChannel();

            if (helpop == null) {
                p.sendMessage("§cHelpOp has not been configured on this server.");
            } else {
                helpop.dispatch(cp, concat.trim());
                p.sendMessage("§cYou have sent the following message to HelpOp:");
                p.sendMessage("§6" + concat.trim());
            }
        } else {
            ChatChannel helpop = (ChatChannel) ISMain.getChannelManager().getHelpOpChannel();
            
            String concat = "";

            for (int i = 0; i < args.length; ++i) {
                concat = concat + args[i] + " ";
            }

            if (helpop == null) {
                p.sendMessage("§cHelpOp has not been configured on this server.");
            } else {
                helpop.dispatch(cp, concat.trim());
            }  
        }
    }
}
