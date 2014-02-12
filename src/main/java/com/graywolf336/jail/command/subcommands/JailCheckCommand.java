package com.graywolf336.jail.command.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.graywolf336.jail.JailManager;
import com.graywolf336.jail.beans.Prisoner;
import com.graywolf336.jail.command.Command;
import com.graywolf336.jail.command.CommandInfo;
import com.graywolf336.jail.enums.LangString;

@CommandInfo(
		maxArgs = 1,
		minimumArgs = 1,
		needsPlayer = false,
		pattern = "check",
		permission = "jail.command.jailcheck",
		usage = "/jail check <playername>"
	)
public class JailCheckCommand implements Command{

	// Checks the status of the specified prisoner
	public boolean execute(JailManager jm, CommandSender sender, String... args) {
		//Otherwise let's check the first argument
		if(jm.isPlayerJailed(args[1])) {
			Prisoner p = jm.getPrisoner(args[1]);
			
			//graywolf663: Being gray's evil twin; CONSOLE (10)
			//prisoner: reason; jailer (time in minutes)
			sender.sendMessage(ChatColor.BLUE + " " + p.getName() + ": " + p.getReason() + "; " + p.getJailer() + " (" + p.getRemainingTimeInMinutes() + " mins)");
		}else {
			sender.sendMessage(jm.getPlugin().getJailIO().getLanguageString(LangString.NOTJAILED, args[1]));
		}
		
		return true;
	}

}
