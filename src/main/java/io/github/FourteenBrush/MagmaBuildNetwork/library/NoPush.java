package io.github.FourteenBrush.MagmaBuildNetwork.library;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class NoPush {

    public static void setCantPush(Player player) {
        Team team = player.getScoreboard().getTeam("NoPush");
        if (team == null) {
            team = player.getScoreboard().registerNewTeam("NoPush");
        }
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.addEntry(player.getName());
    }

    public static void setCanPush(Player player) {
        Team team = player.getScoreboard().getTeam("NoPush");
        if (team != null)
            team.removeEntry(player.getName());
    }
}
