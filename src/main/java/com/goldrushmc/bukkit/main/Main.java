package com.goldrushmc.bukkit.main;

import org.bukkit.plugin.java.JavaPlugin;

import com.goldrushmc.bukkit.commands.CreateTrain;
import com.goldrushmc.bukkit.commands.TrainWand;
import com.goldrushmc.bukkit.train.TrainLis;


public final class Main extends JavaPlugin{
	
	public final TrainLis tl = new TrainLis(this);

	@Override
	public void onEnable() {
		
		getCommand("Train").setExecutor(new CreateTrain(this));
		getCommand("Wand").setExecutor(new TrainWand(this));
		getServer().getPluginManager().registerEvents(tl, this);
		
		getLogger().info("GoldRush Plugin Enabled!");
	}
	
	@Override
	public void onDisable() {
		getLogger().info("GoldRush Plugin Disabled!");
	}

}
