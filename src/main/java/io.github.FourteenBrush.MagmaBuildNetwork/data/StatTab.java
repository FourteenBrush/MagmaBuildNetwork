package io.github.FourteenBrush.MagmaBuildNetwork.data;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class StatTab implements TabCompleter {

    List<String> arguments = new ArrayList<String>();

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
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

        if (cmd.getName().equalsIgnoreCase("debug")) {
            if (arguments.isEmpty()) {
                arguments.add("playersWantingLock");
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
        return null;
    }
}
