package com.goldrushmc.bukkit.main;

import org.bukkit.plugin.java.JavaPlugin;

import com.goldrushmc.bukkit.commands.AddTrain;
import com.goldrushmc.bukkit.commands.CreateTrain;
import com.goldrushmc.bukkit.commands.TrainWand;
import com.goldrushmc.bukkit.train.TrainLis;
import com.goldrushmc.bukkit.train.signs.SignLogic;


public final class Main extends JavaPlugin{
	
	public final TrainLis tl = new TrainLis(this);
	public SignLogic sl;

	@Override
	public void onEnable() {
		
		getCommand("Train").setExecutor(new CreateTrain(this));
		getCommand("Wand").setExecutor(new TrainWand(this));
		getCommand("Cart").setExecutor(new AddTrain(this));
		getServer().getPluginManager().registerEvents(tl, this);
		
		getLogger().info("GoldRush Plugin Enabled!");
	}
	
	@Override
	public void onDisable() {
		getLogger().info("GoldRush Plugin Disabled!");
	}

}
