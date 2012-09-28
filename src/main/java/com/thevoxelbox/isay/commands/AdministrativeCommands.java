/*
 * AdministrativeCommands.java
 *
 * Project: iSay
 *
 * Copyright (C) Patrick Anker 2011 - 2012. All rights reserved.
 * 
 * iSay by Patrick Anker is licensed under a Creative Commons 
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 */

package com.thevoxelbox.isay.commands;

import com.patrickanker.lib.commands.Command;
import com.patrickanker.lib.commands.CommandPermission;
import com.thevoxelbox.isay.ISMain;
import com.thevoxelbox.isay.Statistician;
import com.thevoxelbox.isay.channels.ChatChannel;
import org.bukkit.command.CommandSender;


public class AdministrativeCommands {
    
    private static final String ADMIN_PERMISSION = "system.admin";
    private static final String LOGO = "§b§oi§rSay";
    
    @Command(aliases={"isay"},
            bounds={1, -1},
            help="§c/isay reload §freloads iSay from config\n"
            + "§c/isay info §fshows general information of iSay\n"
            + "§c/isay info [channel] §fshows info about a specific channel\n"
            + "§c/isay info [player] §fshows info about a specific player")
    @CommandPermission(ADMIN_PERMISSION)
    public void isayCommand(CommandSender cs, final String[] args) 
    {
        if (args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            cs.sendMessage(LOGO + " §7reloaded");
        } else if (args[0].equalsIgnoreCase("info")) {
            if (args.length == 1) {
                info(cs, null);
            }
        }
    }
    
    public void reloadConfig() 
    {
        ISMain.getChannelManager().reloadChannels();
        
        try {
            ISMain.getPlayerConfig().load("plugins/iSay/players.yml");
        } catch (Throwable t) {
            ISMain.log("Could not reload player config", 2);
        }
        
        ISMain.getGroupManager().load();
        ISMain.getConfigData().load();
    }
    
    public void info(CommandSender cs, String name)
    {
        info(cs, name, false);
    }
    
    public void info(CommandSender cs, String name, boolean player)
    {
        if (name == null) {
            cs.sendMessage("§8====================");
            cs.sendMessage(LOGO + " §7version §a" + ISMain.getInstance().getDescription().getVersion());
            cs.sendMessage("§8");
            cs.sendMessage("§7Channels§f: §a" + ISMain.getChannelManager().getList().size());
            cs.sendMessage("§7Total messages in session§f: §a" + Statistician.getStats().fetchInt(ChatChannel.STATS_CURRENT_MESSAGE_COUNT));
            
            int mpm = Statistician.getStats().fetchInt(ChatChannel.STATS_MPM);
            
            if (mpm == -1) {
                cs.sendMessage("§7MPM§f: §cServer has not been running long enough to calculate MPM");
            } else {
                cs.sendMessage("§7MPM§f: §a" + mpm);
            }
            
            cs.sendMessage("§8====================");
            return;
        }
    }
}
