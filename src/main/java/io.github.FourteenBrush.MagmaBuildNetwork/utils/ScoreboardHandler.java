package io.github.FourteenBrush.MagmaBuildNetwork.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardHandler {

    public void createScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective obj = board.registerNewObjective("MagmaBuildNetwork", "dummy", ChatColor.GOLD + "Magma Build Network");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score score = obj.getScore("=-=-=-=-=-=-=-=-=-=");
        score.setScore(3);
        Score score2 = obj.getScore(ChatColor.AQUA + "Online players:" + ChatColor.WHITE + Bukkit.getOnlinePlayers().size());
        score.setScore(2);
        Score score3 = obj.getScore("=-=-=-=-=-=-=-=-=-=");
        score.setScore(3);

    }
}
