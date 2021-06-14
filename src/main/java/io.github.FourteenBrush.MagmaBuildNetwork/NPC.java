package io.github.FourteenBrush.MagmaBuildNetwork;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPC {

    private static List<EntityPlayer> NPC = new ArrayList<EntityPlayer>();

    public static void createNPC(Player p, String skin) {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) Bukkit.getWorld(p.getWorld().getName())).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), ChatColor.DARK_AQUA + "" + ChatColor.BOLD + skin);
        EntityPlayer npc = new EntityPlayer(server, world, gameProfile, new PlayerInteractManager(world));
        npc.setLocation(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());

        String[] name = getSkin(p, skin);
        gameProfile.getProperties().put("textures", new Property("textures", name[0], name[1]));

        addNPCPacket(npc);
        NPC.add(npc);

        // saves the players position
        int var = 1;
        if (Main.getData().contains("data")) {
            var = Main.getData().getConfigurationSection("npc_data").getKeys(false).size() + 1;
        }
        Main.getData().set("npc_data." + var + ".x", (int) p.getLocation().getX());
        Main.getData().set("npc_data." + var + ".y", (int) p.getLocation().getY());
        Main.getData().set("npc_data." + var + ".z", (int) p.getLocation().getZ());
        Main.getData().set("npc_data." + var + ".p", p.getLocation().getPitch());
        Main.getData().set("npc_data." + var + ".yaw", p.getLocation().getYaw());
        Main.getData().set("npc_data." + var + ".world", p.getLocation().getWorld().getName());
        Main.getData().set("npc_data." + var + ".name", skin);
        Main.getData().set("npc_data." + var + ".text", name[0]);
        Main.getData().set("npc_data." + var + ".signature", name[1]);
        // recently added
        Main.getData().set("npc_data" + var + ".uuid", gameProfile.getId().toString());
        Main.saveData();

    }

    public static void loadNPC(Location location, GameProfile profile) {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        GameProfile gameProfile = profile;
        EntityPlayer npc = new EntityPlayer(server, world, gameProfile, new PlayerInteractManager(world));
        npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        addNPCPacket(npc);
        NPC.add(npc);
    }

    private static String[] getSkin(Player p, String name) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();

            URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid
                    + "?unsigned=false");
            InputStreamReader reader2 = new InputStreamReader(url2.openStream());
            JsonObject property = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray()
                    .get(0).getAsJsonObject();
            String texture = property.get("value").getAsString();
            String signature = property.get("signature").getAsString();
            return new String[] {texture, signature};
            /*
             * player:
             *   properties:
             *     signature: dahdyua
             *     value: ahsaiushaius
             */
        } catch (Exception e) {
            EntityPlayer player = ((CraftPlayer)p).getHandle();
            GameProfile profile = player.getProfile();
            Property property = profile.getProperties().get("textures").iterator().next();
            String texture = property.getValue();
            String signature = property.getSignature();
            return new String[] {texture, signature};
        }
    }

    public static void addNPCPacket(EntityPlayer npc) {
        for (Player p :Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));
        }
    }

    public static void removeNPC(Player player, EntityPlayer npc) {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
    }

    public static void addJoinPacket(Player p) {
        for (EntityPlayer npc : NPC) {
            PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));
        }
    }

    public static List<EntityPlayer> getNPCs() {
        return NPC;
    }
}
