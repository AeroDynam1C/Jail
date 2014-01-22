package com.graywolf336.jail.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.graywolf336.jail.JailManager;
import com.graywolf336.jail.beans.Cell;
import com.graywolf336.jail.beans.Jail;
import com.graywolf336.jail.command.Command;
import com.graywolf336.jail.command.CommandInfo;
import com.graywolf336.jail.enums.LangString;

@CommandInfo(
		maxArgs = 1,
		minimumArgs = 1,
		needsPlayer = false,
		pattern = "jaillistcells|jcc",
		permission = "jail.command.jaillistcell",
		usage = "/jaillistcells <jail>"
	)
public class JailListCellsCommand implements Command {
	@Override
	public boolean execute(JailManager jm, CommandSender sender, String... args) {
		sender.sendMessage(ChatColor.AQUA + "----------Cells----------");
		
		if(!jm.getJails().isEmpty()) {
			if(jm.getJail(args[0]) != null) {
				Jail j = jm.getJail(args[0]);
				
				String message = "";
				for(Cell c : j.getCells()) {
					if(message.isEmpty()) {
						message = c.getName() + (c.getPrisoner() == null ? "" : "(" + c.getPrisoner().getName() + ")");
					}else {
						message += ", " + c.getName() + (c.getPrisoner() == null ? "" : "(" + c.getPrisoner().getName() + ")");
					}
				}
				
				if(message.isEmpty()) {
					sender.sendMessage(jm.getPlugin().getJailIO().getLanguageString(LangString.NOCELLS, j.getName()));
				}else {
					sender.sendMessage(ChatColor.GREEN + message);
				}
			}else {
				sender.sendMessage(jm.getPlugin().getJailIO().getLanguageString(LangString.NOJAIL, args[0]));
			}
		}else {
			sender.sendMessage(jm.getPlugin().getJailIO().getLanguageString(LangString.NOJAILS));
		}
		
		sender.sendMessage(ChatColor.AQUA + "-------------------------");
		return true;
	}
}
