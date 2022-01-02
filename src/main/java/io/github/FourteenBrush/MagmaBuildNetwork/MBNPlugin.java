package io.github.FourteenBrush.MagmaBuildNetwork;

import io.github.FourteenBrush.MagmaBuildNetwork.chat.ChannelManager;
import io.github.FourteenBrush.MagmaBuildNetwork.chat.MessageManager;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.*;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.managers.CommandManager;
import io.github.FourteenBrush.MagmaBuildNetwork.config.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.database.Database;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.InventoryListener;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.LockListener;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.PlayerListener;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.VaultListener;
import io.github.FourteenBrush.MagmaBuildNetwork.player.UserManager;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.enums.Logger;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class MBNPlugin extends JavaPlugin {

    private static MBNPlugin instance;
    private LuckPerms api;
    private Economy eco;
    private Chat chat;
    private UUID consoleUUID;
    private CommandManager commandManager;
    private ConfigManager configManager;
    private UserManager userManager;
    private ChannelManager channelManager;
    private MessageManager messageManager;
    private Database database;

    @Override
    public void onEnable() {
        instance = this;
        long start = System.currentTimeMillis();
        Logger.INFO.log("Initializing...");
        configManager = new ConfigManager(this);
        userManager = new UserManager(this);
        commandManager = new CommandManager(configManager);
        configManager.startup();
        userManager.startup();
        commandManager.startup();
        consoleUUID = UUID.nameUUIDFromBytes("Console".getBytes());
        database = new Database(this);
        setupDependencies();
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new LockListener(this), this);
        pm.registerEvents(new InventoryListener(), this);
        if (pm.isPluginEnabled("Vault")) {
            pm.registerEvents(new VaultListener(this), this);
            channelManager = new ChannelManager();
            messageManager = new MessageManager(this);
            getCommand("chatchannel").setExecutor(new CommandChatChannel());
            getCommand("tell").setExecutor(new CommandTell());
            getCommand("reply").setExecutor(new CommandReply());
            getCommand("chat").setExecutor(new CommandChat());
        }
        setupCommands();
        Logger.INFO.log("Version " + getDescription().getVersion() + " is activated", "Done! (" + (System.currentTimeMillis() - start) + "ms)");
    }

    @Override
    public void onDisable() {
        commandManager.shutdown();
        userManager.shutdown();
        database.closeConnection();
        Logger.INFO.log("Stopping...", "Goodbye!");
    }

    private void setupCommands() {
        // admin commands
        getCommand("freeze").setExecutor(new SimpleCommand());
        getCommand("heal").setExecutor(new SimpleCommand());
        // basic commands
        getCommand("stats").setExecutor(new SimpleCommand());
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
        getCommand("spawn").setExecutor(CommandSpawn.getInstance());
        getCommand("trade").setExecutor(CommandTrade.getInstance());
        getCommand("vanish").setExecutor(CommandVanish.getInstance());
    }

    private void setupDependencies() {
        RegisteredServiceProvider<?> provider;
        if (getServer().getPluginManager().isPluginEnabled("LuckPerms")) {
            provider = getServer().getServicesManager().getRegistration(LuckPerms.class);
            if (provider != null) {
                api = (LuckPerms) provider.getProvider();
            }
        } else Logger.WARNING.log("Failed to hook LuckPerms, no plugin found!");
        if (getServer().getPluginManager().isPluginEnabled("Vault")) {
            provider = getServer().getServicesManager().getRegistration(Economy.class);
            if (provider != null)
                eco = (Economy) provider.getProvider();
            provider = getServer().getServicesManager().getRegistration(Chat.class);
            if (provider != null)
                chat = (Chat) provider.getProvider();
        } else Logger.WARNING.log("Failed to hook Vault, no plugin found!");
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

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public UserManager getUserManager() {
        return userManager;
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

    public Database getDatabase() {
        return database;
    }
}