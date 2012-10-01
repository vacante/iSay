package com.thevoxelbox.isay.commands;

import com.patrickanker.lib.commands.Command;
import com.patrickanker.lib.commands.CommandPermission;
import com.thevoxelbox.isay.ChatPlayer;
import com.thevoxelbox.isay.ISMain;
import com.thevoxelbox.isay.channels.Channel;
import com.thevoxelbox.isay.channels.ChatChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

            if (cc.hasListener(p.getName())) {
                ISMain.getChannelManager().getFocus(p.getName()).assignFocus(p.getName(), false);
                cc.assignFocus(p.getName(), true);
                p.sendMessage(new StringBuilder().append("§7Channel focus set to §a").append(cc.getName()).toString());
            } else if (args.length == 2) {
                if (cp.canConnect(cc, args[1])) {
                    cc.connect(p.getName());
                } else {
                    p.sendMessage("§cYou do not have access to that channel.");
                }
            } else if (cp.canConnect(cc, "")) {
                cc.connect(p.getName());
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
        ChatPlayer cp = ISMain.getRegisteredPlayer(p);

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
                    p.sendMessage(new StringBuilder().append("§7Now listening to channel §a").append(cc.getName()).toString());
                } else {
                    p.sendMessage("§cYou do not have access to that channel.");
                }
            } else if (cp.canConnect(cc, "")) {
                cc.addListener(p.getName(), false);
                p.sendMessage(new StringBuilder().append("§7Now listening to channel §a").append(cc.getName()).toString());
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
        p.sendMessage(new StringBuilder().append("§7You have §a").append(cp.isMuted() ? "silenced" : "turned on").append(" §7chat.").toString());
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
        if ((cs instanceof Player)) {
            Player p = (Player) cs;

            List<Channel> l = ISMain.getChannelManager().getList();
            
            Iterator<Channel> it = l.listIterator();
            int i = 1;

            while (it.hasNext()) {
                ChatChannel cc = (ChatChannel) it.next();
                String foo;
                if ((cc.hasListener(p.getName())) && (cc.hasFocus(p.getName()))) {
                    foo = new StringBuilder().append("§a").append(cc.getName()).toString();
                } else {
                    if ((cc.hasListener(p.getName())) && (!cc.hasFocus(p.getName()))) {
                        foo = new StringBuilder().append("§f").append(cc.getName()).toString();
                    } else {
                        foo = new StringBuilder().append("§8").append(cc.getName()).toString();
                    }
                }
                
                p.sendMessage(new StringBuilder().append("§f").append(i).append(".§8) ").append(foo).toString());
                i++;
            }
        } else {
            List l = ISMain.getChannelManager().getList();
            Iterator it = l.listIterator();
            int i = 1;

            while (it.hasNext()) {
                ChatChannel cc = (ChatChannel) it.next();

                cs.sendMessage(new StringBuilder().append("§f").append(i).append(".§8)§f ").append(cc.getName()).toString());
                i++;
            }
        }
    }
}