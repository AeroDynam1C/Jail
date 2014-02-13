package com.graywolf336.jail;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.graywolf336.jail.beans.Cell;
import com.graywolf336.jail.beans.Jail;
import com.graywolf336.jail.beans.Prisoner;
import com.graywolf336.jail.beans.SimpleLocation;
import com.graywolf336.jail.enums.LangString;

/**
 * Handles all the saving and loading of the plugin's data.
 * 
 * @author graywolf336
 * @since 2.x.x
 * @version 3.0.0
 * 
 */
public class JailIO {
	private JailMain pl;
	private FileConfiguration flat, lang;
	private int storage; //0 = flatfile, 1 = sqlite, 2 = mysql
	
	public JailIO(JailMain plugin) {
		this.pl = plugin;
		
		String st = pl.getConfig().getString("storage.type", "flatfile");
		if(st.equalsIgnoreCase("sqlite")) {
			storage = 1;
		}else if(st.equalsIgnoreCase("mysql")) {
			storage = 2;
		}else {
			storage = 0;
		}
	}
	
	/** Loads the language file from disk, if there is none then we save the default one. */
	public void loadLanguage() {
		String language = pl.getConfig().getString("system.language");
		boolean save = false;
		File langFile = new File(pl.getDataFolder(), language + ".yml");
		
		//File or folder already exists, let's check
		if(langFile.exists()) {
			if(langFile.isFile()) {
				lang = YamlConfiguration.loadConfiguration(langFile);
				pl.getLogger().info("Loaded the language: " + language);
			}else {
				pl.getLogger().severe("The language file can not be a folder.");
				pl.getLogger().severe("As a result, we are reverting back to English as the language.");
				lang = YamlConfiguration.loadConfiguration(pl.getResource("en.yml"));
				save = true;
			}
		}else {
			pl.getLogger().warning("Loading the default language of: en");
			pl.getLogger().warning("If you wish to change this, please rename 'en.yml' to whatever you wish and set the config value to the name of the file.");
			lang = YamlConfiguration.loadConfiguration(pl.getResource("en.yml"));
			save = true;
		}
		
		//If we have flagged to save the language file, let's save it as en.yml as this flag usually means they didn't have it loaded.
		if(save) {
			try {
				lang.save(new File(pl.getDataFolder(), "en.yml"));
			} catch (IOException e) {
				pl.getLogger().severe("Unable to save the language file: " + e.getMessage());
			}
		}
	}
	
	/** Returns the message in the language, no variables are replaced.*/
	public String getLanguageString(LangString langString) {
		return getLanguageString(langString, new String[] {});
	}
	
	/** Returns the message in the language, no variables are replaced.*/
	public String getLanguageString(LangString langString, LangString langString2) {
		return getLanguageString(langString, getLanguageString(langString2, new String[] {}));
	}
	
	/**
	 * Returns the message in the language, with the provided variables being replaced.
	 * 
	 * @param langString Which {@link LangString} we should be getting to send.
	 * @param variables All the variables to replace, in order from 0 to however many.
	 * @return The message as a colorful message or an empty message if that isn't defined in the language file.
	 */
	public String getLanguageString(LangString langString, String... variables) {
		String message = lang.getString("language." + langString.getSection() + "." + langString.getName());
		
		if(message == null) return "";
		
		for (int i = 0; i < variables.length; i++) {
			message = message.replaceAll("%" + i + "%", variables[i]);
		}
		
		return Util.getColorfulMessage(message);
	}
	
	/**
	 * Prepares the storage engine to be used.
	 */
	public void prepareStorage() {
		switch(storage) {
			case 1:
				//prepare sqlite, I need to research this
				break;
			case 2:
				//prepare mysql, research this as well
				break;
			default:
				flat = YamlConfiguration.loadConfiguration(new File(pl.getDataFolder(), "data.yml"));
				break;
		}
	}
	
	/**
	 * Loads the jails, this should <strong>only</strong> be called after {@link #prepareStorage()}.
	 */
	public void loadJails() {
		switch(storage) {
			case 1:
				//load the jails from sqlite
				break;
			case 2:
				//load the jails from mysql
				break;
			default:
				//load the jails from flatfile
				if(flat.isConfigurationSection("jails")) {
					Set<String> jails = flat.getConfigurationSection("jails").getKeys(false);
					if(!jails.isEmpty()) {
						for(String name : jails) {
							loadJail(name);
						}
					}
				}
				break;
		}
		
		int s = pl.getJailManager().getJails().size();
		pl.getLogger().info("Loaded " + s + (s == 1 ? " jail." : " jails."));
	}
	
	/**
	 * Saves the provided {@link Jail jail} to the storage system we are using.
	 * 
	 * @param j The jail to save.
	 */
	public void saveJail(Jail j) {
		switch(storage) {
			case 1:
			case 2:
				break;
			default:
				if(flat != null) {
					String node = "jails." + j.getName() + ".";
					
					//Corners
					flat.set(node + "world", j.getWorldName());
					flat.set(node + "top.x", j.getMaxPoint().getBlockX());
					flat.set(node + "top.y", j.getMaxPoint().getBlockY());
					flat.set(node + "top.z", j.getMaxPoint().getBlockZ());
					flat.set(node + "bottom.x", j.getMinPoint().getBlockX());
					flat.set(node + "bottom.y", j.getMinPoint().getBlockY());
					flat.set(node + "bottom.z", j.getMinPoint().getBlockZ());
					
					//Tele in
					flat.set(node + "tps.in.x", j.getTeleportIn().getX());
					flat.set(node + "tps.in.y", j.getTeleportIn().getY());
					flat.set(node + "tps.in.z", j.getTeleportIn().getZ());
					flat.set(node + "tps.in.yaw", j.getTeleportIn().getYaw());
					flat.set(node + "tps.in.pitch", j.getTeleportIn().getPitch());
					
					//Tele out
					flat.set(node + "tps.free.world", j.getTeleportFree().getWorld().getName());
					flat.set(node + "tps.free.x", j.getTeleportFree().getX());
					flat.set(node + "tps.free.y", j.getTeleportFree().getY());
					flat.set(node + "tps.free.z", j.getTeleportFree().getZ());
					flat.set(node + "tps.free.yaw", j.getTeleportFree().getYaw());
					flat.set(node + "tps.free.pitch", j.getTeleportFree().getPitch());
					
					//Set all the cells to nothing, then we save each of them so no cells are left behind
					flat.set(node + ".cells", null);
					for(Cell c : j.getCells()) {
						String cNode = node + ".cells." + c.getName() + ".";
						
						if(c.getTeleport() != null) {
							flat.set(cNode + "tp.x", c.getTeleport().getX());
							flat.set(cNode + "tp.y", c.getTeleport().getY());
							flat.set(cNode + "tp.z", c.getTeleport().getZ());
							flat.set(cNode + "tp.yaw", c.getTeleport().getYaw());
							flat.set(cNode + "tp.pitch", c.getTeleport().getPitch());
						}
						
						if(c.getChestLocation() != null) {
							flat.set(cNode + "chest.x", c.getChestLocation().getBlockX());
							flat.set(cNode + "chest.y", c.getChestLocation().getBlockY());
							flat.set(cNode + "chest.z", c.getChestLocation().getBlockZ());
						}
						
						String[] signs = new String[c.getSigns().size()];
						int count = 0;
						for(SimpleLocation loc : c.getSigns()) {
							signs[count] = loc.toString();
							count++;
						}
						
						flat.set(cNode + "signs", signs);
						
						if(c.getPrisoner() != null) {
							Prisoner p = c.getPrisoner();
							flat.set(cNode + "prisoner.name", p.getName());
							flat.set(cNode + "prisoner.muted", p.isMuted());
							flat.set(cNode + "prisoner.time", p.getRemainingTime());
							flat.set(cNode + "prisoner.offlinePending", p.isOfflinePending());
							flat.set(cNode + "prisoner.toBeTransferred", p.isToBeTransferred());
							flat.set(cNode + "prisoner.jailer", p.getJailer());
							flat.set(cNode + "prisoner.reason", p.getReason());
							flat.set(cNode + "prisoner.inventory", p.getInventory());
							flat.set(cNode + "prisoner.armor", p.getArmor());
							if(p.getPreviousLocationString() != null)
								flat.set(cNode + "prisoner.previousLocation", p.getPreviousLocationString());
							if(p.getPreviousGameMode() != null)
								flat.set(cNode + "prisoner.previousGameMode", p.getPreviousGameMode().toString());
						}
					}
					
					//Null all the prisoners out before we save them again, this way no prisoners are left behind
					flat.set(node + "prisoners", null);
					for(Prisoner p : j.getPrisonersNotInCells()) {
						String pNode = node + "prisoners." + p.getName() + ".";
						flat.set(pNode + "muted", p.isMuted());
						flat.set(pNode + "time", p.getRemainingTime());
						flat.set(pNode + "offlinePending", p.isOfflinePending());
						flat.set(pNode + "toBeTransferred", p.isToBeTransferred());
						flat.set(pNode + "jailer", p.getJailer());
						flat.set(pNode + "reason", p.getReason());
						flat.set(pNode + "inventory", p.getInventory());
						flat.set(pNode + "armor", p.getArmor());
						if(p.getPreviousLocationString() != null)
							flat.set(pNode + "previousLocation", p.getPreviousLocationString());
						if(p.getPreviousGameMode() != null)
							flat.set(pNode + "previousGameMode", p.getPreviousGameMode().toString());
					}
					
					try {
						flat.save(new File(pl.getDataFolder(), "data.yml"));
					} catch (IOException e) {
						pl.getLogger().severe("Unable to save the Jail data: " + e.getMessage());
					}
				}else {
					pl.getLogger().severe("Storage not enabled, could not save the jail " + j.getName());
				}
				break;
		}
	}
	
	private void loadJail(String name) {
		switch(storage) {
			case 1:
			case 2:
				break;
			default:
				String node = "jails." + name + ".";
				String cNode = node + "cells.";
				Jail j = new Jail(pl, name);
				
				j.setWorld(flat.getString(node + "world"));
				j.setMaxPoint(new int[] {flat.getInt(node + "top.x"), flat.getInt(node + "top.y"), flat.getInt(node + "top.z")});
				j.setMinPoint(new int[] {flat.getInt(node + "bottom.x"), flat.getInt(node + "bottom.y"), flat.getInt(node + "bottom.z")});
				
				j.setTeleportIn(new SimpleLocation(
						flat.getString(node + "world"),
						flat.getDouble(node + "tps.in.x"),
						flat.getDouble(node + "tps.in.y"),
						flat.getDouble(node + "tps.in.z"),
						(float) flat.getDouble(node + "tps.in.yaw"),
						(float) flat.getDouble(node + "tps.in.pitch")));
				j.setTeleportFree(new SimpleLocation(
						flat.getString(node + "tps.free.world"),
						flat.getDouble(node + "tps.free.x"),
						flat.getDouble(node + "tps.free.y"),
						flat.getDouble(node + "tps.free.z"),
						(float) flat.getDouble(node + "tps.free.yaw"),
						(float) flat.getDouble(node + "tps.free.pitch")));
				
				if(flat.isConfigurationSection(node + "cells")) {
					Set<String> cells = flat.getConfigurationSection(node + "cells").getKeys(false);
					if(!cells.isEmpty()) {
						for(String cell : cells) {
							Cell c = new Cell(cell);
							String cellNode = cNode + cell + ".";
							
							c.setTeleport(new SimpleLocation(j.getTeleportIn().getWorld().getName(),
									flat.getDouble(cellNode + "tp.x"),
									flat.getDouble(cellNode + "tp.y"),
									flat.getDouble(cellNode + "tp.z"),
									(float) flat.getDouble(cellNode + "tp.yaw"),
									(float) flat.getDouble(cellNode + "tp.pitch")));
							c.setChestLocation(new Location(j.getTeleportIn().getWorld(),
									flat.getInt(cellNode + "chest.x"),
									flat.getInt(cellNode + "chest.y"),
									flat.getInt(cellNode + "chest.z")));
							
							for(String sign : flat.getStringList(cellNode + "signs")) {
								String[] arr = sign.split(",");
								c.addSign(new SimpleLocation(arr[0],
										Double.valueOf(arr[1]),
										Double.valueOf(arr[2]),
										Double.valueOf(arr[3]),
										Float.valueOf(arr[4]),
										Float.valueOf(arr[5])));
							}
							
							if(flat.contains(cellNode + "prisoner")) {
								Prisoner p = new Prisoner(flat.getString(cellNode + "prisoner.name"),
												flat.getBoolean(cellNode + "prisoner.muted"),
												flat.getLong(cellNode + "prisoner.time"),
												flat.getString(cellNode + "prisoner.jailer"),
												flat.getString(cellNode + "prisoner.reason"));
								p.setOfflinePending(flat.getBoolean(cellNode + "prisoner.offlinePending"));
								p.setToBeTransferred(flat.getBoolean(cellNode + "prisoner.toBeTransferred"));
								p.setPreviousPosition(flat.getString(cellNode + "prisoner.previousLocation"));
								p.setPreviousGameMode(flat.getString(cellNode + "prisoner.previousGameMode"));
								p.setInventory(flat.getString(cellNode + "prisoner.inventory", ""));
								p.setArmor(flat.getString(cellNode + "prisoner.armor", ""));
								c.setPrisoner(p);
							}
							
							j.addCell(c, false);
						}
					}
				}
				
				if(flat.isConfigurationSection(node + "prisoners")) {
					Set<String> prisoners = flat.getConfigurationSection(node + "prisoners").getKeys(false);
					if(!prisoners.isEmpty()) {
						for(String prisoner : prisoners) {
							String pNode = node + "prisoners." + prisoner + ".";
							Prisoner pris = new Prisoner(prisoner,
									flat.getBoolean(pNode + "muted"),
									flat.getLong(pNode + "time"),
									flat.getString(pNode + "jailer"),
									flat.getString(pNode + "reason"));
							pris.setOfflinePending(flat.getBoolean(pNode + "offlinePending"));
							pris.setToBeTransferred(flat.getBoolean(pNode + "toBeTransferred"));
							pris.setPreviousPosition(flat.getString(pNode + "previousLocation"));
							pris.setPreviousGameMode(flat.getString(pNode + "previousGameMode"));
							pris.setInventory(flat.getString(pNode + "inventory", ""));
							pris.setArmor(flat.getString(pNode + "armor", ""));
							j.addPrisoner(pris);
						}
					}
				}
				
				if(pl.getServer().getWorld(j.getWorldName()) != null) {
					pl.getJailManager().addJail(j, false);
					pl.getLogger().info("Loaded jail " + j.getName() + " with " + j.getAllPrisoners().size() + " prisoners and " + j.getCellCount() + " cells.");
				} else
					pl.getLogger().severe("Failed to load the jail " + j.getName() + " as the world '" + j.getWorldName() + "' does not exist (is null). Did you remove this world?");
				break;
		}
	}
	
	/**
	 * Removes the prisoner from the storage system.
	 * 
	 * @param j the jail which the prisoner is in.
	 * @param p the prisoner data
	 */
	public void removePrisoner(Jail j, Prisoner p) {
		this.removePrisoner(j, null, p);
	}
	
	/**
	 * Removes the prisoner from the storage system.
	 * 
	 * @param j the jail which the prisoner is in.
	 * @param c the cell which the prisoner is in, null if none
	 * @param p the prisoner data
	 */
	public void removePrisoner(Jail j, Cell c, Prisoner p) {
		switch(storage) {
			case 1:
			case 2:
				break;
			default:
				if(c == null)
					flat.set("jails." + j.getName() + ".prisoners." + p.getName(), null);
				else
					flat.set("jails." + j.getName() + "." + c.getName() + ".prisoner", null);
				
				try {
					flat.save(new File(pl.getDataFolder(), "data.yml"));
				} catch (IOException e) {
					pl.getLogger().severe("Unable to save the Jail data: " + e.getMessage());
				}
				break;
		}
	}
	
	/**
	 * Removes a jail from the storage system.
	 * 
	 * @param name of the jail to remove.
	 */
	public void removeJail(String name) {
		switch(storage) {
			case 1:
			case 2:
				break;
			default:
				flat.set("jails." + name, null);
				
				try {
					flat.save(new File(pl.getDataFolder(), "data.yml"));
				} catch (IOException e) {
					pl.getLogger().severe("Unable to remove the jail " + name +  " from the storage: " + e.getMessage());
				}
				break;
		}
	}
}
