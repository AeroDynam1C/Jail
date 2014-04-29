package com.graywolf336.jail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.graywolf336.jail.beans.Cell;
import com.graywolf336.jail.beans.ConfirmPlayer;
import com.graywolf336.jail.beans.CreationPlayer;
import com.graywolf336.jail.beans.Jail;
import com.graywolf336.jail.beans.Prisoner;
import com.graywolf336.jail.enums.Confirmation;
import com.graywolf336.jail.enums.LangString;
import com.graywolf336.jail.steps.CellCreationSteps;
import com.graywolf336.jail.steps.JailCreationSteps;

/**
 * Handles all things related to jails.
 * 
 * <p>
 * 
 * Stores the following:
 * <ul>
 * 	<li>The {@link Jail jails}, which contains the prisoners and cells.</li>
 * 	<li>Players creating jails, see {@link CreationPlayer}.</li>
 * 	<li>Players creating jail cells, see {@link CreationPlayer}.</li>
 * 	<li>An instance of {@link JailCreationSteps} for stepping players through the Jail creation process.</li>
 * </ul>
 * 
 * @author graywolf336
 * @since 3.0.0
 * @version 1.1.0
 */
public class JailManager {
	private JailMain plugin;
	private HashMap<String, Jail> jails;
	private HashMap<String, CreationPlayer> jailCreators;
	private HashMap<String, CreationPlayer> cellCreators;
	private HashMap<String, ConfirmPlayer> confirms;
	private JailCreationSteps jcs;
	private CellCreationSteps ccs;
	
	public JailManager(JailMain plugin) {
		this.plugin = plugin;
		this.jails = new HashMap<String, Jail>();
		this.jailCreators = new HashMap<String, CreationPlayer>();
		this.cellCreators = new HashMap<String, CreationPlayer>();
		this.confirms = new HashMap<String, ConfirmPlayer>();
		this.jcs = new JailCreationSteps();
		this.ccs = new CellCreationSteps();
	}
	
	/** Returns the instance of the plugin main class. */
	public JailMain getPlugin() {
		return this.plugin;
	}
	
	/** Returns a HashSet of all the jails. */
	public HashSet<Jail> getJails() {
		return new HashSet<Jail>(jails.values());
	}
	
	/** Returns an array of all the names of the jails. */
	public String[] getJailNames() {
		return this.jails.keySet().toArray(new String[jails.size()]);
	}
	
	/**
	 * Adds a jail to the collection of them.
	 * 
	 * @param jail The jail to add
	 * @param n True if this is a new jail, false if it isn't.
	 */
	public void addJail(Jail jail, boolean n) {
		this.jails.put(jail.getName(), jail);
		if(n) plugin.getJailIO().saveJail(jail);
	}
	
	/**
	 * Removes a {@link Jail}.
	 * 
	 * @param name of the jail to remove
	 */
	public void removeJail(String name) {
		plugin.getJailIO().removeJail(this.jails.get(name));
		this.jails.remove(name);
	}
	
	/**
	 * Gets a jail by the given name.
	 * 
	 * @param name The name of the jail to get.
	 * @return The {@link Jail} with the given name, if no jail found this <strong>will</strong> return null.
	 */
	public Jail getJail(String name) {
		return this.jails.get(name);
	}
	
	/**
	 * Gets the nearest {@link Jail} to the player, if the sender is a player or else it will get the first jail defined.
	 * 
	 * @param sender The sender who we are looking around.
	 * @return The nearest {@link Jail} to the sender if it is a player or else the first jail defined.
	 */
	public Jail getNearestJail(CommandSender sender) {
		if(sender instanceof Player) {
			Location loc = ((Player) sender).getLocation();
			
			Jail j = null;
			double len = -1;
			
			for(Jail jail : jails.values()) {
				double clen = jail.getDistance(loc);
				
				if (clen < len || len == -1) {
					len = clen;
					j = jail;
				}
			}
			
			return (j == null ? jails.values().iterator().next() : j);
		}else {
			return jails.values().iterator().next();
		}
	}
	
	/**
	 * Gets the jail which this location is in, will return null if none exist.
	 * 
	 * @param loc to get the jail from
	 * @return The jail this block is in, null if no jail found.
	 */
	public Jail getJailFromLocation(Location loc) {
		for(Jail j : jails.values()) {
			if(Util.isInsideAB(loc.toVector(), j.getMinPoint().toVector(), j.getMaxPoint().toVector())) {
				return j;
			}
		}
		
		return null;
	}
	
	/**
	 * Checks to see if the given name for a {@link Jail} is valid, returns true if it is a valid jail.
	 * 
	 * @param name The name of the jail to check.
	 * @return True if a valid jail was found, false if no jail was found.
	 */
	public boolean isValidJail(String name) {
		return this.jails.get(name) != null;
	}
	
	/**
	 * Gets all the prisoners in the system, best for a system wide count of the prisoners or accessing all the prisoners at once.
	 * 
	 * @return HashSet of Prisoners.
	 */
	public HashSet<Prisoner> getAllPrisoners() {
		HashSet<Prisoner> prisoners = new HashSet<Prisoner>();
		
		for(Jail j : jails.values()) {
			prisoners.addAll(j.getAllPrisoners());
		}
		
		return prisoners;
	}
	
	/**
	 * Gets the {@link Jail jail} the given prisoner is in.
	 * 
	 * @param prisoner The prisoner data for the prisoner we are checking
	 * @return The jail the player is in, <strong>CAN BE NULL</strong>.
	 */
	public Jail getJailPrisonerIsIn(Prisoner prisoner) {
		if(prisoner == null) return null;
		
		return getJailPlayerIsIn(prisoner.getName());
	}
	
	/**
	 * Gets the {@link Jail jail} the given player is in.
	 * 
	 * @param name The name of the player whos jail we are getting.
	 * @return The jail the player is in, <strong>CAN BE NULL</strong>.
	 */
	public Jail getJailPlayerIsIn(String name) {
		Jail re = null;
		
		for(Jail j : jails.values()) {
			if(j.isPlayerAPrisoner(name)) {
				re = j;
				break;
			}
		}
		
		return re;
	}
	
	/**
	 * Gets if the given player is jailed or not, in all the jails and cells.
	 * 
	 * @param name The name of the player to check.
	 * @return true if they are jailed, false if not.
	 */
	public boolean isPlayerJailed(String name) {
		boolean r = false;
		
		for(Jail j : jails.values()) {
			if(j.isPlayerAPrisoner(name)) {
				r = true;
				break;
			}
		}
		
		return r;
	}
	
	/** 
	 * Gets the {@link Prisoner} data from for this user, if they are jailed.
	 * 
	 * @param name The name of prisoner who's data to get
	 * @return {@link Prisoner prisoner} data.
	 */
	public Prisoner getPrisoner(String name) {
		Jail j = getJailPlayerIsIn(name);
		
		if(j != null) {
			return j.getPrisoner(name);
		}else {
			return null;
		}
	}
	
	/**
	 * Clears a {@link Jail} of all its prisoners if the jail is provided, otherwise it releases all the prisoners in all the jails.
	 * 
	 * @param jail The name of the jail to release the prisoners in, null if wanting to clear all.
	 * @return The resulting message to be sent to the caller of this method.
	 */
	public String clearJailOfPrisoners(String jail) {
		//If they don't pass in a jail name, clear all the jails
		if(jail != null) {
			Jail j = getJail(jail);
			
			if(j != null) {
				for(Prisoner p : j.getAllPrisoners()) {
					getPlugin().getPrisonerManager().releasePrisoner(getPlugin().getServer().getPlayerExact(p.getName()), p);
				}
				
				return getPlugin().getJailIO().getLanguageString(LangString.PRISONERSCLEARED, j.getName());
			}else {
				return getPlugin().getJailIO().getLanguageString(LangString.NOJAIL, jail);
			}
		}else {
			return clearAllJailsOfAllPrisoners();
		}
	}
	
	/**
	 * Clears all the {@link Jail jails} of prisoners by releasing them.
	 * 
	 * @return The resulting message to be sent to the caller of this method.
	 */
	public String clearAllJailsOfAllPrisoners() {
		//No name of a jail has been passed, so release all of the prisoners in all the jails
		if(getJails().size() == 0) {
			return getPlugin().getJailIO().getLanguageString(LangString.NOJAILS);
		}else {
			for(Jail j : getJails()) {
				for(Prisoner p : j.getAllPrisoners()) {
					getPlugin().getPrisonerManager().releasePrisoner(getPlugin().getServer().getPlayerExact(p.getName()), p);
				}
			}
			
			return getPlugin().getJailIO().getLanguageString(LangString.PRISONERSCLEARED, getPlugin().getJailIO().getLanguageString(LangString.ALLJAILS));
		}
	}
	
	/**
	 * Forcefully clears all the jails if name provided is null.
	 * 
	 * <p />
	 * 
	 * This method just clears them from the storage, doesn't release them.
	 * 
	 * @param name of the jail to clear, null if all of them.
	 * @return The resulting message to be sent to the caller of this method.
	 */
	public String forcefullyClearJailOrJails(String name) {
		if(name == null) {
			if(getJails().size() == 0) {
				return getPlugin().getJailIO().getLanguageString(LangString.NOJAILS);
			}else {
				for(Jail j : getJails()) {
					j.clearPrisoners();
				}
				
				return getPlugin().getJailIO().getLanguageString(LangString.PRISONERSCLEARED, getPlugin().getJailIO().getLanguageString(LangString.ALLJAILS));
			}
		}else {
			Jail j = getJail(name);
			
			if(j != null) {
				j.clearPrisoners();
				return getPlugin().getJailIO().getLanguageString(LangString.PRISONERSCLEARED, j.getName());
			}else {
				return getPlugin().getJailIO().getLanguageString(LangString.NOJAIL, name);
			}
		}
	}
	
	/**
	 * Deletes a jail's cell, checking everything is setup right for it to be deleted.
	 * 
	 * @param jail Name of the jail to delete a cell in.
	 * @param cell Name of the cell to delete.
	 * @return The resulting message to be sent to the caller of this method.
	 */
	public String deleteJailCell(String jail, String cell) {
		//Check if the jail name provided is a valid jail
		if(isValidJail(jail)) {
			Jail j = getJail(jail);
			
			//check if the cell is a valid cell
			if(j.isValidCell(cell)) {
				if(j.getCell(cell).hasPrisoner()) {
					//The cell has a prisoner, so tell them to first transfer the prisoner
					//or release the prisoner
					return getPlugin().getJailIO().getLanguageString(LangString.CELLREMOVALUNSUCCESSFUL, new String[] { cell, jail });
				}else {
					j.removeCell(cell);
					return getPlugin().getJailIO().getLanguageString(LangString.CELLREMOVED, new String[] { cell, jail });
				}
			}else {
				//No cell found by the provided name in the stated jail
				return getPlugin().getJailIO().getLanguageString(LangString.NOCELL, new String[] { cell, jail });
			}
		}else {
			//No jail found by the provided name
			return getPlugin().getJailIO().getLanguageString(LangString.NOJAIL, jail);
		}
	}
	
	/**
	 * Deletes all the cells in a jail, returns a list of Strings.
	 * 
	 * @param jail The name of the jail to delete all the jails in.
	 * @return An array of strings of messages to send.
	 */
	public String[] deleteAllJailCells(String jail) {
		LinkedList<String> msgs = new LinkedList<String>();
		
		//Check if the jail name provided is a valid jail
		if(isValidJail(jail)) {
			Jail j = getJail(jail);
			
			if(j.getCellCount() == 0) {
				//There are no cells in this jail, thus we can't delete them.
				msgs.add(getPlugin().getJailIO().getLanguageString(LangString.NOCELLS, j.getName()));
			}else {
				//Keep a local copy of the hashset so that we don't get any CMEs.
				HashSet<Cell> cells = new HashSet<Cell>(j.getCells());
				
				for(Cell c : cells) {
					if(c.hasPrisoner()) {
						//The cell has a prisoner, so tell them to first transfer the prisoner
						//or release the prisoner
						msgs.add(getPlugin().getJailIO().getLanguageString(LangString.CELLREMOVALUNSUCCESSFUL, new String[] { c.getName(), j.getName() }));
					}else {
						j.removeCell(c.getName());
						msgs.add(getPlugin().getJailIO().getLanguageString(LangString.CELLREMOVED, new String[] { c.getName(), j.getName() }));
					}
				}
			}
		}else {
			//No jail found by the provided name
			msgs.add(getPlugin().getJailIO().getLanguageString(LangString.NOJAIL, jail));
		}
		
		return msgs.toArray(new String[msgs.size()]);
	}
	
	/**
	 * Deletes a jail while doing some checks to verify it can be deleted.
	 * 
	 * @param jail The name of the jail to delete.
	 * @return The resulting message to be sent to the caller of this method.
	 */
	public String deleteJail(String jail) {
		//Check if the jail name provided is a valid jail
		if(isValidJail(jail)) {
			//check if the jail doesn't contain prisoners
			if(getJail(jail).getAllPrisoners().size() == 0) {
				//There are no prisoners, so we can delete it
				removeJail(jail);
				return getPlugin().getJailIO().getLanguageString(LangString.JAILREMOVED, jail);
			}else {
				//The jail has prisoners, they need to release them first
				return getPlugin().getJailIO().getLanguageString(LangString.JAILREMOVALUNSUCCESSFUL, jail);
			}
		}else {
			//No jail found by the provided name
			return getPlugin().getJailIO().getLanguageString(LangString.NOJAIL, jail);
		}
	}
	
	/**
	 * Returns whether or not the player is creating a jail or a cell.
	 * 
	 * <p>
	 * 
	 * If you want to check to see if they're just creating a jail then use {@link #isCreatingAJail(String) isCreatingAJail} or if you want to see if they're creating a cell then use {@link #isCreatingACell(String) isCreatingACell}. 
	 * 
	 * @param name The name of the player, in any case as we convert it to lowercase.
	 * @return True if the player is creating a jail or cell, false if they're not creating anything.
	 */
	public boolean isCreatingSomething(String name) {
		return this.jailCreators.containsKey(name.toLowerCase()) || this.cellCreators.containsKey(name.toLowerCase());
	}
	
	/** Returns a message used for telling them what they're creating and what step they're on. */
	public String getStepMessage(String player) {
		String message = "";
		
		if(isCreatingACell(player)) {//Check whether it is a jail cell
			CreationPlayer cp = this.getCellCreationPlayer(player);
			message = "You're already creating a Cell with the name '" + cp.getCellName() + "' and you still need to ";
			
			switch(cp.getStep()) {
				case 1:
					message += "set the teleport in location.";
					break;
				case 2:
					message += "select all the signs.";
					break;
				case 3:
					message += "set the double chest location.";
					break;
			}
			
		}else if(isCreatingAJail(player)) {//If not a cell, then check if a jail.
			CreationPlayer cp = this.getJailCreationPlayer(player);
			message = "You're already creating a Jail with the name '" + cp.getJailName() + "' and you still need to ";
			
			switch(cp.getStep()) {
				case 1:
					message += "select the first point.";
					break;
				case 2:
					message += "select the second point.";
					break;
				case 3:
					message += "set the teleport in location.";
					break;
				case 4:
					message += "set the release location.";
					break;
			}
		}
		
		return message;
	}
	
	/** Returns whether or not someone is creating a <strong>Jail</strong>. */
	public boolean isCreatingAJail(String name) {
		return this.jailCreators.containsKey(name.toLowerCase());
	}
	
	/**
	 * Method for setting a player to be creating a Jail, returns whether or not they were added successfully.
	 * 
	 * @param player The player who is creating a jail.
	 * @param jailName The name of the jail we are creating.
	 * @return True if they were added successfully, false if they are already creating a Jail.
	 */
	public boolean addCreatingJail(String player, String jailName) {
		if(isCreatingAJail(player)) {
			return false;
		}else {
			this.jailCreators.put(player.toLowerCase(), new CreationPlayer(jailName));
			return true;
		}
	}
	
	/** Returns the instance of the CreationPlayer for this player, null if there was none found. */
	public CreationPlayer getJailCreationPlayer(String name) {
		return this.jailCreators.get(name.toLowerCase());
	}
	
	/** Removes a CreationPlayer with the given name from the jail creators. */
	public void removeJailCreationPlayer(String name) {
		this.jailCreators.remove(name.toLowerCase());
	}
	
	/** Returns whether or not someone is creating a <strong>Cell</strong>. */
	public boolean isCreatingACell(String name) {
		return this.cellCreators.containsKey(name.toLowerCase());
	}
	
	/**
	 * Method for setting a player to be creating a Cell, returns whether or not they were added successfully.
	 * 
	 * @param player The player who is creating a jail.
	 * @param jailName The name of the jail this cell is going.
	 * @param cellName The name of the cell we are creating.
	 * @return True if they were added successfully, false if they are already creating a Jail.
	 */
	public boolean addCreatingCell(String player, String jailName, String cellName) {
		if(isCreatingACell(player)) {
			return false;
		}else {
			this.cellCreators.put(player.toLowerCase(), new CreationPlayer(jailName, cellName));
			return true;
		}
	}
	
	/** Returns the instance of the CreationPlayer for this player, null if there was none found. */
	public CreationPlayer getCellCreationPlayer(String name) {
		return this.cellCreators.get(name.toLowerCase());
	}
	
	/** Removes a CreationPlayer with the given name from the cell creators. */
	public void removeCellCreationPlayer(String name) {
		this.cellCreators.remove(name.toLowerCase());
	}
	
	/** Gets the instance of the {@link JailCreationSteps}. */
	public JailCreationSteps getJailCreationSteps() {
		return this.jcs;
	}
	
	/** Gets the instance of the {@link CellCreationSteps}. */
	public CellCreationSteps getCellCreationSteps() {
		return this.ccs;
	}
	
	/** Adds something to the confirming list. */
	public void addConfirming(String name, ConfirmPlayer confirmer) {
		getPlugin().debug("Adding a confirming for " + name + " to confirm " + confirmer.getConfirming().toString().toLowerCase());
		this.confirms.put(name, confirmer);
	}
	
	/** Removes a name from the confirming list. */
	public void removeConfirming(String name) {
		this.confirms.remove(name);
	}
	
	/** Checks if the given name is confirming something. */
	public boolean isConfirming(String name) {
		if(this.confirms.containsKey(name)) {
			if(this.confirmingHasExpired(name)) {
				getPlugin().debug("Removing " + name + "'s confirmation as it expired.");
				this.removeConfirming(name);
			}
			
			return this.confirms.containsKey(name);
		}else {
			return false;
		}
	}
	
	/** Returns true if the confirmation has expired, false if it is still valid. */
	public boolean confirmingHasExpired(String name) {
		return this.confirms.get(name).getExpiryTime() >= (System.currentTimeMillis() + 5000L);
	}
	
	/** Returns the original arguments for what we are confirming. */
	public String[] getOriginalArgs(String name) {
		return this.confirms.get(name).getArguments();
	}
	
	/** Returns what the given name is confirming. */
	public Confirmation getWhatIsConfirming(String name) {
		return this.confirms.get(name).getConfirming();
	}
}
