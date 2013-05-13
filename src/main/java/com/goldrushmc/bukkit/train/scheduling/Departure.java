package com.goldrushmc.bukkit.train.scheduling;

import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.goldrushmc.bukkit.defaults.DBTrainsAccessible;
import com.goldrushmc.bukkit.defaults.TrainDB;
import com.goldrushmc.bukkit.train.station.TrainStation;

public class Departure implements Runnable {

	private final TrainStation station;
	private final DBTrainsAccessible db;

	public Departure(final TrainStation station, final JavaPlugin plugin) {
		this.station = station;
		this.db = new TrainDB(plugin);
	}

	@Override
	public void run() {
		MinecartGroup[] trains = station.getDepartingTrains();
		for(int i = 0; i < trains.length; i++) {
			trains[i].setForwardForce(0.4);
//			TrainTbl train = db.getTrain(trains[i].getProperties().getTrainName());
			db.getStatuses();
		}

	}

}
