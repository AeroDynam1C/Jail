package com.graywolf336.jail;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.graywolf336.jail.beans.Jail;
import com.graywolf336.jail.command.CommandHandler;
import com.graywolf336.jail.listeners.BlockListener;
import com.graywolf336.jail.listeners.EntityListener;
import com.graywolf336.jail.listeners.PlayerListener;
import com.graywolf336.jail.listeners.PlayerPreventionsListener;

public class JailMain extends JavaPlugin {
	private CommandHandler cmdHand;
	private JailIO io;
	private JailManager jm;
	private PrisonerManager pm;
	
	public void onEnable() {
		loadConfig();
		
		jm = new JailManager(this);
		io = new JailIO(this);
		io.loadLanguage();
		io.prepareStorage();
		io.loadJails();
		
		cmdHand = new CommandHandler(this);
		pm = new PrisonerManager(this);
		
		PluginManager plm = this.getServer().getPluginManager();
		plm.registerEvents(new BlockListener(), this);
		plm.registerEvents(new EntityListener(), this);
		plm.registerEvents(new PlayerListener(this), this);
		plm.registerEvents(new PlayerPreventionsListener(this), this);
		
		//For the time, we will use:
		//http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/TimeUnit.html#convert(long, java.util.concurrent.TimeUnit)
	}

	public void onDisable() {
		if(jm != null)
			for(Jail j : jm.getJails())
				io.saveJail(j);
		
		cmdHand = null;
		pm = null;
		jm = null;
		io = null;
	}
	
	private void loadConfig() {
		//Only create the default config if it doesn't exist
		saveDefaultConfig();
		
		//Append new key-value pairs to the config
		getConfig().options().copyDefaults(true);
		
		// Set the header and save
        getConfig().options().header(getHeader());
        saveConfig();
	}
	
	private String getHeader() {
		String sep = System.getProperty("line.separator");
		
		return "###################" + sep
				+ "Jail v" + this.getDescription().getVersion() + " config file" + sep
				+ "Note: You -must- use spaces instead of tabs!" + sep +
				"###################";
	}
	
	/* Majority of the new command system was heavily influenced by the MobArena.
	 * Thank you garbagemule for the great system you have in place there.
	 *
	 * Send the command off to the CommandHandler class, that way this main class doesn't get clogged up.
	 */
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		cmdHand.handleCommand(jm, sender, command.getName().toLowerCase(), args);
		return true;//Always return true here, that way we can handle the help and command usage ourself.
	}
	
	/** Gets the {@link JailIO} instance. */
	public JailIO getJailIO() {
		return this.io;
	}
	
	/** Gets the {@link JailManager} instance. */
	public JailManager getJailManager() {
		return this.jm;
	}
	
	/** Gets the {@link PrisonerManager} instance. */
	public PrisonerManager getPrisonerManager() {
		return this.pm;
	}
}
