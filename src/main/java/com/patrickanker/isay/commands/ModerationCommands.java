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
import com.patrickanker.isay.channels.Channel;
import com.patrickanker.isay.channels.ChatChannel;
import com.patrickanker.lib.commands.Command;
import com.patrickanker.lib.commands.CommandPermission;
import java.util.List;

import com.patrickanker.lib.permissions.PermissionsManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

    @Command(aliases = {"channelkick", "ckick"},
            bounds = {1,2},
            help = "§c/channelkick <player> §fwill kick <player> from the kicker's focused channel\n"
            + "§c/channelkick <player> <channel> §fwill kick player from <channel>")
    @CommandPermission("isay.moderation.ckick")
    public void channelKick(CommandSender cs, String[] args)
    {
        if (args.length == 1) {
            List<Player> lp = Bukkit.matchPlayer(args[0]);

            if (lp.isEmpty()) {
                cs.sendMessage("§cNo player found with that name.");
            } else if (lp.size() > 1) {
                cs.sendMessage("§cMultiple players found with that name.");
            } else {
                if (!(cs instanceof Player)) {
                    cs.sendMessage("§cThe console does not have a channel focus.");
                    return;
                }

                Player kicker = (Player) cs;
                Player kicked = lp.get(0);

                Channel kickerFocus = ISMain.getChannelManager().getFocus(kicker.getName());

                if (kickerFocus != null) {
                    ChatChannel cc = (ChatChannel) kickerFocus;

                    if (!PermissionsManager.getHandler().hasPermission(cs.getName(), "isay.channel." + cc.getName().toLowerCase() + ".mod")) {
                        cs.sendMessage("§cYou do not have authorisation to moderate this channel.");
                        return;
                    }

                    String kickMessage = "§a" + kicked.getName() + " §7has been §6kicked §7from §a" + cc.getName();

                    if (cc.isVerbose()) {
                        for (String name : cc.getListenerList()) {
                            if (cc.hasFocus(name) || name.equals(kicked.getName())) {
                                if (cs.getName().equals(name))
                                    continue;

                                OfflinePlayer op = Bukkit.getOfflinePlayer(name);
                                Player _player = op.getPlayer();

                                if (_player != null) {
                                    _player.sendMessage(kickMessage);
                                }
                            }
                        }
                    }

                    cc.removeListener(kicked.getName());
                    cs.sendMessage(kickMessage);
                } else {
                    kicker.sendMessage("§cYou do not have a channel focus.");
                }
            }
        } else {
            List<Player> lp = Bukkit.matchPlayer(args[0]);

            if (lp.isEmpty()) {
                cs.sendMessage("§cNo player found with that name.");
            } else if (lp.size() > 1) {
                cs.sendMessage("§cMultiple players found with that name.");
            } else {
                Player kicked = lp.get(0);

                List<Channel> lc = ISMain.getChannelManager().matchChannel(args[1]);

                if (lc.isEmpty()) {
                    cs.sendMessage("§cNo channel found with that name.");
                } else if (lc.size() > 1) {
                    cs.sendMessage("§cMultiple channels found with that name.");
                } else {
                    ChatChannel cc = (ChatChannel) lc.get(0);

                    if (!PermissionsManager.getHandler().hasPermission(cs.getName(), "isay.channel." + cc.getName().toLowerCase() + ".mod")) {
                        cs.sendMessage("§cYou do not have authorisation to moderate this channel.");
                        return;
                    }

                    String kickMessage = "§a" + kicked.getName() + " §7has been §6kicked §7from §a" + cc.getName();

                    if (cc.isVerbose()) {
                        for (String name : cc.getListenerList()) {
                            if (cc.hasFocus(name) || name.equals(kicked.getName())) {
                                if (cs.getName().equals(name))
                                    continue;

                                OfflinePlayer op = Bukkit.getOfflinePlayer(name);
                                Player _player = op.getPlayer();

                                if (_player != null) {
                                    _player.sendMessage(kickMessage);
                                }
                            }
                        }
                    }

                    cc.removeListener(kicked.getName());
                    cs.sendMessage(kickMessage);
                }
            }
        }
    }

    @Command(aliases = {"channelban", "cban"},
            bounds = {1,2},
            help = "§c/channelban <player> §fwill ban <player> from the banner's focused channel\n"
            + "§c/channelban <player> <channel> §fwill ban player from <channel>")
    @CommandPermission("isay.moderation.cban")
    public void channelBan(CommandSender cs, String[] args)
    {
        if (args.length == 1) {
            List<Player> lp = Bukkit.matchPlayer(args[0]);

            if (lp.isEmpty()) {
                cs.sendMessage("§cNo player found with that name.");
            } else if (lp.size() > 1) {
                cs.sendMessage("§cMultiple players found with that name.");
            } else {
                if (!(cs instanceof Player)) {
                    cs.sendMessage("§cThe console does not have a channel focus.");
                    return;
                }

                Player banner = (Player) cs;
                Player banned = lp.get(0);

                Channel kickerFocus = ISMain.getChannelManager().getFocus(banner.getName());

                if (kickerFocus != null) {
                    ChatChannel cc = (ChatChannel) kickerFocus;

                    if (!PermissionsManager.getHandler().hasPermission(cs.getName(), "isay.channel." + cc.getName().toLowerCase() + ".mod")) {
                        cs.sendMessage("§cYou do not have authorisation to moderate this channel.");
                        return;
                    }

                    if (cc.isDefault()) {
                        cs.sendMessage("§cYou cannot ban people from the default channel.");
                        return;
                    }

                    String banMessage = "§a" + banned.getName() + " §7has been §6banned §7from §a" + cc.getName();

                    if (cc.isVerbose()) {
                        for (String name : cc.getListenerList()) {
                            if (cc.hasFocus(name) || name.equals(banned.getName())) {
                                if (cs.getName().equals(name))
                                    continue;

                                OfflinePlayer op = Bukkit.getOfflinePlayer(name);
                                Player _player = op.getPlayer();

                                if (_player != null) {
                                    _player.sendMessage(banMessage);
                                }
                            }
                        }
                    }

                    cc.removeListener(banned.getName());
                    cc.addBannedListener(banned.getName());
                    banner.sendMessage(banMessage);
                } else {
                    banner.sendMessage("§cYou do not have a channel focus.");
                }
            }
        } else {
            List<Player> lp = Bukkit.matchPlayer(args[0]);

            if (lp.isEmpty()) {
                cs.sendMessage("§cNo player found with that name.");
            } else if (lp.size() > 1) {
                cs.sendMessage("§cMultiple players found with that name.");
            } else {
                Player banned = lp.get(0);

                List<Channel> lc = ISMain.getChannelManager().matchChannel(args[1]);

                if (lc.isEmpty()) {
                    cs.sendMessage("§cNo channel found with that name.");
                } else if (lc.size() > 1) {
                    cs.sendMessage("§cMultiple channels found with that name.");
                } else {
                    ChatChannel cc = (ChatChannel) lc.get(0);

                    if (!PermissionsManager.getHandler().hasPermission(cs.getName(), "isay.channel." + cc.getName().toLowerCase() + ".mod")) {
                        cs.sendMessage("§cYou do not have authorisation to moderate this channel.");
                        return;
                    }

                    if (cc.isDefault()) {
                        cs.sendMessage("§cYou cannot ban people from the default channel.");
                        return;
                    }

                    String banMessage = "§a" + banned.getName() + " §7has been §6banned §7from §a" + cc.getName();

                    if (cc.isVerbose()) {
                        for (String name : cc.getListenerList()) {
                            if (cc.hasFocus(name) || name.equals(banned.getName())) {
                                if (cs.getName().equals(name))
                                    continue;

                                OfflinePlayer op = Bukkit.getOfflinePlayer(name);
                                Player _player = op.getPlayer();

                                if (_player != null) {
                                    _player.sendMessage(banMessage);
                                }
                            }
                        }
                    }

                    cc.removeListener(banned.getName());
                    cc.addBannedListener(banned.getName());
                    cs.sendMessage(banMessage);
                }
            }
        }
    }

    @Command(aliases = {"channelpull", "cpull"},
            bounds = {1,2},
            help = "§c/channelkick <player> §fwill pull <player> into the command sender's focused channel\n"
            + "§c/channelkick <player> <channel> §fwill pull player into <channel>")
    @CommandPermission("isay.moderation.cpull")
    public void channelPull(CommandSender cs, String[] args)
    {
        if (args.length == 1) {
            List<Player> lp = Bukkit.matchPlayer(args[0]);

            if (lp.isEmpty()) {
                cs.sendMessage("§cNo player found with that name.");
            } else if (lp.size() > 1) {
                cs.sendMessage("§cMultiple players found with that name.");
            } else {
                if (!(cs instanceof Player)) {
                    cs.sendMessage("§cThe console does not have a channel focus.");
                    return;
                }

                Player puller = (Player) cs;
                Player pulled = lp.get(0);

                Channel kickerFocus = ISMain.getChannelManager().getFocus(puller.getName());

                if (kickerFocus != null) {
                    ChatChannel cc = (ChatChannel) kickerFocus;

                    if (!PermissionsManager.getHandler().hasPermission(cs.getName(), "isay.channel." + cc.getName().toLowerCase() + ".mod")) {
                        cs.sendMessage("§cYou do not have authorisation to moderate this channel.");
                        return;
                    }

                    for (Channel channel : ISMain.getChannelManager().getList()) {
                        if (channel.hasFocus(pulled.getName())) {
                            channel.assignFocus(pulled.getName(), false);
                        }
                    }

                    cc.addListener(pulled.getName(), true);
                    puller.sendMessage("§7Pulled §a" + pulled.getName()  + " §7into §a" + cc.getName());

                } else {
                    puller.sendMessage("§cYou do not have a channel focus.");
                }
            }
        } else {
            List<Player> lp = Bukkit.matchPlayer(args[0]);

            if (lp.isEmpty()) {
                cs.sendMessage("§cNo player found with that name.");
            } else if (lp.size() > 1) {
                cs.sendMessage("§cMultiple players found with that name.");
            } else {
                Player pulled = lp.get(0);

                List<Channel> lc = ISMain.getChannelManager().matchChannel(args[1]);

                if (lc.isEmpty()) {
                    cs.sendMessage("§cNo channel found with that name.");
                } else if (lc.size() > 1) {
                    cs.sendMessage("§cMultiple channels found with that name.");
                } else {
                    ChatChannel cc = (ChatChannel) lc.get(0);

                    if (!PermissionsManager.getHandler().hasPermission(cs.getName(), "isay.channel." + cc.getName().toLowerCase() + ".mod")) {
                        cs.sendMessage("§cYou do not have authorisation to moderate this channel.");
                        return;
                    }

                    for (Channel channel : ISMain.getChannelManager().getList()) {
                        if (channel.hasFocus(pulled.getName())) {
                            channel.assignFocus(pulled.getName(), false);
                        }
                    }

                    cc.addListener(pulled.getName(), true);
                    cs.sendMessage("§7Pulled §a" + pulled.getName()  + " §7into §a" + cc.getName());
                }
            }
        }
    }
}
