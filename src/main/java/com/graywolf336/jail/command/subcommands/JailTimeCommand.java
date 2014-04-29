package com.graywolf336.jail.command.subcommands;

import org.bukkit.command.CommandSender;

import com.graywolf336.jail.JailManager;
import com.graywolf336.jail.Util;
import com.graywolf336.jail.beans.Prisoner;
import com.graywolf336.jail.command.Command;
import com.graywolf336.jail.command.CommandInfo;
import com.graywolf336.jail.enums.LangString;

@CommandInfo(
		maxArgs = 3,
		minimumArgs = 2,
		needsPlayer = false,
		pattern = "time|t",
		permission = "jail.command.jailtime",
		usage = "/jail time <add|remove|show> <player> <time>"
	)
public class JailTimeCommand implements Command {
	public boolean execute(JailManager jm, CommandSender sender, String... args) throws Exception {
		if(jm.isPlayerJailedByLastKnownUsername(args[2])) {
			Prisoner p = jm.getPrisonerByLastKnownName(args[2]);
			
			switch(args.length) {
				case 3:
					if(args[1].equalsIgnoreCase("show")) {
						sender.sendMessage(jm.getPlugin().getJailIO().getLanguageString(LangString.PRISONERSTIME,
								new String[] { p.getLastKnownName(), String.valueOf(p.getRemainingTimeInMinutes()) }));
					}else {
						return false;
					}
					break;
				case 4:
					if(args[1].equalsIgnoreCase("add")) {
						p.addTime(Util.getTime(args[3]));
					}else if(args[1].equalsIgnoreCase("remove")) {
						p.subtractTime(Util.getTime(args[3]));
					}else {
						return false;
					}
					
					sender.sendMessage(jm.getPlugin().getJailIO().getLanguageString(LangString.PRISONERSTIME,
							new String[] { p.getLastKnownName(), String.valueOf(p.getRemainingTimeInMinutes()) }));
					break;
				default:
					return false;
			}
		}else {
			sender.sendMessage(jm.getPlugin().getJailIO().getLanguageString(LangString.NOTJAILED, args[2]));
		}
		
		return true;
	}
}
