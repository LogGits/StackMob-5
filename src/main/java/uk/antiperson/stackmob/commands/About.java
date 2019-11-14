package uk.antiperson.stackmob.commands;

import org.bukkit.command.CommandSender;
import uk.antiperson.stackmob.StackMob;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class About extends SubCommand {

    private StackMob sm;
    public About(StackMob sm) {
        super("about");
        this.sm = sm;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        sender.sendMessage("StackMob version " + sm.getDescription().getVersion());
        sender.sendMessage("Report bugs/missing features at https://github.com/Nathat23/StackMob-5");
        return false;
    }
}
