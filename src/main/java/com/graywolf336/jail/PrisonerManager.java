package com.graywolf336.jail;

import org.bukkit.entity.Player;

import com.graywolf336.jail.beans.Cell;
import com.graywolf336.jail.beans.Jail;
import com.graywolf336.jail.beans.Prisoner;
import com.graywolf336.jail.enums.LangString;
import com.graywolf336.jail.enums.Settings;

public class PrisonerManager {
	private JailMain pl;
	
	public PrisonerManager(JailMain plugin) {
		this.pl = plugin;
	}
	
	/**
	 * Prepare the jailing of this player.
	 * 
	 * @param player The player we are preparing the jail for.
	 * @param prisoner The prisoner file.
	 */
	public void prepareJail(Jail jail, Cell cell, Player player, Prisoner prisoner) throws Exception {
		//Do some checks of whether the passed params are null.
		if(jail == null)
			throw new Exception("The jail can not be null.");
		
		if(prisoner == null)
			throw new Exception("Prisoner data can not be null.");
		
		//Set whether the prisoner is offline or not.
		prisoner.setOfflinePending(player == null);
		
		//Now that we've got those checks out of the way, let's start preparing.
		if(cell == null) {
			jail.addPrisoner(prisoner);
		}else {
			jail.getCell(cell.getName()).setPrisoner(prisoner);
		}
		
		//Save the jail after adding them to the jail
		pl.getJailIO().saveJail(jail);
		
		//If they are offline, handle different..?
		if(prisoner.isOfflinePending()) {
			
		}else {
			
		}
		
		//Get a message ready for broadcasting or logging.
		String msg = "";
		
		if(prisoner.getRemainingTime() < 0)
			msg = pl.getJailIO().getLanguageString(LangString.BROADCASTMESSAGEFOREVER, new String[] { prisoner.getName() });
		else//
			msg = pl.getJailIO().getLanguageString(LangString.BROADCASTMESSAGEFOREVER, new String[] { prisoner.getName(), String.valueOf(prisoner.getRemainingTimeInMinutes()) });
		
		//Broadcast the message, if it is enabled
		if(pl.getConfig().getBoolean(Settings.BROADCASTJAILING.getPath(), false)) {
			pl.getServer().broadcastMessage(msg);
		}
		
		//Log the message, if it is enabled
		if(pl.getConfig().getBoolean(Settings.LOGJAILING.getPath(), true)) {
			pl.getLogger().info(msg);
		}
	}
}
