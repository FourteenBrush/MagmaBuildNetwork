package io.github.FourteenBrush.MagmaBuildNetwork.user;

import io.github.FourteenBrush.MagmaBuildNetwork.user.profiles.ChatProfile;
import io.github.FourteenBrush.MagmaBuildNetwork.user.profiles.MembershipProfile;
import io.github.FourteenBrush.MagmaBuildNetwork.user.profiles.StatisticsProfile;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.text.DecimalFormat;
import java.util.UUID;

public class User {
    private final UUID uuid;
    private final BukkitRunnable actionbar;
    private final ChatProfile chatProfile;
    private final StatisticsProfile statisticsProfile;
    private final MembershipProfile membershipProfile;

    /**
     * Constructs a new user with a given uuid, statistics - and membership profile
     * This object itself is immutable shouldn't be stored somewhere permanently because it stores data
     * which may expire, unless this is kept in a kind of cache with a decent removal
     * listener it can be stored (cached)
     * @param uuid the uuid which acts as an identifier
     * @param statisticsProfile the statistics profile (more on {@link #getStatisticsProfile()})
     * @param membershipProfile the membership profile (more on {@link #getMembershipProfile()})
     */
    public User(UUID uuid, StatisticsProfile statisticsProfile, MembershipProfile membershipProfile) {
        this.uuid = uuid;
        this.actionbar = new Actionbar();
        this.chatProfile = new ChatProfile(this);
        this.statisticsProfile = statisticsProfile;
        this.membershipProfile = membershipProfile;
        disableCollision();
    }

    public User(UUID uuid, StatisticsProfile statisticsProfile) {
        this(uuid, statisticsProfile, null);
        // todo fix membership class
    }

    /**
     * Returns the uuid of this user object, this is the same as the uuid of the player object this is bound to
     * @return the user's uuid
     * @see Player#getUniqueId()
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Gets the player object this user refers to or null if the player went offline, this shouldn't happen and
     * this user should be finalized by default to prevent invalid user objects hanging around.
     * This is strongly maintained by the cache implementation
     * Calling this method should be avoided as much as possible, use direct player objects instead
     * @return the actual player object, or null if this user has become invalid
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * Gets the chat profile for this user, including the channels, ranks, etc.
     * @return the chat profile
     */
    public ChatProfile getChatProfile() {
        return chatProfile;
    }

    /**
     * Gets the statistics profile for this user, including online-time, level, etc.
     * @return the statistics profile
     */
    public StatisticsProfile getStatisticsProfile() {
        return statisticsProfile;
    }

    /**
     * gets the membership profile of this user, including the user's kingdom, kingdom ranks, etc.
     * @return the membership profile
     */
    public MembershipProfile getMembershipProfile() {
        return membershipProfile;
    }

    /**
     * Sends a message to the current user
     * This should be avoided as much as possible and {@link Player#sendMessage(String)} or
     * {@link PlayerUtils#message(CommandSender, String...)} should be used where possible
     * @param message a message which may include color codes
     */
    public void sendMessage(String message) {
        PlayerUtils.message(getPlayer(), message);
    }

    /**
     * This method should be called when logging out the user
     * This method should only be called on the time the user is leaving the server or the plugin is disabling
     */
    public void logoutSafely() {
        actionbar.cancel();
    }

    private void disableCollision() {
        Player player = getPlayer();
        Team team = player.getScoreboard().getTeam("mbn");
        if (team == null) {
            team = player.getScoreboard().registerNewTeam("mbn");
        }
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.addEntry(player.getName());
    }

    private class Actionbar extends BukkitRunnable {
        private final Player player;
        private final DecimalFormat df;

        public Actionbar() {
            this.player = getPlayer();
            df = new DecimalFormat("#.##");
        }

        @Override
        public void run() {
            String message = Utils.colorize("&a&lHP&r " + df.format(player.getHealth() * 5) + " / 100");
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        }
    }
}
