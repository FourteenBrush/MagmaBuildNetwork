package io.github.FourteenBrush.MagmaBuildNetwork;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.*;
import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.data.PacketReader;
import io.github.FourteenBrush.MagmaBuildNetwork.database.MySQL;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.InventoryListener;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.LockListener;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.PlayerListener;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.VaultListener;
import io.github.FourteenBrush.MagmaBuildNetwork.spawn.Spawn;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.NPC;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

import java.sql.SQLException;

public class Main extends JavaPlugin {

    private static Main instance;

    public MySQL mySQL;
    private static LuckPerms api = null;
    private static Economy eco = null;
    private static Chat chat = null;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        startup();
        commandsSetup();
        eventsSetup();
        Utils.logInfo(new String[]{"Version " + getDescription().getVersion() + " is activated!", "Done!"});
    }

    @Override
    public void onDisable() {
        Utils.logInfo(new String[]{"Stopping...", "Goodbye!"});
        CommandManager.onDisable();
        NPC.stopNPC();
        mySQL.disconnect();
        instance = null;
    }

    private void startup() {
        Utils.logInfo("Initializing...");
        ConfigManager.createFiles();
        mySQL = new MySQL();
        try {
            mySQL.connect();
        } catch (ClassNotFoundException | SQLException e) {
            Utils.logInfo("Database not connected");
        }
        if (mySQL.isConnected()) {
            Utils.logInfo("Database connected");
        }
        CommandManager.onEnable();
        if (!setupEconomy()) {
            Utils.logWarning(new String[]{"No Vault or economy plugin found!",
                    "This is fine if that's not installed"});
        }
        if (!setupChat()) {
            Utils.logWarning("No Vault plugin found!");
        }
        if (!setupLuckPerms()) {
            Utils.logWarning(new String[]{"No LuckPerms plugin found!",
                    "This is fine if that's not installed"});
        }
        if (Spawn.getLocation() == null) { // todo debug
            Spawn.setLocation(getServer().getWorlds().get(0).getSpawnLocation());
        }
        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                new PacketReader().inject(p);
            }
        }
    }

    private void commandsSetup() {
        // admin commands
        getCommand("magmabuildnetwork").setExecutor(new CommandMagmabuildnetwork());

        getCommand("magmabuildnetwork").setTabCompleter(new CommandMagmabuildnetwork());
        getCommand("debug").setExecutor(new CommandDebug());

        getCommand("debug").setTabCompleter(new CommandDebug());
        getCommand("freeze").setExecutor(new PlayerCommand());
        getCommand("heal").setExecutor(new PlayerCommand());
        getCommand("ignite").setExecutor(new PlayerCommand());
        getCommand("store").setExecutor(new CommandStore());
        getCommand("spawnnpc").setExecutor(new PlayerCommand());
        getCommand("vanish").setExecutor(new CommandVanish());
        getCommand("invsee").setExecutor(new PlayerCommand());
        getCommand("ban").setExecutor(new CommandBan());
        // basic commands
        getCommand("lock").setExecutor(new CommandLock());

        getCommand("lock").setTabCompleter(new CommandLock());
        getCommand("stats").setExecutor(new PlayerCommand());
        getCommand("trade").setExecutor(new CommandTrade());

        getCommand("trade").setTabCompleter(new CommandTrade());
        getCommand("trails").setExecutor(new PlayerCommand());
        getCommand("ally").setExecutor(new PlayerCommand());
        getCommand("spawn").setExecutor(new CommandSpawn());

        getCommand("spawn").setTabCompleter(new CommandSpawn());
        getCommand("prefix").setExecutor(new PlayerCommand());
        getCommand("shop").setExecutor(new PlayerCommand());
        getCommand("home").setExecutor(new CommandHome());

        getCommand("home").setTabCompleter(new CommandHome());
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

    public static Main getInstance() {
        return instance;
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