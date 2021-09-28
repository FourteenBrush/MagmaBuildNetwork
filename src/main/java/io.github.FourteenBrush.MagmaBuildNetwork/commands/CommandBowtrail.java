package io.github.FourteenBrush.MagmaBuildNetwork.commands;

import io.github.FourteenBrush.MagmaBuildNetwork.particles.Trail;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.MessagesUtils;
import io.github.FourteenBrush.MagmaBuildNetwork.utils.Utils;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

public class CommandBowtrail extends AbstractCommand {

    public CommandBowtrail() {
        super("bowtrail", false);
    }

    @Override
    public boolean execute(@NotNull String[] args) {

        if (isConsole) return MessagesUtils.noConsole(sender);

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("disable")) {
                if (Trail.getTrails().hasTrail(executor)) Trail.getTrails().removeTrail(executor);
                Utils.message(executor, "&aBow trail disabled");
            } else {
                Utils.message(executor, "&cYou don't have a bow trail fitted!");
                return true;
            }
            Particle particle = Particle.valueOf(args[0].toUpperCase());
            if (particle != null) {
                Trail.getTrails().addTrail(executor, particle);
                Utils.message(executor, "&aTrail set! " + particle.name());
            }
        } else {
            Utils.message(executor, String.join(Utils.getFinalArgs(Particle.values().toString().split(" "), 0)));
        }
        return true;
    }
}
