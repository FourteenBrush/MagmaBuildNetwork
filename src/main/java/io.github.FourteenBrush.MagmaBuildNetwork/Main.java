package io.github.FourteenBrush.MagmaBuildNetwork;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.EconomyListener;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.LockListener;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.TradeListener;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandHandler;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.StorageCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.TradeCommand;
import io.github.FourteenBrush.MagmaBuildNetwork.listeners.PlayerListener;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.GUI;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.ScoreboardHandler;
import net.luckperms.api.LuckPerms;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;


public class Main extends JavaPlugin {

    public Economy eco;
    private static Main instance;
    private static DataManager data;
    private ConsoleCommandSender cmdSender;

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("[" + ChatColor.RED + "MagmaBuildNetwork" + ChatColor.WHITE + "]" + ChatColor.RED + " Loading...");
        if (!setupEconomy()) {
            Bukkit.getConsoleSender().sendMessage("[" + ChatColor.RED + "MagmaBuildNetwork" + ChatColor.WHITE + "]" + ChatColor.RED + " You must have Vault installed and an Economy plugin!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupLP();
        instance = this;
        data = new DataManager();
        GUI gui = new GUI();
        gui.register();

        registerCommands();
        registerEvents();
        if (data.getConfig().contains("npc_data"))
            loadNPC();

        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                PacketReader reader = new PacketReader();
                reader.inject(p);
                ScoreboardHandler.createScoreboard(p);
            }
        }
        Bukkit.getConsoleSender().sendMessage("[" + ChatColor.RED + "MagmaBuildNetwork" + ChatColor.WHITE + "]" + ChatColor.RED + " Finished loading.");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("[" + ChatColor.RED + "MagmaBuildNetwork" + ChatColor.WHITE + "]" + ChatColor.RED + " Stopping...");
        Bukkit.getConsoleSender().sendMessage("[" + ChatColor.RED + "MagmaBuildNetwork" + ChatColor.WHITE + "]" + ChatColor.RED + " Goodbye....");
        instance = null;

        for (Player p : Bukkit.getOnlinePlayers()) {
            PacketReader reader = new PacketReader();
            reader.unInject(p);
            for (EntityPlayer npc : NPC.getNPCs()) {
                NPC.removeNPC(p, npc);
            }
        }
    }

    private void registerCommands() {
        this.getCommand("reload").setExecutor(new CommandHandler());
        this.getCommand("ignite").setExecutor(new CommandHandler());
        this.getCommand("lock").setExecutor(new CommandHandler());
        this.getCommand("lock").setTabCompleter(new StatTab());
        this.getCommand("freeze").setExecutor(new CommandHandler());
        this.getCommand("heal").setExecutor(new CommandHandler());
        this.getCommand("store").setExecutor(new StorageCommand());
        this.getCommand("stats").setExecutor(new CommandHandler());
        this.getCommand("MagmaBuildNetwork").setExecutor(new CommandHandler());
        this.getCommand("trade").setExecutor(new TradeCommand(new TradeListener()));
        this.getCommand("spawnnpc").setExecutor(new CommandHandler());
        this.getCommand("trails").setExecutor(new CommandHandler());
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(), this);
        pm.registerEvents(new TradeListener(), this);
        pm.registerEvents(new LockListener(), this);
        pm.registerEvents(new EconomyListener(), this);
    }

    public void checkVersion() throws IOException {
        URL url = new URL("https://github.com/FourteenBrush/FourteenBrush.github.io");
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

    public void loadNPC() {
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

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economy = getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economy != null)
            eco = economy.getProvider();
        return (eco != null);
    }

    private boolean setupLP() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager()
                .getRegistration(LuckPerms.class);
        if (provider != null) {
            LuckPerms api = provider.getProvider();
        }
        return true;
    }
}