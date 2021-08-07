package io.github.FourteenBrush.MagmaBuildNetwork;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.*;
import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.data.ImageManager;
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
    public static LuckPerms api = null;
    public static Economy eco = null;
    public static Chat chat = null;

    @Override
    public void onLoad() {
        instance = this;
        mySQL = new MySQL();
        ConfigManager.createFiles();
    }

    @Override
    public void onEnable() {
        startup();
        commandsSetup();
        eventsSetup();
        Utils.logInfo(new String[] {"Version " + getDescription().getVersion() + " is activated!", "Done!"});
    }

    @Override
    public void onDisable() {
        Utils.logInfo(new String[] {"Stopping...", "Goodbye!"});
        NPC.stopNPC();
        if (!CommandSafechest.getMenus().isEmpty()) {
            CommandSafechest.saveInventories();
        }
        mySQL.disconnect();
        instance = null;
    }

    private void startup() {
        Utils.logInfo("Initializing...");
        try {
            mySQL.connect();
        } catch (ClassNotFoundException | SQLException e) {
            Utils.logInfo("Database not connected");
        }
        if (mySQL.isConnected()) {
            Utils.logInfo("Database connected");
        }
        if (!setupEconomy()) {
            Utils.logWarning(new String[] {"No Vault or economy plugin found!",
            "This is fine if that's not installed"});
        }
        if (!setupChat()) {
            Utils.logWarning("No Vault plugin found!");
        }
        if (!setupLuckPerms()) {
            Utils.logWarning(new String[] {"No LuckPerms plugin found!",
            "This is fine if that's not installed"});
        }
        if (getConfig().contains("npc_data")) {
            NPC.loadNPCIntoWorld();
        }
        if (getConfig().contains("safe_chests")) {
            CommandSafechest.loadInventories();
        }
        if (Spawn.getLocation() == null) {
            Spawn.setLocation(getServer().getWorlds().get(0).getSpawnLocation());
        }
        new ImageManager().init();
        // in case of reload
        if (isReloading()) {
            if (!Bukkit.getOnlinePlayers().isEmpty()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                new PacketReader().inject(p);
            }
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
        getCommand("createmap").setExecutor(new CommandCreatemap());
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

    public static boolean isReloading() {
        return Bukkit.getWorlds().size() != 0;
    }

    private void eventsSetup() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(), this);
        pm.registerEvents(new LockListener(), this);
        pm.registerEvents(new InventoryListener(), this);
        if (getVaultActivated()) {
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

    public static Chat getChat() {
        return chat;
    }

    public static LuckPerms getApi() {
        return api;
    }

    public static boolean getVaultActivated() {
        return Bukkit.getServer().getPluginManager().isPluginEnabled("Vault");
    }

    public static boolean getGemsEconomyActivated() {
        return Bukkit.getServer().getPluginManager().isPluginEnabled("GemsEconomy");
    }

    public static boolean getLPActivated() {
        return Bukkit.getServer().getPluginManager().isPluginEnabled("LuckPerms");
    }
}