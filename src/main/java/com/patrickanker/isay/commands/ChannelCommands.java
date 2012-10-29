package com.patrickanker.isay.commands;

import com.patrickanker.lib.commands.Command;
import com.patrickanker.lib.commands.CommandPermission;
import com.patrickanker.isay.ChatPlayer;
import com.patrickanker.isay.ISMain;
import com.patrickanker.isay.channels.Channel;
import com.patrickanker.isay.channels.ChatChannel;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.patrickanker.lib.permissions.PermissionsManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChannelCommands {

    @Command(aliases = {"join"}, 
            bounds = {1, 2}, 
            help = "Join a channel using §c/join [channel]\n" + 
                   "§fIf you already ghost that channel,\n" + 
                   "you will change your chat focus to\n" + 
                   "the selected [channel].", 
            playerOnly = true)
    @CommandPermission("isay.channels.join")
    public void join(CommandSender cs, String[] args)
    {
        Player p = (Player) cs;
        ChatPlayer cp = ISMain.getRegisteredPlayer(p);

        List l = ISMain.getChannelManager().matchChannel(args[0]);

        if (l.isEmpty()) {
            p.sendMessage("§cNo channel found by that name.");
        } else if (l.size() > 1) {
            p.sendMessage("§cMultiple channels found by that name.");
        } else {
            ChatChannel cc = (ChatChannel) l.get(0);
            Channel focus = ISMain.getChannelManager().getFocus(p.getName());

            if (cc.hasListener(p.getName())) {

                if (focus != null) {
                    focus.assignFocus(p.getName(), false);
                }

                cc.assignFocus(p.getName(), true);
                p.sendMessage("§7Channel focus set to §a" + cc.getName());
            } else if (args.length == 2) {
                if (cp.canConnect(cc, args[1])) {
                    cc.connect(p.getName());

                    if (focus != null) {
                        focus.assignFocus(p.getName(), false);
                    }

                } else {
                    p.sendMessage("§cYou do not have access to that channel.");
                }
            } else if (cp.canConnect(cc, "")) {
                cc.connect(p.getName());

                if (focus != null) {
                    focus.assignFocus(p.getName(), false);
                }
            } else {
                p.sendMessage("§cYou do not have access to that channel.");
            }
        }
    }

    @Command(aliases = {"leave"}, 
            bounds = {1, 1}, 
            help = "To leave a channel, simply type §c/leave [channel]", 
            playerOnly = true)
    @CommandPermission("isay.channels.leave")
    public void leave(CommandSender cs, String[] args)
    {
        Player p = (Player) cs;

        List l = ISMain.getChannelManager().matchChannel(args[0]);

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

    @Command(aliases = {"ghost"}, 
            bounds = {1, 2}, 
            help = "To ghost a channel, simply type §c/ghost [channel]", 
            playerOnly = true)
    @CommandPermission("isay.channels.ghost")
    public void ghost(CommandSender cs, String[] args)
    {
        Player p = (Player) cs;
        ChatPlayer cp = ISMain.getRegisteredPlayer(p);

        List l = ISMain.getChannelManager().matchChannel(args[0]);

        if (l.isEmpty()) {
            p.sendMessage("§cNo channel found by that name.");
        } else if (l.size() > 1) {
            p.sendMessage("§cMultiple channels found by that name.");
        } else {
            ChatChannel cc = (ChatChannel) l.get(0);

            if (cc.hasListener(p.getName())) {
                p.sendMessage("§cYou are already listening to that channel.");
            } else if (args.length == 2) {
                if (cp.canConnect(cc, args[1])) {
                    cc.addListener(p.getName(), false);
                    p.sendMessage("§7Now listening to channel §a" + cc.getName());
                } else {
                    p.sendMessage("§cYou do not have access to that channel.");
                }
            } else if (cp.canConnect(cc, "")) {
                cc.addListener(p.getName(), false);
                p.sendMessage("§7Now listening to channel §a" + cc.getName());
            } else {
                p.sendMessage("§cYou do not have access to that channel.");
            }
        }
    }

    @Command(aliases = {"cmute"}, 
            bounds = {0, 0}, 
            help = "Toggle chat with §c/cmute", 
            playerOnly = true)
    @CommandPermission("isay.channels.cmute")
    public void cmute(CommandSender cs, String[] args)
    {
        Player p = (Player) cs;
        ChatPlayer cp = ISMain.getRegisteredPlayer(p);

        cp.setMuted(!cp.isMuted());
        p.sendMessage("§7You have §a" + (cp.isMuted() ? "silenced" : "turned on") + " §7chat.");
    }

    @Command(aliases = {"quickmessage", "qm"}, 
            bounds = {2, -1}, 
            help = "Send a message to a channel that you are\n" + 
            "listening to with §c/qm [channel] (message...)", 
            playerOnly = true)
    @CommandPermission("isay.channels.quickmessage")
    public void quickMessage(CommandSender cs, String[] args)
    {
        Player p = (Player) cs;
        ChatPlayer cp = ISMain.getRegisteredPlayer(p);

        List l = ISMain.getChannelManager().matchChannel(args[0]);

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

                for (int i = 1; i < args.length; i++) {
                    concat = new StringBuilder().append(concat).append(args[i]).append(" ").toString();
                }

                cc.dispatch(cp, concat.trim());
            }
        }
    }

    @Command(aliases = {"listchannels", "lc"}, 
            bounds = {0, 0}, 
            help = "List all channels and your affliation with them using §c/lc\n" + 
            "A §agreen §fname shows your focus channel, the one you chat into.\n" + 
            "A §fwhite §fname shows a channel you are ghosting.\n" + 
            "A §8dark grey §fname shows a channel you are not affiliated with.")
    @CommandPermission("isay.channels.list")
    public void listChannels(CommandSender cs, String[] args)
    {
        List<Channel> l = ISMain.getChannelManager().getList();
        List<String> cnames = new LinkedList<String>();

        for (Channel c : l) {
            cnames.add(c.getName());
        }

        if (l.isEmpty()) {
            cs.sendMessage("§cNo channels are defined.");
            return;
        }

        Collections.sort(cnames);
        int i = 1;

        for (String cname : cnames) {
            ChatChannel channel = (ChatChannel) ISMain.getChannelManager().matchChannel(cname).get(0);

            String concat = "§f" + i + "§8.) ";

            if (channel.hasListener(cs.getName())) {
                concat += (channel.hasFocus(cs.getName()) ? "§a" : "§f") + channel.getName();
            } else {
                concat += "§7" + cs.getName();
            }

            ++i;
        }
    }
    
    @Command(aliases={"channel", "ch"},
            bounds={1, -1},
            help="§c/channel [create, -c] <channel> (password) §fcreates a channel with an optional password\n"
            + "§c/channel [remove, -r] <channel> §fpermanently removes a channel\n"
            + "§c/channel [enable, -e] <channel> §fenables/disables a channel\n"
            + "§c/channel [lock, -l] <channel> §flocks a channel to those who do not have the iSay administrative permission\n"
            + "§c/channel [verbose, -v] <channel> §ftoggles if a channel is verbose or not\n"
            + "§c/channel [promote, -p] <channel> §ftoggles if only promoted individuals can chat in a channel")
    @CommandPermission("isay.channels.channel")
    public void channelCommand(CommandSender cs, String[] args)
    {
        if (args.length < 2) {
            cs.sendMessage("§cA channel must be specified.");
            return;
        }

        if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("-c")) {
            if (args.length == 2) {
                if (ISMain.getChannelManager().matchChannel(args[1]).size() == 1) {
                    cs.sendMessage("§cA channel already exists with that name.");
                    return;
                }

                ChatChannel channel = new ChatChannel(args[1]);
                ISMain.getChannelManager().registerChannel(channel);

                cs.sendMessage("§7Registered new channel §a" + channel);

                if (!PermissionsManager.getHandler().hasPermission(cs.getName(), "isay.channel." + args[1].toLowerCase() + ".admin")) {
                    PermissionsManager.getHandler().givePermission(cs.getName(), "isay.channel." + args[1].toLowerCase() + ".admin");
                }
            } else {
                if (ISMain.getChannelManager().matchChannel(args[1]).size() == 1) {
                    cs.sendMessage("§cA channel already exists with that name.");
                    return;
                }

                ChatChannel channel = new ChatChannel(args[1]);
                ISMain.getChannelManager().registerChannel(channel);

                String concat = "";

                for (int i = 2; i < args.length; ++i) {
                    concat += args[i] + " ";
                }

                concat = concat.trim();
                channel.setPassword(concat);

                cs.sendMessage("§7Registered new channel \"§a" + channel.getName() + "§7\" with password \"§a" + concat + "§7\"");

                if (!PermissionsManager.getHandler().hasPermission(cs.getName(), "isay.channel." + args[1].toLowerCase() + ".admin")) {
                    PermissionsManager.getHandler().givePermission(cs.getName(), "isay.channel." + args[1].toLowerCase() + ".admin");
                }
            }
        } else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("-r")) {
            List<Channel> l = ISMain.getChannelManager().matchChannel(args[1]);

            if (l.isEmpty()) {
                cs.sendMessage("§cNo channel found with that name.");
            } else if (l.size() > 1) {
                cs.sendMessage("§cMultiple channels found with that name.");
            } else {
                ChatChannel cc = (ChatChannel) l.get(0);

                if (!PermissionsManager.getHandler().hasPermission(cs.getName(), "isay.channel." + cc.getName().toLowerCase() + ".admin")) {
                    cs.sendMessage("§cYou do not have authorisation to administer this channel.");
                    return;
                }

                if (cc.isDefault()) {
                    cs.sendMessage("§cYou cannot remove the default channel.");
                    return;
                }

                ISMain.getChannelManager().removeChannel(cc);
                cs.sendMessage("§7Successfully §6removed §7channel §a" + cc.getName());
            }
        } else if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("-e")) {
            if (!PermissionsManager.getHandler().hasPermission(cs.getName(), "isay.channel." + args[1] + ".admin")) {
                cs.sendMessage("§cYou do not have authorisation to administer this channel.");
                return;
            }
        } else if (args[0].equalsIgnoreCase("lock") || args[0].equalsIgnoreCase("-l")) {
            if (!PermissionsManager.getHandler().hasPermission(cs.getName(), "isay.channel." + args[1] + ".admin")) {
                cs.sendMessage("§cYou do not have authorisation to administer this channel.");
                return;
            }
        } else if (args[0].equalsIgnoreCase("verbose") || args[0].equalsIgnoreCase("-v")) {
            if (!PermissionsManager.getHandler().hasPermission(cs.getName(), "isay.channel." + args[1] + ".admin")) {
                cs.sendMessage("§cYou do not have authorisation to administer this channel.");
                return;
            }
        } else if (args[0].equalsIgnoreCase("promote") || args[0].equalsIgnoreCase("-p")) {
            List<Channel> l = ISMain.getChannelManager().matchChannel(args[1]);

            if (l.isEmpty()) {
                 cs.sendMessage("§cNo channel found with that name.");
            } else if (l.size() > 1) {
                cs.sendMessage("§cMultiple channels found with that name.");
            } else {
                ChatChannel cc = (ChatChannel) l.get(0);

                if (!PermissionsManager.getHandler().hasPermission(cs.getName(), "isay.channel." + cc.getName().toLowerCase() + ".admin")) {
                    cs.sendMessage("§cYou do not have authorisation to administer this channel.");
                    return;
                }

                cc.setPromoted(!cc.isPromoted());
                cs.sendMessage("§a" + cc.getName() + "§7's chat has been §a" + (cc.isPromoted() ? "promoted" : "normalised"));
            }
        }
    }
}