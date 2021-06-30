package io.github.FourteenBrush.MagmaBuildNetwork;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.ConsoleCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.TradeCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.data.DataManager;
import io.github.FourteenBrush.MagmaBuildNetwork.data.ImageManager;
import io.github.FourteenBrush.MagmaBuildNetwork.data.PacketReader;
import io.github.FourteenBrush.MagmaBuildNetwork.data.StatTab;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.LockListener;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.PlayerCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.PlayerListener;
import io.github.FourteenBrush.MagmaBuildNetwork.updatechecker.UpdateChecker;
import io.github.FourteenBrush.MagmaBuildNetwork.inventory.GUI;
import io.github.FourteenBrush.MagmaBuildNetwork.util.ScoreboardHandler;
import io.github.FourteenBrush.MagmaBuildNetwork.util.Utils;
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

    public Economy eco;
    public LuckPerms api;
    private static Main instance;
    private static DataManager data;

    @Override
    public void onEnable() {
        instance = this;
        initialSetup();
        commandsSetup();
        eventsSetup();
        dependenciesSetup();
        Utils.logInfo("Done!");
    }

    @Override
    public void onDisable() {
        Utils.logInfo(new String[] {"Stopping...", "Goodbye!"});
        instance = null;
        stopNPC();
        super.onDisable();
    }

    private void commandsSetup() {
        this.getCommand("reload").setExecutor(new PlayerCommand());
        this.getCommand("ignite").setExecutor(new PlayerCommand());
        this.getCommand("lock").setExecutor(new PlayerCommand());
        this.getCommand("lock").setTabCompleter(new StatTab());
        this.getCommand("freeze").setExecutor(new PlayerCommand());
        this.getCommand("heal").setExecutor(new PlayerCommand());
        this.getCommand("store").setExecutor(new PlayerCommand());
        this.getCommand("stats").setExecutor(new PlayerCommand());
        this.getCommand("MagmaBuildNetwork").setExecutor(new PlayerCommand());
        this.getCommand("spawnnpc").setExecutor(new PlayerCommand());
        this.getCommand("trails").setExecutor(new PlayerCommand());
        this.getCommand("trade").setExecutor(new TradeCommand());
        this.getCommand("createmap").setExecutor(new PlayerCommand());
        this.getCommand("console").setExecutor(new ConsoleCommand());
    }

    private void initialSetup() {
        Utils.logInfo(new String[] {"Initializing...", "Version " + instance.getDescription().getVersion() + " is activated"});
        data = new DataManager();
        new GUI().registerTrails();
        new ImageManager().init();
        if (data.getConfig().contains("npc_data"))
            loadNPC();
        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                PacketReader reader = new PacketReader();
                reader.inject(p);
                ScoreboardHandler.createScoreboard(p);
            }
        }
    }

    private void dependenciesSetup() {
      // Setup Economy
        RegisteredServiceProvider<Economy> economy = getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economy != null)
            eco = economy.getProvider();
        if (eco == null) {
            Utils.logInfo("You must have Vault installed and an Economy plugin!");
            getServer().getPluginManager().disablePlugin(this);
        }
       // Setup Luckperms
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager()
                .getRegistration(LuckPerms.class);
        if (provider != null) {
            api = provider.getProvider();
        }
    }

    private void eventsSetup() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(), this);
        pm.registerEvents(new LockListener(), this);
        pm.registerEvents(new UpdateChecker(), this);
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

    public static FileConfiguration getData() {
        return data.getConfig();
    }

    public static void saveData() {
        data.saveConfig();
    }
}