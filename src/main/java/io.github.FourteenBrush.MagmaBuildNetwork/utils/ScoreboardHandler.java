package io.github.FourteenBrush.MagmaBuildNetwork.utils;

import io.github.FourteenBrush.MagmaBuildNetwork.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardHandler {

    private final static Main plugin = Main.getInstance();

    public static Scoreboard createScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();

        Objective obj = board.registerNewObjective("mbn", "dummy", "§6Magma Build Network");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score score = obj.getScore(" ");
        score.setScore(13);

        Score score1 = obj.getScore("§eLevel:");
        score1.setScore(12);

        String level = String.valueOf((int) player.getExp() / 30);
        Score score2 = obj.getScore("§f" + level);
        score2.setScore(11);

        Score score3 = obj.getScore(" ");
        score3.setScore(10);

        Score score4 = obj.getScore("§eCredits:");
        score4.setScore(9);

        return board;
    }
}

// Magma Build Network
//                      (13)
// Level:               (12)
// 1                    (11)

