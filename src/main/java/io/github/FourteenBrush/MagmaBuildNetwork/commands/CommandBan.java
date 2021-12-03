package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.managers.CommandHandler;
import io.github.FourteenBrush.MagmaBuildNetwork.database.DatabaseFactory;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Lang;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Permission;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class CommandBan extends CommandHandler implements IConsoleCommand {

    public CommandBan() {
        super("ban", Permission.ADMIN, true);
    }

    @Override
    public boolean execute(@NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Lang.SPECIFY_A_PLAYER.get());
        } else {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (!PlayerUtils.checkPlayerOnline(sender, target)) return true;
            String banReason = Utils.colorize(args.length > 1 ? Utils.getFinalArgs(args, 1) : "&c&lBanned from the server");
            boolean silent = args[args.length - 1].equalsIgnoreCase("-s");
            try (Connection conn = DatabaseFactory.getDatabase().getConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO tblbans(uuid, reason, banned_by) VALUES(?, ?, ?);")) {
                ps.setString(1, target.getUniqueId().toString());
                ps.setString(2, banReason);
                ps.setString(3, sender.getName());
                ps.execute();
            } catch (SQLException e) {
                PlayerUtils.message(sender, "&cFailed to add ban to the database! Manuel operation succeeded!");
                e.printStackTrace();
            }
            plugin.getServer().getBanList(BanList.Type.NAME).addBan(target.getName(), banReason, null, null);
            target.kickPlayer(banReason);
            PlayerUtils.message(sender, String.format("&6Banned %s | %s", target.getName(), banReason));
            Utils.logWarning("Player " + target.getName() + " got banned by " + sender.getName());
            if (!silent)
                Bukkit.broadcastMessage(String.format("&cPlayer &6%s&c got banned by &6%s&c | &6%s", target.getName(), sender.getName(), banReason));
        }
        return true;
    }

    @Override
    public List<String> tabComplete(@NotNull String[] args) {
        if (args.length == 1) {
            return null;
        } else if (args.length > 1) {
            return Collections.singletonList("-s");
        }
        return super.tabComplete(args);
    }
}
