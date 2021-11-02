package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.database.DatabaseFactory;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Permission;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.PlayerUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class CommandBan extends AbstractCommand implements ConsoleCommand {

    private static String banReason;

    public CommandBan() {
        super("ban", Permission.ADMIN, true);
    }

    @Override
    public boolean execute(@NotNull String[] args) throws SQLException {
        if (args.length < 1) {
            PlayerUtils.message(sender, "&cPlease specify a player!");
            return true;
        }
        boolean silent = args[args.length - 1].equalsIgnoreCase("-s");
        Player target = Bukkit.getPlayerExact(args[0]);
        if (!PlayerUtils.checkPlayerOnline(sender, target, false)) return true;
        banReason = Utils.colorize(args.length > 1 ? Utils.getFinalArgs(args, 1) : "&c&lBanned by a moderator");
        PreparedStatement ps = DatabaseFactory.getDatabase().getPreparedStatement("INSERT INTO mbn_bans(uuid, reason, banned_by) VALUES(?, ? , ?);");
        ps.setString(1, target.getUniqueId().toString());
        ps.setString(2, banReason);
        ps.setString(3, sender.getName());
        ps.executeUpdate();
        DatabaseFactory.getDatabase().closeStatement(ps);
        plugin.getServer().getBanList(BanList.Type.NAME).addBan(target.getName(), banReason, null, executor != null ? executor.getName() : "Banned by console");
        target.kickPlayer(banReason);
        PlayerUtils.message(sender, String.format("&6Banned %s | %s", target.getName(), banReason));
        Utils.logWarning("Player " + target.getName() + " got banned by " + sender.getName());
        if (!silent)
            PlayerUtils.broadcast(String.format("&cPlayer &6%s&c got banned by &6%s&c | &6%s", target.getName(), sender.getName(), banReason), true);
        return true;
    }

    public static String getBanReason() {
        return banReason;
    }

    @Override
    protected List<String> tabComplete(@NotNull String[] args) {
        return args.length > 1 ? Collections.singletonList("-s") : super.tabComplete();
    }
}
