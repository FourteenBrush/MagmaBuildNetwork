package io.github.FourteenBrush.MagmaBuildNetwork;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.ConsoleCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.GlobalCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.TradeCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.data.DataManager;
import io.github.FourteenBrush.MagmaBuildNetwork.data.ImageManager;
import io.github.FourteenBrush.MagmaBuildNetwork.data.PacketReader;
import io.github.FourteenBrush.MagmaBuildNetwork.data.StatTab;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.LockListener;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.PlayerCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.PlayerListener;
import io.github.FourteenBrush.MagmaBuildNetwork.updatechecker.UpdateChecker;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.NPC;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.ScoreboardHandler;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import net.luckperms.api.LuckPerms;
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

    private static LuckPerms api;
    private static Main instance;
    private static DataManager data;
    private static Economy eco = null;

    @Override
    public void onEnable() {
        instance = this;
        initialSetup();
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

    private void initialSetup() {
        Utils.logInfo("Initializing...");
        if (!setupEconomy()) {
            Utils.logWarning(new String[] {"Now Vault or economy plugin found!",
            "This is fine if that's not installed"});
        }
        data = new DataManager();
        new ImageManager().init();
        if (data.getConfig().contains("npc_data"))
            loadNPC();
        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                PacketReader reader = new PacketReader();
                reader.inject(p);
            }
        }
    }

    private void commandsSetup() {
        getCommand("reload").setExecutor(new PlayerCommand());
        getCommand("ignite").setExecutor(new PlayerCommand());
        getCommand("lock").setExecutor(new PlayerCommand());
        getCommand("lock").setTabCompleter(new StatTab());
        getCommand("freeze").setExecutor(new PlayerCommand());
        getCommand("heal").setExecutor(new PlayerCommand());
        getCommand("store").setExecutor(new PlayerCommand());
        getCommand("stats").setExecutor(new PlayerCommand());
        getCommand("magmabuildnetwork").setExecutor(new GlobalCommand());
        getCommand("spawnnpc").setExecutor(new PlayerCommand());
        getCommand("trails").setExecutor(new PlayerCommand());
        getCommand("trade").setExecutor(new TradeCommand());
        getCommand("trade").setTabCompleter(new StatTab());
        getCommand("createmap").setExecutor(new PlayerCommand());
        getCommand("console").setExecutor(new ConsoleCommand());
        getCommand("debug").setExecutor(new PlayerCommand());
        getCommand("debug").setTabCompleter(new StatTab());
        getCommand("invsee").setExecutor(new PlayerCommand());
        getCommand("ally").setExecutor(new PlayerCommand());
        getCommand("vanish").setExecutor(new PlayerCommand());
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
        if (rsp == null) {
            return false;
        }
        eco = rsp.getProvider();
        return eco != null;
    }

    // TODO
    private void setupLuckPerms() {
        if (getConfig().getBoolean("requires_luckperms") &&
        Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
            getServer().getPluginManager().disablePlugin(this);
        }
        else if (getConfig().getBoolean("requires_luckperms") &&
        Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
                RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager()
                        .getRegistration(LuckPerms.class);
                if (provider != null) {
                    api = provider.getProvider();
                }
            }
        }
    }

    private void loadNPC() {
        FileConfiguration file = data.getConfig();
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


    public static void saveData() {
        data.saveConfig();
    }
}