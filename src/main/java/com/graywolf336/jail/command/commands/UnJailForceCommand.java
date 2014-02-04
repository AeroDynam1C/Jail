package com.graywolf336.jail.command.commands;

import org.bukkit.command.CommandSender;

import com.graywolf336.jail.JailManager;
import com.graywolf336.jail.command.Command;
import com.graywolf336.jail.command.CommandInfo;
import com.graywolf336.jail.enums.LangString;
import com.graywolf336.jail.enums.Settings;

@CommandInfo(
		maxArgs = 1,
		minimumArgs = 1,
		needsPlayer = false,
		pattern = "unjailforce|ujf",
		permission = "jail.command.unjailforce",
		usage = "/unjailforce [player]"
	)
public class UnJailForceCommand implements Command {
	
	public boolean execute(JailManager jm, CommandSender sender, String... args) {
		//Check if the player is jailed
		if(jm.isPlayerJailed(args[0])) {
			jm.getPlugin().getPrisonerManager().forceRelease(jm.getPrisoner(args[0]));
			sender.sendMessage(jm.getPlugin().getJailIO().getLanguageString(LangString.FORCEUNJAILED, args[0]));
			
			if(jm.getPlugin().getConfig().getBoolean(Settings.LOGJAILING.getPath())) {
				jm.getPlugin().getLogger().info(jm.getPlugin().getJailIO().getLanguageString(LangString.BROADCASTUNJAILING, new String[] { args[0], sender.getName() }));
			}
		}else {
			//The player is not currently jailed
			sender.sendMessage(jm.getPlugin().getJailIO().getLanguageString(LangString.NOTJAILED, args[0]));
		}
		
		return true;
	}
}
