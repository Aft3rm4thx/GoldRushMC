package com.goldrushmc.bukkit.defaults;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Query;
import com.goldrushmc.bukkit.train.db.TrainSchedule;
import com.goldrushmc.bukkit.train.db.TrainStatus;
import com.goldrushmc.bukkit.train.db.Trains;

public class TrainDB implements DBTrainsAccessible {

	private final JavaPlugin plugin;

	public TrainDB(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public Query<TrainSchedule> querySchedule() {
		return getDB().find(TrainSchedule.class);
	}

	@Override
	public Query<Trains> queryTrains() {
		return getDB().find(Trains.class);
	}

	@Override
	public Query<TrainStatus> queryStatus() {
		return getDB().find(TrainStatus.class);
	}

	@Override
	public List<TrainSchedule> getSchedules() {
		return querySchedule().findList();
	}

	@Override
	public List<Trains> getTrains() {
		return queryTrains().findList();
	}

	@Override
	public List<TrainStatus> getStatuses() {
		return queryStatus().findList();
	}

	@Override
	public TrainSchedule getNextDeparture(Trains train) {
		for(TrainSchedule ts : train.getSchedule()) {
			if(ts.isNext()) return ts;
		}
		return null;
	}

	@Override
	public TrainSchedule getNextDeparture(String trainName) {
		return getNextDeparture(getTrain(trainName));
	}

	@Override
	public Trains getTrain(String trainName) {
		return queryTrains().where().ieq("TRAIN_NAME", trainName).findUnique();
	}

	@Override
	public TrainStatus getTrainStatus(Trains train) {
		return train.getStatus();
	}

	@Override
	public TrainStatus getTrainStatus(String trainName) {
		return getTrainStatus(getTrain(trainName));
	}

	@Override
	public EbeanServer getDB() {
		return plugin.getDatabase();
	}

}
