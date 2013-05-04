package com.goldrushmc.bukkit.train;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.goldrushmc.bukkit.train.util.TrainTools;
import com.goldrushmc.bukkit.train.util.TrainTools.TrainType;

/**
 * <p>This class is in charge of facilitating the TrainCarts plugin.</p>
 * <p>It will be used for creating and deleting trains, as well as keeping track of them.</p>
 * 
 * @author Diremonsoon09
 * @version 1.0
 */
public class TrainFactory {

	public static List<String> trainNames = new ArrayList<String>();

	//Stores the player's rail selections
	public static Map<Player, Location[]> selections = new HashMap<Player, Location[]>();

	/**
	 * <p>Use this to create new Standard Trains on the map.</p>
	 * <p>This is configured so trains ALWAYS SPAWN RIGHT TO LEFT
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
	public static void newStandardTrain(Player player, String nameOfTrain, String trainType) {

		//Get the cardinal direction to the left of the player.
		BlockFace leftDir = TrainTools.toTheLeft(TrainTools.getDirection(player));

		Location[] locMap = selections.get(player);
		Location trainSpawn = null;
		if(locMap[0] == null && locMap[1] != null) trainSpawn = locMap[1];
		else if(locMap[0] != null) trainSpawn = locMap[0];
		if(!TrainTools.singleRailCheck(trainSpawn)) return;

		//Specifying the cart types to be created. This is simply one of each.
		List<EntityType> carts = new LinkedList<EntityType>();
		carts.add(EntityType.MINECART);
		carts.add(EntityType.MINECART_CHEST);
		carts.add(EntityType.MINECART_FURNACE);

		makeTrain(TrainType.getType(trainType), nameOfTrain, trainSpawn.getBlock(), leftDir, carts);
	}



	/**
	 * Use this to create new Custom Trains on the map.
	 * 
	 * @param trainSpawn Location of where to spawn the train.
	 * @param nameOfTrain The name of the new train.
	 * @param numOfPassengers The amount of passenger seats available.
	 * @param numOfChests The amount of storage chests available.
	 */
	public static void newCustomTrain(Player player, String nameOfTrain, String trainType, int numOfPassengers, int numOfChests) {

		//Get the cardinal direction to the left of the player.
		BlockFace leftDir = TrainTools.toTheLeft(TrainTools.getDirection(player));

		Location[] locMap = selections.get(player);
		Location trainSpawn = locMap[0];		

		//Just makes sure the locations chosen are appropriate.
		if(!TrainTools.singleRailCheck(trainSpawn)) return;

		//		PathFinder pf = new PathFinder(trainSpawn.getBlock());

		//Measure the distance between the two locations, to see how big they want the train.
		int dist = TrainTools.getDistance(trainSpawn, locMap[1]);
		player.sendMessage("The distance between your two points are: " + dist);

		if(dist < (numOfPassengers + numOfChests)) {
			player.sendMessage("There is not enough room to put a train here!");
			player.sendMessage("----------------------------------------------");
			player.sendMessage("As a standard rule of thumb, count the number of carts of the train, and add 4 to it.");
			player.sendMessage("That is how much space is required.");
			return;
		}

		//Specifying the cart types to be created. This is simply one of each.
		List<EntityType> carts = new LinkedList<EntityType>();
		for(int i = 0; i < numOfPassengers; i++) {
			carts.add(EntityType.MINECART);
		}
		for(int i = 0; i < numOfChests; i++) {
			carts.add(EntityType.MINECART_CHEST);
		}
		carts.add(EntityType.MINECART_FURNACE);

		makeTrain(TrainType.getType(trainType), nameOfTrain, trainSpawn.getBlock(), leftDir, carts);
	}

	/**
	 *  Use this to create new Passenger Trains on the map.
	 *  
	 * @param trainSpawn Locaton of where to spawn the train
	 * @param nameOfTrain The name of the new train
	 * @param numOfPassengers The amount of passenger seats available
	 * @return
	 */
	public static void newPassengerTrain(Player player, String nameOfTrain, String trainType, int numOfPassengers) {

		//Get the cardinal direction to the left of the player.
		BlockFace leftDir = TrainTools.toTheLeft(TrainTools.getDirection(player));

		Location[] locMap = selections.get(player);
		Location trainSpawn = locMap[0];		

		//Just makes sure the locations chosen are appropriate.
		if(!TrainTools.singleRailCheck(trainSpawn)) return;

		//		PathFinder pf = new PathFinder(trainSpawn.getBlock());

		//Measure the distance between the two locations, to see how big they want the train.
		int dist = TrainTools.getDistance(trainSpawn, locMap[1]);
		player.sendMessage("The distance between your two points are: " + dist);


		if(dist < numOfPassengers) {
			player.sendMessage("There is not enough room to put a train here!");
			player.sendMessage("----------------------------------------------");
			player.sendMessage("As a standard rule of thumb, count the number of carts of the train, and add 4 to it.");
			player.sendMessage("That is how much space is required.");
			return;
		}

		//Specifying the cart types to be created. This is simply one of each.
		List<EntityType> carts = new LinkedList<EntityType>();
		for(int i = 0; i < numOfPassengers; i++) {
			carts.add(EntityType.MINECART);
		}
		
		carts.add(EntityType.MINECART_FURNACE);

		makeTrain(TrainType.getType(trainType), nameOfTrain, trainSpawn.getBlock(), leftDir, carts);
	}

	/**
	 * Use this to create new Storage Trains on the map.
	 * 
	 * @param trainSpawn Location of where to spawn the train.
	 * @param nameOfTrain The name of the new train.
	 * @param numOfChests The amount of storage chests available.
	 */
	public static void newStorageTrain(Player player, String nameOfTrain, String trainType, int numOfChests) {
		//Get the cardinal direction to the left of the player.
		BlockFace leftDir = TrainTools.toTheLeft(TrainTools.getDirection(player));

		Location[] locMap = selections.get(player);
		Location trainSpawn = locMap[0];		

		//Just makes sure the locations chosen are appropriate.
		if(!TrainTools.singleRailCheck(trainSpawn)) return;

		//		PathFinder pf = new PathFinder(trainSpawn.getBlock());

		//Measure the distance between the two locations, to see how big they want the train.
		int dist = TrainTools.getDistance(trainSpawn, locMap[1]);
		player.sendMessage("The distance between your two points are: " + dist);

		if(dist < numOfChests) {
			player.sendMessage("There is not enough room to put a train here!");
			player.sendMessage("----------------------------------------------");
			player.sendMessage("As a standard rule of thumb, count the number of carts of the train, and add 4 to it.");
			player.sendMessage("That is how much space is required.");
			return;
		}

		//Specifying the cart types to be created. This is simply one of each.
		List<EntityType> carts = new LinkedList<EntityType>();
		for(int i = 0; i < numOfChests; i++) {
			carts.add(EntityType.MINECART_CHEST);
		}
		carts.add(EntityType.MINECART_FURNACE);
		
		makeTrain(TrainType.getType(trainType), nameOfTrain, trainSpawn.getBlock(), leftDir, carts);
	}

	private static void makePublicTrain(String nameOfTrain, Block b, BlockFace face, List<EntityType> carts) {
		//Add carts to a group to link them
		MinecartGroup mg = MinecartGroup.spawn(b, face, carts);
		TrainProperties tp = TrainProperties.create();

		//Properties of a Public Transportation Train
		tp.setName(nameOfTrain);
		tp.setSpeedLimit(1.0);
		tp.setPickup(false);
		tp.setKeepChunksLoaded(true);
		mg.setProperties(tp);

		trainNames.add(nameOfTrain);

	}

	private static void makeTownTrain(String nameOfTrain, Block b, BlockFace face, List<EntityType> carts) {
		//Add carts to a group to link them
		MinecartGroup mg = MinecartGroup.spawn(b, face, carts);
		TrainProperties tp = TrainProperties.create();

		//Properties of a Town Train
		tp.setName(nameOfTrain);
		tp.setSpeedLimit(2.0);
		tp.setPickup(false);
		tp.setKeepChunksLoaded(true);
		tp.setManualMovementAllowed(false);
		tp.setPlayerTakeable(false);
		mg.setProperties(tp);

		trainNames.add(nameOfTrain);
	}
	
	private static void makeTrain(TrainType trainType, String trainName, Block b, BlockFace face, List<EntityType> carts) {
		switch(trainType) {
		case PRIVATE: break;
		case PUBLIC: makePublicTrain(trainName, b, face, carts); break;
		case TOWN: makeTownTrain(trainName, b, face, carts);	break;
		case TRANSPORT: break;
		default: break;
		}
	}
}
