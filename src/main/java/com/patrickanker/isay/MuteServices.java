/*
 * MuteServices.java
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

import org.bukkit.Bukkit;


public class MuteServices {
    
    private static final String DEFAULT_MUTE_REASON = "Unruliness";
    
    public static void muteWarn(ChatPlayer cp)
    {
        if (!cp.getMuteTimeout().equals("")) {
            cp.sendMessage("§8====================");
            cp.sendMessage("§7You have been §6muted§7. You cannot chat or PM anyone.");
            
            if (cp.muteTimedOut()) {
                cp.sendMessage("§7You §acan §7enter the §akey phrase §7to unmute.");
            } else {
                cp.sendMessage("§7You §6cannot §7enter the §akey phrase §7to unmute yourself until:");
                cp.sendMessage("§6" + cp.getMuteTimeout());
            }
            
            cp.sendMessage("§8====================");
        } else {
            cp.sendMessage("§8====================");
            cp.sendMessage("§7You have been §6muted§7. You cannot chat or PM anyone.");
            cp.sendMessage("§7You §acan §7enter the §akey phrase §7to unmute yourself.");
            cp.sendMessage("§8====================");
        }
    }
    
    public static void adminMuteWarn(ChatPlayer cp)
    {
        if (!cp.getAdminMuteTimeout().equals("")) {
            if (cp.adminMuteTimedOut()) {
                unmuteAnnounce(cp);
                cp.setAdminMute(false);
                cp.setAdminMuteTimeout("");
            }
            
            cp.sendMessage("§8====================");
            cp.sendMessage("§7You have been §6force muted§7. You cannot chat or PM anyone.");
            cp.sendMessage("§7You §6will remain §7muted until:");
            cp.sendMessage("§6" + cp.getMuteTimeout());
            cp.sendMessage("§8====================");
        } else {
            cp.sendMessage("§8====================");
            cp.sendMessage("§7You have been §6force muted§7. You cannot chat or PM anyone.");
            cp.sendMessage("§7You §6will remain §7muted until a moderator lifts the mute.");
            cp.sendMessage("§8====================");
        }
    }
    
    public static void broadcastMute(String gagger, String gagged)
    {
        broadcastMute(gagger, gagged, "");
    }
    
    public static void broadcastMute(String gagger, String gagged, String reason)
    {
        Bukkit.broadcastMessage("§8====================");
        Bukkit.broadcastMessage("§6" + gagged + " §7has been muted by §6" + gagger + " §7for:");
        Bukkit.broadcastMessage("§3" + (reason.equals("") ? DEFAULT_MUTE_REASON : reason));
        Bukkit.broadcastMessage("§8====================");
    }
    
    public static void unmuteAnnounce(ChatPlayer cp)
    {
        cp.sendMessage("§aYou have been unmuted.");
    }
}
