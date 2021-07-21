package io.github.FourteenBrush.MagmaBuildNetwork;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandBan;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandCreatemap;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandDebug;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandLock;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandMagmabuildnetwork;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandSpawn;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandStore;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandTrade;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandVanish;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.PlayerCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.data.ConfigManager;
import io.github.FourteenBrush.MagmaBuildNetwork.data.ImageManager;
import io.github.FourteenBrush.MagmaBuildNetwork.data.PacketReader;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.LockListener;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.PlayerListener;
import io.github.FourteenBrush.MagmaBuildNetwork.spawn.Spawn;
import io.github.FourteenBrush.MagmaBuildNetwork.updatechecker.UpdateChecker;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.NPC;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.chat.Chat;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

import java.util.UUID;

public class Main extends JavaPlugin {

    private static Main instance;
    private static LuckPerms api = null;
    private static Economy eco = null;
    private static Chat chat = null;

    private static boolean vaultActivated = false;

    @Override
    public void onEnable() {
        instance = this;
        startup();
        commandsSetup();
        eventsSetup();
        Utils.logInfo(new String[] {"Version " + getDescription().getVersion() + " is activated!", "Done!"});
    }

    @Override
    public void onDisable() {
        Utils.logInfo(new String[] {"Stopping...", "Goodbye!"});
        instance = null;
        stopNPC();
    }

    private void startup() {
        Utils.logInfo("Initializing...");
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
        if (Spawn.getLocation() == null) {
            Spawn.setLocation(getServer().getWorlds().get(0).getSpawnLocation());
        }
        if (ConfigManager.getConfigConfig().contains("npc_data")) {
            loadNPC();
        }
        new ImageManager().init();
        // in case of reload
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
    }

    public static boolean isReloading() {
        return Bukkit.getWorlds().size() != 0;
    }

    private void eventsSetup() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(), this);
        pm.registerEvents(new LockListener(), this);
        pm.registerEvents(new UpdateChecker(), this);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            eco = rsp.getProvider();
        }
        vaultActivated = true;
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
        vaultActivated = true;
        return (chat != null);
    }

    private boolean setupLuckPerms() {
       if (getServer().getPluginManager().getPlugin("LuckPerms") == null) {
           return false;
       }
       RegisteredServiceProvider<LuckPerms> rsp = getServer().getServicesManager().getRegistration(LuckPerms.class);
       if (rsp == null) {
           return false;
       }
       api = rsp.getProvider();
       return api != null;
    }

    private void loadNPC() {
        FileConfiguration file = ConfigManager.getConfigConfig();
        file.getConfigurationSection("npc_data").getKeys(false).forEach(npc -> {
            Location location = new Location(Bukkit.getWorld(
                    file.getString("npc_data." + npc + ".world")),
                    file.getInt("npc_data." + npc + ".x"), file.getInt("npc_data." + npc + ".y"),
                    file.getInt("npc_data." + npc + ".z"));
            location.setPitch((float) file.getDouble("npc_data." + npc + ".p"));
            location.setYaw((float) file.getDouble("npc_data." + npc + ".yaw"));

            String name = file.getString("npc_data." + npc + ".name");
            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), ChatColor.DARK_AQUA + "" + ChatColor.BOLD + name);
            gameProfile.getProperties().put("textures", new Property("textures", file.getString("npc_data." + npc + ".text"),
                    file.getString("npc_data." + npc + ".signature")));
            NPC.loadNPC(location, gameProfile);
        });
    }

    private void stopNPC() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            PacketReader reader = new PacketReader();
            reader.unInject(p);
            for (EntityPlayer npc : NPC.getNPCs()) {
                NPC.removeNPC(p, npc);
            }
        }
    }

    public static Main getInstance() {
        return instance;
    }

    public static Economy getEco() {
        return eco;
    }

    public static Chat getChat() {
        return chat;
    }

    public static LuckPerms getApi() {
        return api;
    }

    public static boolean getVaultActivated() {
        return vaultActivated;
    }
}