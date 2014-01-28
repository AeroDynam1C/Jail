package com.graywolf336.jail.command;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import com.graywolf336.jail.JailMain;
import com.graywolf336.jail.JailManager;
import com.graywolf336.jail.command.jcommands.JailFoundation;
import com.graywolf336.jail.command.jcommands.ListJails;
import com.graywolf336.jail.command.subcommands.JailCommand;
import com.graywolf336.jail.command.subcommands.JailListCommand;
import com.graywolf336.jail.enums.LangString;

public class JailHandler {
	private LinkedHashMap<String, Command> commands;
	
	public JailHandler(JailMain plugin) {
		commands = new LinkedHashMap<String, Command>();
		loadCommands();
		
		plugin.getLogger().info("Loaded " + commands.size() + " sub-commands of /jail.");
	}

	public void handleCommand(JailManager jm, CommandSender sender, String... args) {
		if(args.length == 0) {
			parseCommand(jm, sender, getMatches("jail").get(0), args);
		}else {
			JailFoundation foundation = new JailFoundation();
			JCommander jc = new JCommander(foundation);
			
			//Now let's add the subcommands
			jc.addCommand("list", new ListJails());
			
			try {
				jc.parse(args);
				
				List<Command> matches = getMatches(jc.getParsedCommand());
				
				if(matches.size() == 0) {
					//There should only be one for /jail
					parseCommand(jm, sender, getMatches("jail").get(0), args);
				} else if(matches.size() > 1) {
					for(Command c : matches)
						showUsage(sender, c);
				}else {
					parseCommand(jm, sender, matches.get(0), args);
				}
			}catch(ParameterException e) {
				parseCommand(jm, sender, getMatches("jail").get(0), args);
			}
		}
	}
	
	/**
	 * Handles the given command and checks that the command is in valid form.
	 * 
	 * <p>
	 * 
	 * It checks in the following order:
	 * <ol>
	 * 	<li>If they have permission for it, if they don't then we send them a message stating so.</li>
	 * 	<li>If the command needs a player instance, if so we send a message stating that.</li>
	 * 	<li>If the required minimum arguments have been passed, if not sends the usage.</li>
	 * 	<li>If the required maximum arguments have been passed (if there is a max, -1 if no max), if not sends the usage.</li>
	 * 	<li>Then executes, upon failed execution it sends the usage command.</li>
	 * </ol>
	 * 
	 * @param jailmanager The instance of {@link JailManager}.
	 * @param sender The sender of the command.
	 * @param command The name of the command.
	 * @param args The arguments passed to the command.
	 */
	public boolean parseCommand(JailManager jailmanager, CommandSender sender, Command c, String[] args) {
		CommandInfo i = c.getClass().getAnnotation(CommandInfo.class);
		
		// First, let's check if the sender has permission for the command.
		if(!sender.hasPermission(i.permission())) {
			sender.sendMessage(jailmanager.getPlugin().getJailIO().getLanguageString(LangString.NOPERMISSION));
			return false;
		}
		
		// Next, let's check if we need a player and then if the sender is actually a player
		if(i.needsPlayer() && !(sender instanceof Player)) {
			sender.sendMessage(jailmanager.getPlugin().getJailIO().getLanguageString(LangString.PLAYERCONTEXTREQUIRED));
			return false;
		}
		
		// Now, let's check the size of the arguments passed. If it is shorter than the minimum required args, let's show the usage.
		if(args.length < i.minimumArgs()) {
			showUsage(sender, c);
			return false;
		}
		
		// Then, if the maximumArgs doesn't equal -1, we need to check if the size of the arguments passed is greater than the maximum args.
		if(i.maxArgs() != -1 && i.maxArgs() < args.length) {
			showUsage(sender, c);
			return false;
		}
		
		// Since everything has been checked and we're all clear, let's execute it.
		// But if get back false, let's show the usage message.
		try {
			if(!c.execute(jailmanager, sender, args)) {
				showUsage(sender, c);
				return false;
			}else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			jailmanager.getPlugin().getLogger().severe("An error occured while handling the command: " + i.usage());
			showUsage(sender, c);
			return false;
		}
	}
	
	private List<Command> getMatches(String command) {
		List<Command> result = new ArrayList<Command>();
		
		for(Entry<String, Command> entry : commands.entrySet()) {
			if(command.matches(entry.getKey())) {
				result.add(entry.getValue());
			}
		}
		
		return result;
	}
	
	/**
	 * Shows the usage information to the sender, if they have permission.
	 * 
	 * @param sender The sender of the command
	 * @param command The command to send usage of.
	 */
	private void showUsage(CommandSender sender, Command command) {
		CommandInfo info = command.getClass().getAnnotation(CommandInfo.class);
		if(!sender.hasPermission(info.permission())) return;
		
		sender.sendMessage(info.usage());
	}
	
	private void loadCommands() {
		load(JailCommand.class);
		load(JailListCommand.class);
	}
	
	private void load(Class<? extends Command> c) {
		CommandInfo info = c.getAnnotation(CommandInfo.class);
		if(info == null) return;
		
		try {
			commands.put(info.pattern(), c.newInstance());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
