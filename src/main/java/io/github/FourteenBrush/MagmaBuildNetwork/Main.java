package io.github.FourteenBrush.MagmaBuildNetwork;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.SimpleCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.managers.CommandManager;
import io.github.FourteenBrush.MagmaBuildNetwork.config.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.ChannelManager;
import io.github.FourteenBrush.MagmaBuildNetwork.library.chat.framework.ChatPlayer;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.*;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main extends JavaPlugin {

    private LuckPerms api;
    private Economy eco;
    private Chat chat;
    private UUID consoleUUID;
    private CommandManager commandManager;
    private ConfigManager configManager;
    private ChannelManager channelManager;
    private Map<UUID, ChatPlayer> playerCache;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        Utils.logInfo("Initializing...");
        configManager = new ConfigManager(this);
        commandManager = new CommandManager(this);
        consoleUUID = UUID.nameUUIDFromBytes("Console".getBytes());
        configManager.startup();
        commandManager.startup();
        for (Lang l : Lang.values()) {
            configManager.getLang().set(l.getPath(), l.getFallback());
            configManager.saveConfig(ConfigManager.FileType.LANG);
        }
        // DatabaseFactory.startup();
        if (!setupEconomy()) {
            Utils.logWarning("No Vault or economy plugin found!",
                    "This is fine if that's not installed");
        }
        if (!setupChat()) {
            Utils.logWarning("No Vault found!");
        }
        if (!setupLuckPerms()) {
            Utils.logWarning("No LuckPerms plugin found!",
                    "This is fine if that's not installed");
        }
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new LockListener(this), this);
        pm.registerEvents(new InventoryListener(), this);
        if (pm.isPluginEnabled("Vault")) {
            channelManager = new ChannelManager();
            playerCache = new HashMap<>();
            pm.registerEvents(new VaultListener(this), this);
            pm.registerEvents(new ChatListener(this), this);
        }
        setupCommands();
        Utils.logInfo("Version " + getDescription().getVersion() + " is activated!", "Done! (" + (System.currentTimeMillis() - start) + "ms)");
    }

    @Override
    public void onDisable() {
        commandManager.shutdown();
        Utils.logInfo("Stopping...", "Goodbye!");
    }

    private void setupCommands() {
        // admin commands
        getCommand("freeze").setExecutor(new SimpleCommand());
        getCommand("heal").setExecutor(new SimpleCommand());
        getCommand("ignite").setExecutor(new SimpleCommand());
        // basic commands
        getCommand("stats").setExecutor(new SimpleCommand());
        getCommand("trails").setExecutor(new SimpleCommand());
        getCommand("ally").setExecutor(new SimpleCommand());
        getCommand("prefix").setExecutor(new SimpleCommand());
        getCommand("shop").setExecutor(new SimpleCommand());
        getCommand("safechest").setExecutor(new SimpleCommand());
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            eco = rsp.getProvider();
        }
        return eco != null;
    }

    private boolean setupChat() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }
        return chat != null;
    }

    private boolean setupLuckPerms() {
        if (getServer().getPluginManager().getPlugin("LuckPerms") == null) {
            return false;
        }
        RegisteredServiceProvider<LuckPerms> rsp = getServer().getServicesManager().getRegistration(LuckPerms.class);
        if (rsp != null) {
            api = rsp.getProvider();
        }
        return api != null;
    }

    public ChatPlayer getChatPlayer(UUID uuid) {
        return playerCache.computeIfAbsent(uuid, ignored -> new ChatPlayer(this, uuid));
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

    public ChannelManager getChannelManager() {
        return channelManager;
    }

    public UUID getConsoleUUID() {
        return consoleUUID;
    }
}