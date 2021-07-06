package io.github.FourteenBrush.MagmaBuildNetwork.data;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StatTab implements TabCompleter {

    List<String> arguments = new ArrayList<String>();

    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("lock")) {
            if (arguments.isEmpty()) {
                arguments.add("cancel");
                arguments.add("set");
                arguments.add("remove");
                arguments.add("info");
            }

            List<String> result = new ArrayList<String>();
            if (args.length == 1) {
                for (String a : arguments) {
                    if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                        result.add(a);
                }
                return result;
            }
        }

        else if (cmd.getName().equalsIgnoreCase("debug")) {
            if (arguments.isEmpty()) {
                arguments.add("playersWantingLock");
                arguments.add("tradegui");
            }

            List<String> result = new ArrayList<String>();
            if (args.length == 1) {
                for (String a : arguments) {
                    if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                        result.add(a);
                }
                return result;
            }
        }

        else if (cmd.getName().equalsIgnoreCase("trade")) {
            if (arguments.isEmpty()) {
                arguments.add("accept");
                arguments.add("request");
                arguments.add("decline");
            }

            List<String> result = new ArrayList<>();
            if (args.length == 1) {
                for (String a : arguments) {
                    if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                        result.add(a);
                }
                return result;
            }
        }

        return null;
    }
}
