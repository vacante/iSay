/*
 * MessagingCommands.java
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
import com.patrickanker.isay.formatters.ConsoleMessageFormatter;
import com.patrickanker.isay.formatters.SingleLineBroadcastFormatter;
import com.patrickanker.lib.commands.Command;
import com.patrickanker.lib.commands.CommandPermission;
import com.patrickanker.lib.logging.ConsoleLogger;
import com.patrickanker.lib.util.Formatter;
import com.thevoxelbox.voxelguest.AsshatMitigationModule;
import com.thevoxelbox.voxelguest.modules.ModuleException;
import com.thevoxelbox.voxelguest.modules.ModuleManager;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessagingCommands {
    
    @Command(aliases={"broadcast", "bcast"},
            bounds={1, -1},
            help="Broadcast a message to all using §c/broadcast (message...)")
    @CommandPermission("isay.messaging.broadcast")
    public void broadcast(CommandSender cs, String[] args) {
        String concat = "";
        
        for (int i = 0; i < args.length; ++i) {
            concat += (args[i] + " ");
        }
        
        concat = Formatter.selectFormatter(SingleLineBroadcastFormatter.class).formatMessage(concat.trim());
        
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(concat);
        }
        
        Bukkit.getConsoleSender().sendMessage(concat + ChatColor.WHITE + " (Origin: " + cs.getName() + ")");
    }
    
    @Command(aliases={"say"},
            bounds={0, -1})
    @CommandPermission("isay.messaging.say")
    public void say(CommandSender cs, String[] args) {
        String concat = "";
        
        for (int i = 0; i < args.length; ++i) {
            concat += (args[i] + " ");
        }
        
        concat = concat.trim();
        
        if (cs instanceof Player) {
            concat = Formatter.encodeColors(concat);
            
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(concat);
            }
            
            Bukkit.getConsoleSender().sendMessage(concat + ChatColor.WHITE + " (Origin: " + cs.getName() + ")");
        } else {
            concat = Formatter.selectFormatter(ConsoleMessageFormatter.class).formatMessage(concat);
            
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(concat);
            }
            
            Bukkit.getConsoleSender().sendMessage(concat);
        }
    }
    
    @Command(aliases={"whisper", "w", "message", "m", "tell", "t"},
            bounds={2, -1},
            help="Whisper a message to another player using\n"
            + "§c/whisper [player] (message...)",
            playerOnly=true)
    @CommandPermission("isay.messaging.whisper")
    public void whisper(CommandSender cs, String[] args) {
        Player p = (Player) cs;
        ChatPlayer cp = ISMain.getRegisteredPlayer(p);
        
        try {
            AsshatMitigationModule module = (AsshatMitigationModule) ModuleManager.getManager().getModule(AsshatMitigationModule.class);
            
            if (module.gagged.contains(cp.getPlayer().getName())) {
                cp.message("§cYou are gagged. You cannot whisper people.");
                return;
            }
            
        } catch (NullPointerException ex) {
            // Continue -- VoxelGuest is not installed
        } catch (ModuleException ex) {
            // Continue -- Module isn't enabled
        }
        
        List<Player> l = Bukkit.matchPlayer(args[0]);
        
        if (l.isEmpty()) {
            p.sendMessage("§cNo player found by that name.");
        } else if (l.size() > 1) {
            p.sendMessage("§cMultiple players found by that name.");
        } else {
            Player send = l.get(0);
            ChatPlayer newTellee = ISMain.getRegisteredPlayer(send);
            
            String concat = "";
            
            for (int i = 1; i < args.length; ++i) {
                concat += (args[i] + " ");
            }
            
            if (newTellee.getPlayer().getName().equals(cp.getPlayer().getName())) {
                p.sendMessage("§7You mumbled to yourself §8-> §b§o" + concat.trim());
            } else {
                if (newTellee.isIgnoring(cp)) {
                    cp.getPlayer().sendMessage("§cThis player is ignoring you.");
                    return;
                }
                
                cp.setConversationWith(newTellee);
                newTellee.setConversationWith(cp);

                p.sendMessage("§7You whispered to §3" + send.getName() + " §8-> §b§o" + concat.trim());
                send.sendMessage("§3" + p.getName() + " §7whispers §8-> §b§o" + concat.trim());
                
                ConsoleLogger.log("[" + p.getName() + " -> " + send.getName() + "] " + concat.trim());
            }
        }
    }
    
    @Command(aliases={"reply", "r"},
            bounds={1, -1},
            help="To reply to a whisper, simply type\n"
            + "§c/reply (message...)",
            playerOnly=true)
    @CommandPermission("isay.messaging.whisper")
    public void reply(CommandSender cs, String[] args) {
        Player p = (Player) cs;
        ChatPlayer cp = ISMain.getRegisteredPlayer(p);
        
        try {
            AsshatMitigationModule module = (AsshatMitigationModule) ModuleManager.getManager().getModule(AsshatMitigationModule.class);
            
            if (module.gagged.contains(cp.getPlayer().getName())) {
                cp.message("§cYou are gagged. You cannot whisper people.");
                return;
            }
            
        } catch (NullPointerException ex) {
            // Continue -- VoxelGuest is not installed
        } catch (ModuleException ex) {
            // Continue -- Module isn't enabled
        }
        
        ChatPlayer converser = cp.getConverser();
        
        if (converser != null) {
            if (!converser.getPlayer().isOnline()) {
                p.sendMessage("§cThat player is not online.");
                return;
            }
            
            String concat = "";
            
            for (int i = 0; i < args.length; ++i) {
                concat += (args[i] + " ");
            }
            
            p.sendMessage("§7You whispered to §3" + converser.getPlayer().getName() + " §8-> §b§o" + concat.trim());
            converser.getPlayer().sendMessage("§3" + p.getName() + " §7whispers §8-> §b§o" + concat.trim());
            
            ConsoleLogger.log("[" + p.getName() + " -> " + converser.getPlayer().getName() + "] " + concat.trim());
        } else {
            p.sendMessage("§cYou haven't whispered anyone yet!");
        }
    }
    
    @Command(aliases={"ignore"},
            bounds={1,1},
            help="Ignore someone using §c/ignore [player]",
            playerOnly=true)
    @CommandPermission("isay.messaging.ignore")
    public void ignore(CommandSender cs, String[] args) {
        Player p = (Player) cs;
        ChatPlayer cp = ISMain.getRegisteredPlayer(p);
        
        List<Player> l = Bukkit.matchPlayer(args[0]);
        
        if (l.isEmpty()) {
            p.sendMessage("§cNo player found by that name.");
        } else if (l.size() > 1) {
            p.sendMessage("§cMultiple players found by that name.");
        } else {
            Player ignorePlayer = l.get(0);
            
            if (!cp.isIgnoring(ignorePlayer)) {
                cp.ignore(ignorePlayer);
                
                p.sendMessage("§7You ignored §a" + ignorePlayer.getName());
            }
        }
    }
    
    @Command(aliases={"unignore"},
            bounds={1,1},
            help="Unignore someone using §c/ignore [player]",
            playerOnly=true)
    @CommandPermission("isay.messaging.ignore")
    public void unignore(CommandSender cs, String[] args) {
        Player p = (Player) cs;
        ChatPlayer cp = ISMain.getRegisteredPlayer(p);
        
        List<Player> l = Bukkit.matchPlayer(args[0]);
        
        if (l.isEmpty()) {
            p.sendMessage("§cNo player found by that name.");
        } else if (l.size() > 1) {
            p.sendMessage("§cMultiple players found by that name.");
        } else {
            Player ignorePlayer = l.get(0);
            
            if (cp.isIgnoring(ignorePlayer)) {
                cp.unignore(ignorePlayer);
                
                p.sendMessage("§7You unignored §a" + ignorePlayer.getName());
            } else {
                p.sendMessage("§cYou are not ignoring this person.");
            }
        }
    }
}
