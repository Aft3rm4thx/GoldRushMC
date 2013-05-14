package com.goldrushmc.bukkit.train.scheduling;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import com.goldrushmc.bukkit.db.TrainScheduleTbl;
import com.goldrushmc.bukkit.db.TrainStationTbl;
import com.goldrushmc.bukkit.defaults.DBTrainsAccessible;
import com.goldrushmc.bukkit.defaults.TrainDB;
import com.goldrushmc.bukkit.train.station.TrainStation;

public class Departure implements Runnable {

	private final DBTrainsAccessible db;

	public Departure(final JavaPlugin plugin) {
		this.db = new TrainDB(plugin);
	}

	@Override
	public void run() {
		List<TrainStation> stations = TrainStation.getTrainStations();
		for(TrainStation station : stations) {
			TrainStationTbl sClass = db.getTrainStation(station.getStationName());
			for(TrainScheduleTbl tClass : sClass.getDepartures()) {
				
			}
		}
	}


}
