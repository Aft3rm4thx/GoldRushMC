package com.goldrushmc.bukkit.train.signs;

import java.util.List;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * Controls and remembers the signs within a given {@link Chunk}. 
 * 
 * @author Diremonsoon
 *
 */
public interface ISignLogic {

	/**
	 * Gets the stored {@code Map<?,?>} of {@link Sign} and {@link SignType}
	 * 
	 * @return The set of signs, {@code Set<Sign>}.
	 */
	public Map<SignType, Sign> getSignTypes();
	
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
	 * Gets a {@link Sign} with the specified name.
	 * 
	 * @param signName The {@link Sign} name.
	 * @return The {@link Sign}
	 */
	public Sign getSign(String signName);
	
	public Sign getSign(SignType type);
	/**
	 * Finds all of the relevant {@link Sign}s to Gold Rush MC, within a given chunk.
	 * 
	 * @param chunk The {@link Chunk} to search.
	 */
	public void findRelevantSigns(List<Block> blocks);
	
	/**
	 * Gets the {@link Chunk} associated with this logic.
	 * @return
	 */
	public List<Sign> getSigns();
	
	/**
	 * Updates all the signs with the right train name, so players can buy and sell properly from that train.
	 * 
	 * @param trainName
	 */
	public void updateTrain(String trainName);
	
}
