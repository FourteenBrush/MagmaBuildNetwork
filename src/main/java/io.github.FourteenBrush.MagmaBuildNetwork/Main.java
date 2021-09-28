package io.github.FourteenBrush.MagmaBuildNetwork;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandBan;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandDebug;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandFly;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandHome;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandLock;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandMagmabuildnetwork;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandManager;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandSafechest;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandSpawn;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandStore;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandTrade;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandVanish;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.PlayerCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.InventoryListener;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.LockListener;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.PlayerListener;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.VaultListener;
import io.github.FourteenBrush.MagmaBuildNetwork.spawn.Spawn;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {

    private static LuckPerms api;
    private static Economy eco;
    private static Chat chat;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        startup();
        commandsSetup();
        eventsSetup();
        Utils.logInfo("Version " + getDescription().getVersion() + " is activated!", "Done! (" + (System.currentTimeMillis() - start) + "ms)");
    }

    @Override
    public void onDisable() {
        CommandManager.onDisable();
        Utils.logInfo("Stopping...", "Goodbye!");
    }

    private void startup() {
        Utils.logInfo("Initializing...");
        ConfigManager.createFiles();
        CommandManager.onEnable();
        Spawn.setup();
        //ConfigurationSerialization.registerClass(Home.class);
        if (!setupEconomy()) {
            Utils.logWarning("No Vault or economy plugin found!",
                    "This is fine if that's not installed");
        }
        if (!setupChat()) {
            Utils.logWarning("No Vault plugin found!");
        }
        if (!setupLuckPerms()) {
            Utils.logWarning("No LuckPerms plugin found!",
                    "This is fine if that's not installed");
        }
    }

    private void commandsSetup() {
        // admin commands
        getCommand("magmabuildnetwork").setExecutor(new CommandMagmabuildnetwork());
        getCommand("debug").setExecutor(new CommandDebug());
        getCommand("freeze").setExecutor(new PlayerCommand());
        getCommand("heal").setExecutor(new PlayerCommand());
        getCommand("ignite").setExecutor(new PlayerCommand());
        getCommand("store").setExecutor(new CommandStore());
        getCommand("vanish").setExecutor(new CommandVanish());
        getCommand("invsee").setExecutor(new PlayerCommand());
        getCommand("ban").setExecutor(new CommandBan());
        getCommand("fly").setExecutor(new CommandFly());
        // basic commands
        getCommand("lock").setExecutor(new CommandLock());
        getCommand("stats").setExecutor(new PlayerCommand());
        getCommand("trade").setExecutor(new CommandTrade());
        getCommand("trails").setExecutor(new PlayerCommand());
        getCommand("ally").setExecutor(new PlayerCommand()); // todo
        getCommand("spawn").setExecutor(new CommandSpawn());
        getCommand("prefix").setExecutor(new PlayerCommand());
        getCommand("shop").setExecutor(new PlayerCommand());
        getCommand("home").setExecutor(new CommandHome());
        getCommand("safechest").setExecutor(new CommandSafechest());
    }

    private void eventsSetup() {
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
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (rsp != null) {
            eco = rsp.getProvider();
        }
        return eco != null;
    }

    private boolean setupChat() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }
        return chat != null;
    }

    private boolean setupLuckPerms() {
        if (getServer().getPluginManager().getPlugin("LuckPerms") == null) {
            return false;
        }
        RegisteredServiceProvider<LuckPerms> rsp = getServer().getServicesManager().getRegistration(net.luckperms.api.LuckPerms.class);
        if (rsp != null) {
            api = rsp.getProvider();
        }
        return api != null;
    }

    public static LuckPerms getApi() {
        return api;
    }

    public static Economy getEco() {
        return eco;
    }

    public static Chat getChat() {
        return chat;
    }
}