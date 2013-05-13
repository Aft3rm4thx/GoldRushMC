package com.goldrushmc.bukkit.defaults;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Query;
import com.goldrushmc.bukkit.db.TrainScheduleTbl;
import com.goldrushmc.bukkit.db.TrainStatusTbl;
import com.goldrushmc.bukkit.db.TrainTbl;

public class TrainDB implements DBTrainsAccessible {

	private final JavaPlugin plugin;

	public TrainDB(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public Query<TrainScheduleTbl> querySchedule() {
		return getDB().find(TrainScheduleTbl.class);
	}

	@Override
	public Query<TrainTbl> queryTrains() {
		return getDB().find(TrainTbl.class);
	}

	@Override
	public Query<TrainStatusTbl> queryStatus() {
		return getDB().find(TrainStatusTbl.class);
	}

	@Override
	public List<TrainScheduleTbl> getSchedules() {
		return querySchedule().findList();
	}

	@Override
	public List<TrainTbl> getTrains() {
		return queryTrains().findList();
	}

	@Override
	public List<TrainStatusTbl> getStatuses() {
		return queryStatus().findList();
	}

	@Override
	public TrainScheduleTbl getNextDeparture(TrainTbl train) {
		for(TrainScheduleTbl ts : train.getSchedule()) {
			if(ts.isNext()) return ts;
		}
		return null;
	}

	@Override
	public TrainScheduleTbl getNextDeparture(String trainName) {
		return getNextDeparture(getTrain(trainName));
	}

	@Override
	public TrainTbl getTrain(String trainName) {
		return queryTrains().where().ieq("TRAIN_NAME", trainName).findUnique();
	}

	@Override
	public TrainStatusTbl getTrainStatus(TrainTbl train) {
		return train.getStatus();
	}

	@Override
	public TrainStatusTbl getTrainStatus(String trainName) {
		return getTrainStatus(getTrain(trainName));
	}

	@Override
	public EbeanServer getDB() {
		return plugin.getDatabase();
	}

}
