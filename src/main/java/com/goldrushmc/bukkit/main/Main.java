package com.goldrushmc.bukkit.main;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.goldrushmc.bukkit.commands.AddTrain;
import com.goldrushmc.bukkit.commands.CreateTrain;
import com.goldrushmc.bukkit.commands.CreateTrainStation;
import com.goldrushmc.bukkit.commands.StationWand;
import com.goldrushmc.bukkit.db.BankTbl;
import com.goldrushmc.bukkit.db.JobTbl;
import com.goldrushmc.bukkit.db.PlayerTbl;
import com.goldrushmc.bukkit.db.TownTbl;
import com.goldrushmc.bukkit.db.TrainScheduleTbl;
import com.goldrushmc.bukkit.db.TrainStationLocationTbl;
import com.goldrushmc.bukkit.db.TrainStationTbl;
import com.goldrushmc.bukkit.db.TrainStatusTbl;
import com.goldrushmc.bukkit.db.TrainTbl;
import com.goldrushmc.bukkit.defaults.TrainDB;
import com.goldrushmc.bukkit.train.TrainLis;
import com.goldrushmc.bukkit.train.WandLis;
import com.goldrushmc.bukkit.train.station.TrainStationListener;



public final class Main extends JavaPlugin{
	
	public final TrainLis tl = new TrainLis(this);
	public final WandLis wl = new WandLis(this);
	public final TrainStationListener tsl = new TrainStationListener(this);
	public final TrainDB db = new TrainDB(this);

	@Override
	public void onEnable() {
		setupDB();
		
		getCommand("Train").setExecutor(new CreateTrain(this));
		getCommand("StationWand").setExecutor(new StationWand(this));
		getCommand("Cart").setExecutor(new AddTrain(this));
		getCommand("Station").setExecutor(new CreateTrainStation(this));
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(tl, this);
		pm.registerEvents(wl, this);
		pm.registerEvents(tsl, this);
		
		getLogger().info(getDescription().getName() + " Enabled!");		
	}
	
	private void setupDB() {
		try {
			getDatabase().find(TrainTbl.class).findRowCount();
			getDatabase().find(TrainScheduleTbl.class).findRowCount();
			getDatabase().find(TrainStatusTbl.class).findRowCount();
			getDatabase().find(TrainStationTbl.class).findRowCount();
			getDatabase().find(TrainStationLocationTbl.class).findRowCount();
			getDatabase().find(PlayerTbl.class).findRowCount();
			getDatabase().find(TownTbl.class).findRowCount();
			getDatabase().find(BankTbl.class).findRowCount();
			getDatabase().find(JobTbl.class).findRowCount();
		} catch (PersistenceException | NullPointerException e) {
			getLogger().info("Installing database for " + getDescription().getName() + " due to first time use.");
			installDDL();
		}
	}
	
	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(TrainTbl.class);
		list.add(TrainScheduleTbl.class);
		list.add(TrainStatusTbl.class);
		list.add(TrainStationTbl.class);
		list.add(TrainStationLocationTbl.class);
		list.add(PlayerTbl.class);
		list.add(TownTbl.class);
		list.add(BankTbl.class);
		list.add(JobTbl.class);
		return list;
	}
	
	@Override
	public void onDisable() {
		getLogger().info("GoldRush Plugin Disabled!");
	}

}
