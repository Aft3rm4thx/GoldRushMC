package com.goldrushmc.bukkit.tunnelcollapse;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CollapsingTunnels extends JavaPlugin {

	TunnelsListener tl = new TunnelsListener(this);
	
	SettingsManager settings = SettingsManager.getInstance();
	
	public void onEnable() {
		
		settings.setup(this);
		
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(tl, this);
		
		getCommand("fall").setExecutor(tl);
		
	}
	
	public void onDisable() {
		
	}
	
}
