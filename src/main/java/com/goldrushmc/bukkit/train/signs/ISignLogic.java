package com.goldrushmc.bukkit.train.signs;

import java.util.Map;

import org.bukkit.Chunk;
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
	 * Add a {@link Sign} to the permission mapping.
	 * 
	 * @param sign The {@link Sign} to add.
	 */
	public void addSign(Sign sign, SignType type);
	
	/**
	 * Adds a {@link Sign} to the set of signs.
	 * 
	 * @param signName The {@link Sign}'s name
	 * @param sign The {@link Sign}
	 */
	public void addSign(String signName, Sign sign);
	/**
	 * Removes all references to the specified sign.
	 * 
	 * @param signName The name of the {@link Sign} to remove.
	 */
 	public void removeSign(String signName);
	
	/**
	 * Finds the sign type for the specified sign.
	 * 
	 * @param sign The sign to check.
	 * @return The type, if any, of the sign.
	 */
	public SignType getSignType(Sign sign);
	
	/**
	 * Gets a {@link Sign} with the specified name.
	 * 
	 * @param signName The {@link Sign} name.
	 * @return The {@link Sign}
	 */
	public Sign getSign(String signName);
	/**
	 * Finds all of the relevant {@link Sign}s to Gold Rush MC, within a given chunk.
	 * 
	 * @param world The {@link World} to search.
	 */
	public void findRelevantSigns(Chunk chunk);
	
}
