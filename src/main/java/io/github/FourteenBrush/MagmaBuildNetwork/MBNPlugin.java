package io.github.FourteenBrush.MagmaBuildNetwork;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.*;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.managers.CommandManager;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.spawn.CommandSpawn;
import io.github.FourteenBrush.MagmaBuildNetwork.config.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.database.DatabaseFactory;
import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.ChannelManager;
import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.MessageManager;
import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.channels.Global;
import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.channels.Local;
import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework.User;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.*;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MBNPlugin extends JavaPlugin {

    private static MBNPlugin instance;
    private LuckPerms api;
    private Economy eco;
    private Chat chat;
    private UUID consoleUUID;
    private CommandManager commandManager;
    private ConfigManager configManager;
    private ChannelManager channelManager;
    private MessageManager messageManager;
    private Map<UUID, User> userCache;

    @Override
    public void onEnable() {
        instance = this;
        long start = System.currentTimeMillis();
        Utils.logInfo("Initializing...");
        configManager = new ConfigManager(this);
        configManager.startup();
        commandManager = new CommandManager(this);
        commandManager.startup();
        consoleUUID = UUID.nameUUIDFromBytes("Console".getBytes());
        // DatabaseFactory.startup(this);
        setupDependencies();
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new LockListener(this), this);
        pm.registerEvents(new InventoryListener(), this);
        if (pm.isPluginEnabled("Vault")) {
            channelManager = new ChannelManager();
            channelManager.addChannel(new Global(this));
            channelManager.addChannel(new Local(this));
            messageManager = new MessageManager(this);
            userCache = new HashMap<>();
            pm.registerEvents(new VaultListener(this), this);
            Utils.logInfo("Vault hooked");
        }
        setupCommands();
        new BukkitRunnable() {
            @Override
            public void run() {
                userCache.values().forEach(User::savePlayerData);
            }
        }.runTaskTimer(this, 20, 20 * 60 * 4); // Every four minutes
        Utils.logInfo("Version " + getDescription().getVersion() + " is activated", "Done! (" + (System.currentTimeMillis() - start) + "ms)");
    }

    @Override
    public void onDisable() {
        commandManager.shutdown();
        try { // Close connection if it was still open
            if (DatabaseFactory.getDatabase() != null)
                DatabaseFactory.getDatabase().closeConnection();
        } catch (SQLException e) {
            Utils.logError("Failed to close database connection!");
            e.printStackTrace();
        }
        userCache.values().forEach(User::savePlayerData);
        Utils.logInfo("Stopping...", "Goodbye!");
    }

    private void setupCommands() {
        // admin commands
        getCommand("freeze").setExecutor(new SimpleCommand());
        getCommand("heal").setExecutor(new SimpleCommand());
        // basic commands
        getCommand("stats").setExecutor(new SimpleCommand());
        getCommand("trails").setExecutor(new SimpleCommand());
        getCommand("ally").setExecutor(new SimpleCommand());
        getCommand("prefix").setExecutor(new SimpleCommand());
        getCommand("shop").setExecutor(new SimpleCommand());
        getCommand("safechest").setExecutor(new SimpleCommand());

        getCommand("ban").setExecutor(new CommandBan());
        getCommand("fly").setExecutor(CommandFly.getInstance());
        getCommand("home").setExecutor(new CommandHome());
        getCommand("lock").setExecutor(new CommandLock());
        getCommand("magmabuildnetwork").setExecutor(new CommandMagmabuildnetwork());
        getCommand("debug").setExecutor(new CommandDebug());
        getCommand("spawn").setExecutor(new CommandSpawn());
        getCommand("trade").setExecutor(CommandTrade.getInstance());
        getCommand("vanish").setExecutor(CommandVanish.getInstance());
        getCommand("maintenance").setExecutor(new CommandMaintenance());
        getCommand("chatchannel").setExecutor(new CommandChatChannel());
        getCommand("tell").setExecutor(new CommandTell());
        getCommand("reply").setExecutor(new CommandReply());
        getCommand("chat").setExecutor(new CommandChat());
    }

    private void setupDependencies() {
        RegisteredServiceProvider<?> provider;
        if (getServer().getPluginManager().isPluginEnabled("LuckPerms")) {
            provider = getServer().getServicesManager().getRegistration(LuckPerms.class);
            if (provider != null) {
                api = (LuckPerms) provider.getProvider();
                Utils.logInfo("LuckPerms hooked");
            }
        } else Utils.logWarning("Failed to hook LuckPerms, no plugin found!");
        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
            provider = getServer().getServicesManager().getRegistration(Economy.class);
            if (provider != null)
                eco = (Economy) provider.getProvider();
            provider = getServer().getServicesManager().getRegistration(Chat.class);
            if (provider != null)
                chat = (Chat) provider.getProvider();
        } else Utils.logWarning("Failed to hook Vault, no plugin found!");
    }

    public static MBNPlugin getInstance() {
        return instance;
    }

    public LuckPerms getApi() {
        return api;
    }

    public Economy getEco() {
        return eco;
    }

    public Chat getChat() {
        return chat;
    }

    public User getUser(UUID uuid) {
        return userCache.computeIfAbsent(uuid, ignored -> new User(uuid, this));
    }

    public void removeUserFromCache(UUID uuid) {
        userCache.remove(uuid);
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ChannelManager getChannelManager() {
        return channelManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public UUID getConsoleUUID() {
        return consoleUUID;
    }
}