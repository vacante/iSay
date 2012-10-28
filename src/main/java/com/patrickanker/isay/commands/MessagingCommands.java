package com.patrickanker.isay.commands;

import com.patrickanker.lib.commands.Command;
import com.patrickanker.lib.commands.CommandPermission;
import com.patrickanker.lib.logging.ConsoleLogger;
import com.patrickanker.lib.util.Formatter;
import com.patrickanker.isay.ChatPlayer;
import com.patrickanker.isay.ISMain;
import com.patrickanker.isay.MuteServices;
import com.patrickanker.isay.formatters.ConsoleMessageFormatter;
import com.patrickanker.isay.formatters.SingleLineBroadcastFormatter;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessagingCommands {

    @Command(aliases = {"broadcast", "bcast"}, 
            bounds = {1, -1}, 
            help = "Broadcast a message to all using §c/broadcast (message...)")
    @CommandPermission("isay.messaging.broadcast")
    public void broadcast(CommandSender cs, String[] args)
    {
        String concat = "";

        for (int i = 0; i < args.length; i++) {
            concat = concat + args[i] + " ";
        }

        concat = Formatter.selectFormatter(SingleLineBroadcastFormatter.class).formatMessage(concat.trim());

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(concat);
        }

        ISMain.log(concat + " (Origin: " + cs.getName() + ")");
    }

    @Command(aliases = {"say"}, 
            bounds = {0, -1})
    @CommandPermission("isay.messaging.say")
    public void say(CommandSender cs, String[] args)
    {
        String concat = "";

        for (int i = 0; i < args.length; i++) {
            concat = concat + args[i] + " ";
        }

        concat = concat.trim();

        if ((cs instanceof Player)) {
            concat = Formatter.encodeColors(concat);

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(concat);
            }

            ISMain.log(concat + "(Origin: " + cs.getName() + ")");
        } else {
            concat = Formatter.selectFormatter(ConsoleMessageFormatter.class).formatMessage(concat, null, new Object[0]);

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(concat);
            }

            ISMain.log(concat);
            Bukkit.getConsoleSender().sendMessage(Formatter.stripColors(concat));
        }
    }

    @Command(aliases = {"whisper", "w", "message", "m", "tell", "t"}, 
            bounds = {2, -1}, 
            help = "Whisper a message to another player using\n" + 
                   "§c/whisper [player] (message...)", 
            playerOnly = true)
    @CommandPermission("isay.messaging.whisper")
    public void whisper(CommandSender cs, String[] args)
    {
        Player from = (Player) cs;
        ChatPlayer cp = ISMain.getRegisteredPlayer(from);
        
        if (cp.isMuted()) {
            MuteServices.muteWarn(cp);
            return;
        }
        
        List<Player> l = Bukkit.matchPlayer(args[0]);

        if (l.isEmpty()) {
            from.sendMessage("§cNo player found by that name.");
        } else if (l.size() > 1) {
            from.sendMessage("§cMultiple players found by that name.");
        } else {
            Player to = (Player) l.get(0);
            ChatPlayer newTellee = ISMain.getRegisteredPlayer(to);

            String concat = "";

            for (int i = 1; i < args.length; i++) {
                concat = concat + args[i] + " ";
            }

            if (newTellee.getPlayer().getName().equals(cp.getPlayer().getName())) {
                from.sendMessage("§7You mumbled to yourself §8-> §b§o" + concat.trim());
            } else {
                if (newTellee.isIgnoring(cp)) {
                    cp.getPlayer().sendMessage("§cThis player is ignoring you.");
                    return;
                }

                String getter;
                String sender;

                if (cp.getNameAlias() != null) {
                    sender = cp.getNameAlias();
                } else {
                    sender = cp.getPlayer().getName();
                }

                if (cp.getNameAlias() != null) {
                    getter = newTellee.getNameAlias();
                } else {
                    getter = newTellee.getPlayer().getName();
                }

                cp.setConversationWith(newTellee);
                newTellee.setConversationWith(cp);

                from.sendMessage("§8[§7You §8-> §3" + getter + "§8] §b§o" + concat.trim());
                to.sendMessage("§8[§3" + sender + " §8-> §7You§8] §b§o" + concat.trim());

                ISMain.log("[" + from.getName() + " -> " + to.getName() + "] " + concat.trim());
            }
        }
    }

    @Command(aliases = {"reply", "r"}, 
            bounds = {1, -1}, 
            help = "To reply to a whisper, simply type\n" 
            + "§c/reply (message...)", 
            playerOnly = true)
    @CommandPermission("isay.messaging.whisper")
    public void reply(CommandSender cs, String[] args)
    {
        Player from = (Player) cs;
        ChatPlayer cp = ISMain.getRegisteredPlayer(from);
        
        if (cp.isMuted()) {
            MuteServices.muteWarn(cp);
            return;
        }
        
        ChatPlayer converser = cp.getConverser();

        if (converser != null) {
            if (!converser.getPlayer().isOnline()) {
                from.sendMessage("§cThat player is not online.");
                return;
            }

            String concat = "";

            for (int i = 0; i < args.length; i++) {
                concat = concat + args[i] + " §b§o";
            }

            String getter;
            String sender;

            if (cp.getNameAlias() != null) {
                sender = cp.getNameAlias();
            } else {
                sender = cp.getPlayer().getName();
            }

            if (converser.getNameAlias() != null) {
                getter = converser.getNameAlias();
            } else {
                getter = converser.getPlayer().getName();
            }

            cp.setConversationWith(converser);
            converser.setConversationWith(cp);

            from.sendMessage("§8[§7You §8-> §3" + getter + "§8] §b§o" + concat.trim());
            converser.sendMessage("§8[§3" + sender + " §8-> §7You§8] §b§o" + concat.trim());

            ISMain.log("[" + from.getName() + " -> " + converser.getPlayer().getName() + "] " + concat.trim());
        } else {
            from.sendMessage("§cYou haven't whispered anyone yet!");
        }
    }

    @Command(aliases = {"ignore"}, 
            bounds = {1, 1}, 
            help = "Ignore someone using §c/ignore [player]", 
            playerOnly = true)
    @CommandPermission("isay.messaging.ignore")
    public void ignore(CommandSender cs, String[] args)
    {
        Player p = (Player) cs;
        ChatPlayer cp = ISMain.getRegisteredPlayer(p);

        List l = Bukkit.matchPlayer(args[0]);

        if (l.isEmpty()) {
            p.sendMessage("§cNo player found by that name.");
        } else if (l.size() > 1) {
            p.sendMessage("§cMultiple players found by that name.");
        } else {
            Player ignorePlayer = (Player) l.get(0);

            if (!cp.isIgnoring(ignorePlayer)) {
                cp.ignore(ignorePlayer);

                p.sendMessage("§7You ignored §a" + ignorePlayer.getName());
            }
        }
    }

    @Command(aliases = {"unignore"}, 
            bounds = {1, 1}, 
            help = "Unignore someone using §c/ignore [player]", 
            playerOnly = true)
    @CommandPermission("isay.messaging.ignore")
    public void unignore(CommandSender cs, String[] args)
    {
        Player p = (Player) cs;
        ChatPlayer cp = ISMain.getRegisteredPlayer(p);

        List l = Bukkit.matchPlayer(args[0]);

        if (l.isEmpty()) {
            p.sendMessage("§cNo player found by that name.");
        } else if (l.size() > 1) {
            p.sendMessage("§cMultiple players found by that name.");
        } else {
            Player ignorePlayer = (Player) l.get(0);

            if (cp.isIgnoring(ignorePlayer)) {
                cp.unignore(ignorePlayer);

                p.sendMessage("§7You unignored §a" + ignorePlayer.getName());
            } else {
                p.sendMessage("§cYou are not ignoring this person.");
            }
        }
    }
}