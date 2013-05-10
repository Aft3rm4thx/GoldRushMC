package com.goldrushmc.bukkit.main;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.bukkit.plugin.java.JavaPlugin;

import com.goldrushmc.bukkit.commands.AddTrain;
import com.goldrushmc.bukkit.commands.CreateTrain;
import com.goldrushmc.bukkit.commands.TrainWand;
import com.goldrushmc.bukkit.defaults.TrainDB;
import com.goldrushmc.bukkit.train.TrainLis;
import com.goldrushmc.bukkit.train.db.TrainSchedule;
import com.goldrushmc.bukkit.train.db.TrainStation;
import com.goldrushmc.bukkit.train.db.TrainStationLocation;
import com.goldrushmc.bukkit.train.db.TrainStatus;
import com.goldrushmc.bukkit.train.db.Trains;



public final class Main extends JavaPlugin{
	
	public final TrainLis tl = new TrainLis(this);
	public final TrainDB db = new TrainDB(this);

	@Override
	public void onEnable() {
		setupDB();
		
		getCommand("Train").setExecutor(new CreateTrain(this));
		getCommand("Wand").setExecutor(new TrainWand(this));
		getCommand("Cart").setExecutor(new AddTrain(this));
		getServer().getPluginManager().registerEvents(tl, this);
		
		getLogger().info("GoldRush Plugin Enabled!");
	}
	
	private void setupDB() {
		try {
			getDatabase().find(Trains.class).findRowCount();
			getDatabase().find(TrainSchedule.class).findRowCount();
			getDatabase().find(TrainStatus.class).findRowCount();
			getDatabase().find(TrainStation.class).findRowCount();
			getDatabase().find(TrainStationLocation.class).findRowCount();
		} catch (PersistenceException | NullPointerException e) {
			getLogger().info("Installing database for " + getDescription().getName() + " due to first time use.");
			installDDL();
		}
	}
	
	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(Trains.class);
		list.add(TrainSchedule.class);
		list.add(TrainStatus.class);
		list.add(TrainStation.class);
		list.add(TrainStationLocation.class);
		return list;
	}
	
	@Override
	public void onDisable() {
		getLogger().info("GoldRush Plugin Disabled!");
	}

}
