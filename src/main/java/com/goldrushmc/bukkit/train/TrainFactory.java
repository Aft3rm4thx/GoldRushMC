package com.goldrushmc.bukkit.train;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;

/**
 * <p>This class is in charge of facilitating the TrainCarts plugin.</p>
 * <p>It will be used for creating and deleting trains, as well as keeping track of them.</p>
 * 
 * @author Diremonsoon09
 * @version 1.0
 */
public class TrainFactory {
	
	private final JavaPlugin plugin;
	
	public static List<String> trainNames = new ArrayList<String>();
	
	public TrainFactory(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * <p>Use this to create new Standard Trains on the map.</p>
	 * 
	 * <p><b>Standard trains have:</b></p>
	 * <ol>
	 * 	<li>1 Passenger Cart</li>
	 * 	<li>1 Storage Cart</li>
	 * 	<li>1 Furnace Cart (Power)</li>
	 * </ol>
	 * 
	 * @param trainSpawn Location of where to spawn the train
	 * @param nameOfTrain The name of the new train
	 */
	public static boolean newStandardTrain(Location trainSpawn, String nameOfTrain) {

		if(!isRail(trainSpawn.getBlock())) {
			return false; //If not a rail, return false.
		}
		BlockFace direction = getDirection(trainSpawn.getYaw());

		//Specifying the cart types to be created. This is simply one of each.
		List<EntityType> carts = new LinkedList<EntityType>();
		carts.add(EntityType.MINECART);
		carts.add(EntityType.MINECART_CHEST);
		carts.add(EntityType.MINECART_FURNACE);

		MinecartGroup mg = MinecartGroup.spawn(trainSpawn.getBlock(), direction, carts);
		TrainProperties tp = TrainProperties.create();
		tp.setName(nameOfTrain);
		tp.setSpeedLimit(5.0);
		tp.setPickup(false);
		mg.setProperties(tp);
		
		trainNames.add(nameOfTrain);
		
		return true;
	}

	/**
	 * Use this to create new Custom Trains on the map.
	 * 
	 * @param trainSpawn Location of where to spawn the train.
	 * @param nameOfTrain The name of the new train.
	 * @param numOfPassengers The amount of passenger seats available.
	 * @param numOfChests The amount of storage chests available.
	 */
	public static boolean newCustomTrain(Location trainSpawn, String nameOfTrain, int numOfPassengers, int numOfChests) {

		if(!isRail(trainSpawn.getBlock())) {
			return false; //If not a rail, return false.
		}
		
		BlockFace direction = getDirection(trainSpawn.getYaw());

		// Specifying the cart types to be created.
		List<EntityType> carts = new LinkedList<EntityType>();
		for(int i = 0; i < numOfPassengers; i++) {
			carts.add(EntityType.MINECART);	
		}
		for(int i = 0; i < numOfChests; i++) {
			carts.add(EntityType.MINECART_CHEST);	
		}
		carts.add(EntityType.MINECART_FURNACE);

		//Creates the minecart group (in theory).
		MinecartGroup mg = MinecartGroup.spawn(trainSpawn.getBlock(), direction, carts);
		TrainProperties tp = TrainProperties.create();
		tp.setName(nameOfTrain);
		tp.setSpeedLimit(5.0);
		tp.setPickup(false);
		mg.setProperties(tp);
		
		trainNames.add(nameOfTrain);
		return true;
	}
	
	/**
	 *  Use this to create new Passenger Trains on the map.
	 *  
	 * @param trainSpawn Locaton of where to spawn the train
	 * @param nameOfTrain The name of the new train
	 * @param numOfPassengers The amount of passenger seats available
	 * @return
	 */
	public static boolean newPassengerTrain(Location trainSpawn, String nameOfTrain, int numOfPassengers) {
		if(!isRail(trainSpawn.getBlock())) {
			return false; //If not a rail, return false.
		}
		
		BlockFace direction = getDirection(trainSpawn.getYaw());

		// Specifying the cart types to be created.
		List<EntityType> carts = new LinkedList<EntityType>();
		for(int i = 0; i < numOfPassengers; i++) {
			carts.add(EntityType.MINECART);	
		}
		carts.add(EntityType.MINECART_FURNACE);

		//Creates the minecart group (in theory).
		MinecartGroup mg = MinecartGroup.spawn(trainSpawn.getBlock(), direction, carts);
		TrainProperties tp = TrainProperties.create();
		tp.setName(nameOfTrain);
		tp.setSpeedLimit(5.0);
		tp.setPickup(false);
		mg.setProperties(tp);
		
		trainNames.add(nameOfTrain);
		return true;
	}
	
	/**
	 * Use this to create new Storage Trains on the map.
	 * 
	 * @param trainSpawn Location of where to spawn the train.
	 * @param nameOfTrain The name of the new train.
	 * @param numOfChests The amount of storage chests available.
	 */
	public static boolean newStorageTrain(Location trainSpawn, String nameOfTrain, int numOfChests) {
		if(!isRail(trainSpawn.getBlock())) {
			return false; //If not a rail, return false.
		}
		
		BlockFace direction = getDirection(trainSpawn.getYaw());

		// Specifying the cart types to be created.
		List<EntityType> carts = new LinkedList<EntityType>();
		for(int i = 0; i < numOfChests; i++) {
			carts.add(EntityType.MINECART);	
		}
		carts.add(EntityType.MINECART_FURNACE);

		//Creates the minecart group (in theory).
		MinecartGroup mg = MinecartGroup.spawn(trainSpawn.getBlock(), direction, carts);
		TrainProperties tp = TrainProperties.create();
		tp.setName(nameOfTrain);
		tp.setSpeedLimit(5.0);
		tp.setPickup(false);
		mg.setProperties(tp);
		
		trainNames.add(nameOfTrain);
		return true;
	}

	/**
	 * Gets the BlockFace direction, based on the Yaw
	 * 
	 * @param yaw The direction in {@code double} form, to be translated
	 * @return The BlockFace direction
	 */
	private static BlockFace getDirection(double yaw) {
		if (0 <= yaw && yaw < 22.5) {
			return BlockFace.NORTH;
		} else if (22.5 <= yaw && yaw < 67.5) {
			return BlockFace.NORTH_EAST;
		} else if (67.5 <= yaw && yaw < 112.5) {
			return BlockFace.EAST;
		} else if (112.5 <= yaw && yaw < 157.5) {
			return BlockFace.SOUTH_EAST;
		} else if (157.5 <= yaw && yaw < 202.5) {
			return BlockFace.SOUTH;
		} else if (202.5 <= yaw && yaw < 247.5) {
			return BlockFace.SOUTH_WEST;
		} else if (247.5 <= yaw && yaw < 292.5) {
			return BlockFace.WEST;
		} else if (292.5 <= yaw && yaw < 337.5) {
			return BlockFace.NORTH_WEST;
		} else if (337.5 <= yaw && yaw < 360.0) {
			return BlockFace.NORTH;
		} else {
			return null;
		}
	}

	/**
	 * Checks whether or not the block is a rail block.
	 * 
	 * @param block The block in question.
	 * @return true if it is a rail, false otherwise.
	 */
	private static boolean isRail(Block block) {
		return block.getType() == Material.RAILS || block.getType() == Material.POWERED_RAIL || block.getType() == Material.ACTIVATOR_RAIL;
	}
	
	public boolean loadTrains() {
		List<Map<?,?>> existing = plugin.getConfig().getMapList("trains");
		
		return false;
	}
}
