package io.github.FourteenBrush.MagmaBuildNetwork.utils;

import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandFly;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandTrade;
import io.github.FourteenBrush.MagmaBuildNetwork.commands.CommandVanish;

public interface Instances {

    CommandFly COMMAND_FLY = new CommandFly();
    CommandVanish COMMAND_VANISH = new CommandVanish();
    CommandTrade COMMAND_TRADE = new CommandTrade();
}
