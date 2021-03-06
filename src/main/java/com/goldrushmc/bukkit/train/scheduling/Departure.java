package com.goldrushmc.bukkit.train.scheduling;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import com.goldrushmc.bukkit.train.station.TrainStation;

public class Departure implements Runnable {

//	private final DBTrainsAccessible db;

	public Departure(final JavaPlugin plugin) {
//		this.db = new DBAccess(plugin);
	}

	@Override
	public void run() {
		List<TrainStation> stations = TrainStation.getTrainStations();
		//If the list is null, we can't quite send trains on their way!
		if(stations == null) return;
		//For each station, send the next train on its way, and bring the next forward.
		for(TrainStation station : stations) {
			boolean hasTrain = station.pushQueue();
//			if(!hasTrain) Bukkit.broadcastMessage(station.getStationName() + " has no trains to depart...");
		}
	}


}
