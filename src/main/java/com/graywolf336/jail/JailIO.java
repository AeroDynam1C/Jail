package com.graywolf336.jail;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.graywolf336.jail.beans.Jail;
import com.graywolf336.jail.beans.SimpleLocation;

public class JailIO {
	private JailMain pl;
	private FileConfiguration flat;
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
						pl.getLogger().info("Jails configuration section exists and there are " + jails.size() + ".");
						for(String name : jails) {
							loadJail(name);
						}
					}else {
						pl.getLogger().warning("Jails configuration section exists but no jails are there.");
					}
				}
				break;
		}
		
		int s = pl.getJailManager().getJails().size();
		pl.getLogger().info("Loaded " + s + (s == 1 ? " jail." : " jails."));
	}
	
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
				Jail j = new Jail(pl, name);
				
				j.setWorld(node + "world");
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
				
				pl.getJailManager().addJail(j, false);
				break;
		}
	}
}
