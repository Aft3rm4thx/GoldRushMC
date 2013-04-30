package com.goldrushmc.bukkit.train;

import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ITrain {

	/**
	 * Will start the train, although this might be able to be facilitated by a vanilla stone switch...
	 */
	public void start();
	
	/**
	 * Will stop the train.
	 */
	public void stop();
	
	/**
	 * Gets a list of passengers currently riding the train.
	 * 
	 * @return The players riding on the train.
	 */
	public List<Player> getPassengers();
	
	
	/**
	 * Gets a list of the items aboard the train's storage.
	 * 
	 * @return The items in the payload.
	 */
	public List<ItemStack> getPayload();
	
	/**
	 * Sets the passengers on the train.
	 * 
	 * @param passengers Sets the passengers on the train.
	 */
	public void setPassengers(List<Player> passengers);
	
	/**
	 * Sets the payload on the train.
	 * 
	 * @param payload Sets the items to be on the train's payload.
	 */
	public void setPayload(List<ItemStack> payload);
	
	/**
	 * Gets the next stop's name.
	 * 
	 * @return the stop's name.
	 */
	public String nextStop();
	
	/**
	 * Gets the stops that the train has on its route.
	 * 
	 * @return {@code Set<String>} of stop names.
	 */
	public Set<String> getStops();
	
	/**
	 * Adds stops to a train's route.
	 * 
	 * @param stopName The name of the stop.
	 * @param stopLoc The location in the world the stop is.
	 */
	public void addStop(String stopName, Location stopLoc);
	
	/**
	 * Removes stops from a train's route.
	 * 
	 * @param stopName The stop to be removed.
	 */
	public void removeStop(String stopName);
	
	/**
	 * Gets flagged if the train's path is blocked.
	 * 
	 * @return yes or no...
	 */
	public boolean isBlocked();
	
	/**
	 * Sets whether the train is blocked or not.
	 * 
	 * @param isBlocked
	 */
	public void setBlocked(boolean isBlocked);
}
