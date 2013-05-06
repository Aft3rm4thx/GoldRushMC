package com.goldrushmc.bukkit.train.signs;

import java.util.Map;

import org.bukkit.World;
import org.bukkit.block.Sign;

public interface ISignLogic {

	/**
	 * Gets the stored {@code Map<?,?>} of {@link Sign} and {@link SignType}
	 * 
	 * @return The set of signs, {@code Set<Sign>}.
	 */
	public Map<Sign, SignType> getSigns();
	
	/**
	 * Add a sign to the set of signs.
	 * 
	 * @param sign The sign to add.
	 */
	public void addSign(Sign sign, SignType type);
	
	/**
	 * Removes a sign from the set of signs.
	 * 
	 * @param sign The sign to remove.
	 */
	public void removeSign(Sign sign);
	
	/**
	 * Finds the sign type for the specified sign.
	 * 
	 * @param sign The sign to check.
	 * @return The type, if any, of the sign.
	 */
	public SignType getSignType(Sign sign);
	
	/**
	 * Finds all of the relevant {@link Sign}s to Gold Rush MC, within a given world.
	 * 
	 * @param world The {@link World} to search.
	 */
	public void findRelevantSigns(World world);
	
}
