package com.goldrushmc.bukkit.defaults;

import java.util.List;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Query;
import com.goldrushmc.bukkit.train.db.TrainSchedule;
import com.goldrushmc.bukkit.train.db.TrainStatus;
import com.goldrushmc.bukkit.train.db.Trains;

public interface DBTrainsAccessible {

	/**
	 * Queries the DB for the TrainSchedule class.
	 * 
	 * @return
	 */
	Query<TrainSchedule> querySchedule();
	
	/**
	 * Queries the DB for the Trains class.
	 * 
	 * @return
	 */
	Query<Trains> queryTrains();
	
	/**
	 * Queries the DB for the TrainStatus class.
	 * 
	 * @return
	 */
	Query<TrainStatus> queryStatus();
	
	/**
	 * Gets all of the current schedules for all trains.
	 * 
	 * @return
	 */	
	List<TrainSchedule> getSchedules();
	
	/**
	 * Gets all of the current trains in the server.
	 * 
	 * @return
	 */
	List<Trains> getTrains();
	
	/**
	 * Gets all of the current statuses.
	 * 
	 * @return
	 */
	List<TrainStatus> getStatuses();
	
	/**
	 * Gets the next train schedule for the specified train via {@link Trains} class.
	 * 
	 * @param train The {@link Trains} class specified.
	 * @return
	 */
	TrainSchedule getNextDeparture(Trains train);
	
	/**
	 * Gets the next train schedule for the specified train via Train Name.
	 * 
	 * @param trainName The name of the Train.
	 * @return
	 */
	TrainSchedule getNextDeparture(String trainName);
	
	/**
	 * Gets a {@link Trains} class based off of its name.
	 * 
	 * @param trainName The name of the train.
	 * @return
	 */
	Trains getTrain(String trainName);
	
	/**
	 * Gets the train's current status via {@link Trains} class.
	 * 
	 * @param train The {@link Trains} class specified.
	 * @return
	 */
	TrainStatus getTrainStatus(Trains train);
	
	/**
	 * Gets the train's current status via Train Name.
	 * 
	 * @param trainName The {@link Trains} class specified.
	 * @return
	 */
	TrainStatus getTrainStatus(String trainName);
	
	/**
	 * Gets the Database instance of the plugin.
	 * 
	 * @return
	 */
	EbeanServer getDB();
}
