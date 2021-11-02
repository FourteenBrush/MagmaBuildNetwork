package io.github.FourteenBrush.MagmaBuildNetwork;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.SimpleCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.managers.CommandManager;
import io.github.FourteenBrush.MagmaBuildNetwork.config.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.database.DatabaseFactory;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.InventoryListener;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.LockListener;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.PlayerListener;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.VaultListener;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {

    private LuckPerms api;
    private Economy eco;
    private Chat chat;
    private CommandManager commandManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        Utils.logInfo("Initializing...");
        configManager = new ConfigManager();
        commandManager = new CommandManager();
        configManager.startup();
        commandManager.startup();
        DatabaseFactory.startup(); // todo remove after test
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
        setupCommands();
        setupEvents();
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
        getCommand("invsee").setExecutor(new SimpleCommand());
        // basic commands
        getCommand("stats").setExecutor(new SimpleCommand());
        getCommand("trails").setExecutor(new SimpleCommand());
        getCommand("ally").setExecutor(new SimpleCommand());
        getCommand("prefix").setExecutor(new SimpleCommand());
        getCommand("shop").setExecutor(new SimpleCommand());
        getCommand("safechest").setExecutor(new SimpleCommand());
    }

    private void setupEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(), this);
        pm.registerEvents(new LockListener(), this);
        pm.registerEvents(new InventoryListener(), this);
        if (Utils.isPluginEnabled("Vault")) {
            pm.registerEvents(new VaultListener(), this);
        }
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
}