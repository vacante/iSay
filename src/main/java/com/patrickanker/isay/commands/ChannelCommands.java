/*
 * ChannelCommands.java
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
import com.patrickanker.lib.commands.Command;
import com.patrickanker.lib.commands.CommandPermission;
import java.util.Iterator;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChannelCommands {
    
    @Command(aliases={"join"},
            bounds={1,2},
            help="Join a channel using §c/join [channel]\n"
            + "§fIf you already ghost that channel,\n"
            + "you will change your chat focus to\n"
            + "the selected [channel].",
            playerOnly=true)
    @CommandPermission("isay.channels.join")
    public void join(CommandSender cs, String[] args) {
        Player p = (Player) cs;
        ChatPlayer cp = ISMain.getRegisteredPlayer(p);
        
        List<Channel> l = ISMain.getChannelManager().matchChannel(args[0]);
        
        if (l.isEmpty()) {
            p.sendMessage("§cNo channel found by that name.");
        } else if (l.size() > 1) {
            p.sendMessage("§cMultiple channels found by that name.");
        } else {
            ChatChannel cc = (ChatChannel) l.get(0);
            
            if (cc.hasListener(p.getName())) {
                ISMain.getChannelManager().getFocus(p.getName()).assignFocus(p.getName(), false);
                cc.assignFocus(p.getName(), true);
                p.sendMessage("§7Channel focus set to §a" + cc.getName());
            } else {
                if (args.length == 2) {
                    if (cp.canConnect(cc, args[1])) {
                        cc.connect(p.getName());
                    } else {
                        p.sendMessage("§cYou do not have access to that channel.");
                    }
                } else {
                   if (cp.canConnect(cc, "")) {
                        cc.connect(p.getName());
                    } else {
                        p.sendMessage("§cYou do not have access to that channel.");
                    } 
                }
            }
        }
    }
    
    @Command(aliases={"leave"},
            bounds={1,1},
            help="To leave a channel, simply type §c/leave [channel]",
            playerOnly=true)
    @CommandPermission("isay.channels.leave")
    public void leave(CommandSender cs, String[] args) {
        Player p = (Player) cs;
        ChatPlayer cp = ISMain.getRegisteredPlayer(p);
        
        List<Channel> l = ISMain.getChannelManager().matchChannel(args[0]);
        
        if (l.isEmpty()) {
            p.sendMessage("§cNo channel found by that name.");
        } else if (l.size() > 1) {
            p.sendMessage("§cMultiple channels found by that name.");
        } else {
            ChatChannel cc = (ChatChannel) l.get(0);
            
            if (cc.hasListener(p.getName())) {
                cc.disconnect(p.getName());
            } else {
                p.sendMessage("§cYou are not listening to that channel.");
            }
        }
    }
    
    @Command(aliases={"ghost"},
            bounds={1,2},
            help="To ghost a channel, simply type §c/ghost [channel]",
            playerOnly=true)
    @CommandPermission("isay.channels.ghost")
    public void ghost(CommandSender cs, String[] args) {
        Player p = (Player) cs;
        ChatPlayer cp = ISMain.getRegisteredPlayer(p);
        
        List<Channel> l = ISMain.getChannelManager().matchChannel(args[0]);
        
        if (l.isEmpty()) {
            p.sendMessage("§cNo channel found by that name.");
        } else if (l.size() > 1) {
            p.sendMessage("§cMultiple channels found by that name.");
        } else {
            ChatChannel cc = (ChatChannel) l.get(0);
            
            if (cc.hasListener(p.getName())) {
                p.sendMessage("§cYou are already listening to that channel.");
            } else {
                if (args.length == 2) {
                    if (cp.canConnect(cc, args[1])) {
                        cc.addListener(p.getName(), false);
                        p.sendMessage("§7Now listening to channel §a" + cc.getName());
                    } else {
                        p.sendMessage("§cYou do not have access to that channel.");
                    }
                } else {
                   if (cp.canConnect(cc, "")) {
                        cc.addListener(p.getName(), false);
                        p.sendMessage("§7Now listening to channel §a" + cc.getName());
                    } else {
                        p.sendMessage("§cYou do not have access to that channel.");
                    } 
                }
            }
        }
    }
    
    @Command(aliases={"cmute"},
            bounds={0,0},
            help="Toggle chat with §c/cmute",
            playerOnly=true)
    @CommandPermission("isay.channels.cmute")
    public void cmute(CommandSender cs, String[] args) {
        Player p = (Player) cs;
        ChatPlayer cp = ISMain.getRegisteredPlayer(p);
        
        cp.setMuted(!cp.isMuted());
        p.sendMessage("§7You have §a" + ((cp.isMuted()) ? "silenced" : "turned on") + " §7chat.");
    }
    
    @Command(aliases={"quickmessage", "qm"},
            bounds={2, -1},
            help="Send a message to a channel that you are\n"
            + "listening to with §c/qm [channel] (message...)",
            playerOnly=true)
    @CommandPermission("isay.channels.quickmessage")
    public void quickMessage(CommandSender cs, String[] args) {
        Player p = (Player) cs;
        ChatPlayer cp = ISMain.getRegisteredPlayer(p);
        
        List<Channel> l = ISMain.getChannelManager().matchChannel(args[0]);
        
        if (l.isEmpty()) {
            p.sendMessage("§cNo channel found by that name.");
        } else if (l.size() > 1) {
            p.sendMessage("§cMultiple channels found by that name.");
        } else {
            ChatChannel cc = (ChatChannel) l.get(0);
            
            if (!cc.hasListener(p.getName())) {
                p.sendMessage("§cYou are not listening to that channel.");
            } else {
                String concat = "";

                for (int i = 1; i < args.length; ++i) {
                    concat = concat + args[i] + " ";
                }
                
                cc.dispatch(cp, concat.trim());
            }
        }
    }
    
    @Command(aliases={"listchannels", "lc"},
            bounds={0,0},
            help="List all channels and your affliation with them using §c/lc\n"
            + "A §agreen §fname shows your focus channel, the one you chat into.\n"
            + "A §fwhite §fname shows a channel you are ghosting.\n"
            + "A §8dark grey §fname shows a channel you are not affiliated with.")
    @CommandPermission("isay.channels.list")
    public void listChannels(CommandSender cs, String[] args) {
        if (cs instanceof Player) {
            Player p = (Player) cs;
            ChatPlayer cp = ISMain.getRegisteredPlayer(p);
            
            List<Channel> l = ISMain.getChannelManager().getList();
            Iterator<Channel> it = l.listIterator();
            int i = 1;
            
            while (it.hasNext()) {
                ChatChannel cc = (ChatChannel) it.next();
                String foo;
                
                if (cc.hasListener(p.getName()) && cc.hasFocus(p.getName())) {
                    foo = "§a" + cc.getName();
                } else if (cc.hasListener(p.getName()) && !cc.hasFocus(p.getName())) {
                    foo = "§f" + cc.getName();
                } else {
                    foo = "§8" + cc.getName();
                }
                
                p.sendMessage("§f" + i + ".§8) " + foo);
                ++i;
            }
        } else {
            List<Channel> l = ISMain.getChannelManager().getList();
            Iterator<Channel> it = l.listIterator();
            int i = 1;
            
            while (it.hasNext()) {
                ChatChannel cc = (ChatChannel) it.next();
                
                cs.sendMessage("§f" + i + ".§8)§f " + cc.getName());
                ++i;
            }
        }
    }
}
