package com.graywolf336.jail.command.subcommands;

import org.bukkit.command.CommandSender;

import com.graywolf336.jail.JailManager;
import com.graywolf336.jail.command.Command;
import com.graywolf336.jail.command.CommandInfo;

@CommandInfo(
		maxArgs = 0,
		minimumArgs = 0,
		needsPlayer = false,
		pattern = "version|v",
		permission = "jail.command.jailversion",
		usage = "/jail version"
	)
public class JailVersionCommand implements Command{
	
	public boolean execute(JailManager jm, CommandSender sender, String... args) {
		// Sends the version number to the sender
		sender.sendMessage("Jail Version: " + jm.getPlugin().getDescription().getVersion());
		
		return true;
	}

}
