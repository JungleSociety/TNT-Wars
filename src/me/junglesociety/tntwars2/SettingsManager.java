package me.junglesociety.tntwars2;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class SettingsManager {
	
	
	/*
	 * 
	 * CREDIT TO POGOSTICK29DEV FOR THIS LIBRARY
	 * 
	 */

	private SettingsManager() { }
	
	static SettingsManager instance = new SettingsManager();
	
	public static SettingsManager getInstance() {
		return instance;
	}
	
	Plugin p;
	
	FileConfiguration data;
	File dfile;
	
	public void setup(Plugin p) {
		if (!p.getDataFolder().exists()) {
			p.getDataFolder().mkdir();
		}
		
		dfile = new File(p.getDataFolder(), "data.yml");
		
		if (!dfile.exists()) {
			try {
				dfile.createNewFile();
			}
			catch (IOException e) {
				Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not create data.yml!");
			}
		}
		
		data = YamlConfiguration.loadConfiguration(dfile);
	}
	
	public FileConfiguration getData() {
		return data;
	}
	
	public void saveData() {
		try {
			data.save(dfile);
		}
		catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save data.yml!");
		}
	}
	
	public void reloadData() {
		data = YamlConfiguration.loadConfiguration(dfile);
	}
	
	public PluginDescriptionFile getDesc() {
		return p.getDescription();
	}
}