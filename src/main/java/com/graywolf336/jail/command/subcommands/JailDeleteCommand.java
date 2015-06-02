package com.graywolf336.jail.command.subcommands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.graywolf336.jail.JailManager;
import com.graywolf336.jail.beans.ConfirmPlayer;
import com.graywolf336.jail.command.Command;
import com.graywolf336.jail.command.CommandInfo;
import com.graywolf336.jail.enums.Confirmation;
import com.graywolf336.jail.enums.Lang;

@CommandInfo(
        maxArgs = 1,
        minimumArgs = 1,
        needsPlayer = false,
        pattern = "delete|d",
        permission = "jail.command.jaildelete",
        usage = "/jail delete [jail]"
        )
public class JailDeleteCommand implements Command {
    public boolean execute(JailManager jm, CommandSender sender, String... args) throws Exception {
        if(jm.isConfirming(sender.getName())) {
            sender.sendMessage(Lang.ALREADY.get());
        }else {
            jm.addConfirming(sender.getName(), new ConfirmPlayer(sender.getName(), args, Confirmation.DELETE));
            sender.sendMessage(Lang.START.get());
        }

        return true;
    }

    public List<String> provideTabCompletions(JailManager jm, CommandSender sender, String... args) throws Exception {
        return jm.getJailsByPrefix(args.length == 2 ? args[1] : "");
    }
}
