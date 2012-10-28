package com.patrickanker.isay;

import com.patrickanker.lib.commands.*;
import com.patrickanker.lib.config.PropertyConfiguration;
import com.patrickanker.lib.logging.ConsoleLogger;
import com.patrickanker.lib.permissions.PermissionsManager;
import com.patrickanker.isay.channels.ChannelManager;
import com.patrickanker.isay.commands.*;
import com.patrickanker.isay.listeners.PlayerListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.patrickanker.lib.util.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

public class ISMain extends JavaPlugin {

    private static ISMain instance;
    protected static PermissionsManager permsManager;
    protected static CommandManager commandManager;
    protected static GroupManager groupManager;
    protected static ChannelManager channelManager;
    protected static PingManager pingManager;
    protected static ItemAliasManager itemAliasManager;
    protected static PropertyConfiguration config = new PropertyConfiguration("/iSay/iSay");
    protected static YamlConfiguration playerConfig = new YamlConfiguration();
    protected static YamlConfiguration channelConfig = new YamlConfiguration();
    private static List<ChatPlayer> registeredPlayers = new ArrayList();
    private static final String defaultMessageFormat = "$id $m";
    private static final String defaultBroadcastFormat = "&f[&cBroadcast&f] &a$m";
    private static final String defaultConsoleFormat = "&d[Server] $m";

    @Override
    public void onDisable()
    {
        channelManager.shutDown();
        pingManager.shutdownPingTask();
        itemAliasManager.shutDown();

        Iterator it = registeredPlayers.listIterator();

        while (it.hasNext()) {
            ChatPlayer next = (ChatPlayer) it.next();
            next.save();
        }

        unregisterAllPlayers();

        try {
            playerConfig.save(new File("plugins/iSay/players.yml"));
        } catch (IOException ex) {
            log("Could not save player data file", 2);
        }

        groupManager.saveGroupConfigurations();
        config.save();
    }

    @Override
    public void onEnable()
    {
        instance = this;
        config.load();

        if ((getConfigData().getString("reset") == null) || (getConfigData().getString("reset").equalsIgnoreCase("yes"))) {
            loadFactorySettings();
        }
        
        commandManager = new CommandManager(this);
        permsManager = new PermissionsManager(getServer(), "[iSay]", config);
        groupManager = new GroupManager();
        channelManager = new ChannelManager();
        pingManager = new PingManager();
        itemAliasManager = new ItemAliasManager();
        
        groupManager.load();
        
        commandManager.registerCommands(ChannelCommands.class);
        commandManager.registerCommands(GeneralCommands.class);
        commandManager.registerCommands(MessagingCommands.class);
        commandManager.registerCommands(AdministrativeCommands.class);
        commandManager.registerCommands(ModerationCommands.class);
        commandManager.registerCommands(PlayerCommands.class);
        
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(permsManager, this);

        permsManager.registerActiveHandler();
        
        try {
            File _players = new File("plugins/iSay/players.yml");
            
            if (!_players.exists()) {
                _players.getParentFile().mkdirs();
                _players.createNewFile();
            }
            
            playerConfig.load(_players);
        } catch (FileNotFoundException ex) {
            log("Could not load player data", 2);
        } catch (IOException ex) {
            log("Could not load player data", 2);
        } catch (InvalidConfigurationException ex) {
            log("Could not load player data", 2);
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            ChatPlayer foo = registerPlayer(p);
            channelManager.onPlayerLogin(foo);
        }
        
        // Init Metrics
        
        try {
            MetricsLite metrics = new MetricsLite(this);
            
            if (!metrics.isOptOut()) {
                metrics.start();
            }
        } catch (Throwable t) {
            ISMain.log("Could not send statistics to Metrics", 1);
        }
    }

    @Override
    public boolean onCommand(CommandSender cs, Command command, String label, String[] args)
    {
        return commandManager.executeCommandProcessErrors(command, cs, args, this);
    }

    public static ChannelManager getChannelManager()
    {
        return channelManager;
    }

    public static PropertyConfiguration getConfigData()
    {
        return config;
    }

    public static YamlConfiguration getPlayerConfig()
    {
        return playerConfig;
    }
    
    public static YamlConfiguration getChannelConfig()
    {
        return channelConfig;
    }

    public static ISMain getInstance()
    {
        return instance;
    }

    public static GroupManager getGroupManager()
    {
        return groupManager;
    }

    public static PingManager getPingManager()
    {
        return pingManager;
    }

    public static ItemAliasManager getItemAliasManager()
    {
        return itemAliasManager;
    }

    public static ChatPlayer registerPlayer(Player player)
    {
        if (!isPlayerRegistered(player)) {
            ChatPlayer cp = new ChatPlayer(player);
            registerPlayer(cp);
            return cp;
        }

        return getRegisteredPlayer(player);
    }

    private static void registerPlayer(ChatPlayer cp)
    {
        if (!registeredPlayers.contains(cp)) {
            registeredPlayers.add(cp);
        }
    }

    public static void unregisterPlayer(Player player)
    {
        if (isPlayerRegistered(player)) {
            ChatPlayer cp = getRegisteredPlayer(player);
            unregisterPlayer(cp);
        }
    }

    private static void unregisterPlayer(ChatPlayer cp)
    {
        if (registeredPlayers.contains(cp)) {
            registeredPlayers.remove(cp);
        }
    }

    private static void unregisterAllPlayers()
    {
        registeredPlayers.clear();
    }

    public static boolean isPlayerRegistered(Player player)
    {
        ChatPlayer[] cps = new ChatPlayer[registeredPlayers.size()];
        cps = (ChatPlayer[]) registeredPlayers.toArray(cps);

        for (ChatPlayer cp : cps) {
            if (cp.getPlayer().getName().equals(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public static ChatPlayer getRegisteredPlayer(Player player)
    {
        ChatPlayer[] cps = new ChatPlayer[registeredPlayers.size()];
        cps = (ChatPlayer[]) registeredPlayers.toArray(cps);

        for (ChatPlayer cp : cps) {
            if (cp.getPlayer().equals(player)) {
                return cp;
            }
        }
        ChatPlayer cp = new ChatPlayer(player);
        registerPlayer(cp);
        return cp;
    }

    public static String getDefaultBroadcastFormat()
    {
        return defaultBroadcastFormat;
    }

    public static String getDefaultConsoleFormat()
    {
        return defaultConsoleFormat;
    }

    public static String getDefaultMessageFormat()
    {
        return defaultMessageFormat;
    }

    private void loadFactorySettings()
    {
        getConfigData().setString("broadcast-format", "&f[&cBroadcast&f] &a$m");
        getConfigData().setString("console-format", "&d[Server] $m");
        getConfigData().setString("message-format", "$id $m");

        getConfigData().setString("reset", "no");
        log("| ========================================== |");
        log("| * iSay                                     |");
        log("| *                                          |");
        log("| * Continue, good sir. I'm listening...     |");
        log("| *                                          |");
        log("| * Built by: psanker                        |");
        log("| * Licensed by the BSD License - 2012       |");
        log("| ========================================== |");
        log("Factory settings loaded");
    }

    public static void log(String str)
    {
        log(str, 0);
    }

    public static void log(String str, int importance)
    {
        str = Formatter.stripColors(str);

        String playerCopy = str;

        if (importance == 1) {
            playerCopy = "§6[WARNING] " + playerCopy;
        } else if (importance == 2) {
            playerCopy = "§c[ERROR] " + playerCopy;
        }

        channelManager.getDebugChannel().dispatch(null, playerCopy);
        ConsoleLogger.getLogger("iSay").log(str, importance);
    }
}