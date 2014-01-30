package com.graywolf336.jail.command.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.graywolf336.jail.JailManager;
import com.graywolf336.jail.beans.Jail;
import com.graywolf336.jail.command.Command;
import com.graywolf336.jail.command.CommandInfo;
import com.graywolf336.jail.enums.LangString;

@CommandInfo(
		maxArgs = 2,
		minimumArgs = 1,
		needsPlayer = false,
		pattern = "teleout|teleportout",
		permission = "jail.command.jailteleout",
		usage = "/jail teleout <jailname> (player)"
	)
public class JailTeleOutCommand implements Command {
	public boolean execute(JailManager jm, CommandSender sender, String... args) throws Exception {
		Jail j = jm.getJail(args[1]);
		
		//The jail doesn't exist
		if(j == null) {
			sender.sendMessage(jm.getPlugin().getJailIO().getLanguageString(LangString.NOJAIL, args[1]));
		}else {
			//The jail does exist
			//now let's check the size of the command
			//if it has two args then someone is sending someone else in
			//otherwise it is just the sender going in
			if(args.length == 3) {
				Player p = jm.getPlugin().getServer().getPlayer(args[2]);
				
				//If the player they're trying to send is offline, don't do anything
				if(p == null) {
					sender.sendMessage(jm.getPlugin().getJailIO().getLanguageString(LangString.PLAYERNOTONLINE, args[2]));
				}else {
					p.teleport(j.getTeleportFree());
					sender.sendMessage(jm.getPlugin().getJailIO().getLanguageString(LangString.TELEOUT, new String[] { args[2], args[1] }));
				}
			}else {
				if(sender instanceof Player) {
					((Player) sender).teleport(j.getTeleportFree());
					sender.sendMessage(jm.getPlugin().getJailIO().getLanguageString(LangString.TELEOUT, new String[] { sender.getName(), args[1] }));
				}else {
					sender.sendMessage(jm.getPlugin().getJailIO().getLanguageString(LangString.PLAYERCONTEXTREQUIRED));
				}
			}
		}
		
		return true;
	}
}
