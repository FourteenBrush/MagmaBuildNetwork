package io.github.FourteenBrush.MagmaBuildNetwork.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Trail {

    private final Particle particle;
    private final ArrayList<Arrow> arrows;
    private final Map<UUID, Trail> players;
    private static Trail arrowTrails;

    public Trail(Particle particle) {
        this.particle = particle;
        arrows = new ArrayList<>();
        players = new HashMap<>();
    }

    public void addArrow(Arrow arrow) {
        arrows.add(arrow);
    }

    public void tick() {
        for (Arrow a : arrows) {
            if (a.isOnGround() || a.isDead() || a == null) {
                arrows.remove(a);
                return;
            } else {
                particle(a.getLocation());
            }
        }
    }

    private void particle(Location loc) {
        loc.getWorld().spawnParticle(particle, loc, 1);
    }

    public void addTrail(Player player, Particle particle) {
        players.put(player.getUniqueId(), new Trail(particle));
    }

    public Trail getTrail(Player player) {
        return players.get(player.getUniqueId());
    }

    public static Trail getTrails() {
        return arrowTrails;
    }

    public void removeTrail(Player player) {
        players.remove(player.getUniqueId());
    }

    public boolean hasTrail(Player player) {
        return players.containsKey(player.getUniqueId());
    }
}
