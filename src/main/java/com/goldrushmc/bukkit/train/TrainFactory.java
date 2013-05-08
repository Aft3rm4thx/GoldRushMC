package com.goldrushmc.bukkit.train;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberChest;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberRideable;
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

	//References the train groups and their names.
	public static Map<String, MinecartGroup> trains = new HashMap<String, MinecartGroup>();

	//References the owners to a list of minecarts they own.
	public static Map<Player, List<MinecartMemberChest>> ownerList = new HashMap<Player, List<MinecartMemberChest>>();
	public static Map<Player, List<MinecartMemberRideable>> ownerRideable = new HashMap<Player, List<MinecartMemberRideable>>();

	//Stores the player's rail selections
	public static Map<Player, Location[]> selections = new HashMap<Player, Location[]>();

	/**
	 * <p><b>FOR TESTING PURPOSES ONLY</b></p>
	 * 
	 * <p>Use this to create new Standard Trains on the map.</p>
	 * <p>This is configured so trains ALWAYS SPAWN RIGHT TO LEFT</p>
	 * 
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
	public static void newStandardTrain(Player player, String nameOfTrain, String trainType, double speedLimit) {

		//Get the cardinal direction to the left of the player.
		BlockFace leftDir = TrainTools.toTheLeft(TrainTools.getDirection(player));

		Location[] locMap = selections.get(player);
		Location trainSpawn = null;
		if(locMap[0] == null && locMap[1] != null) trainSpawn = locMap[1];
		else if(locMap[0] != null) trainSpawn = locMap[0];
		if(!TrainTools.singleRailCheck(trainSpawn)) return;

		//Specifying the cart types to be created. This is simply one of each.
		List<EntityType> carts = new LinkedList<EntityType>();
		carts.add(EntityType.MINECART_FURNACE);
		carts.add(EntityType.MINECART);
		carts.add(EntityType.MINECART_CHEST);


		makeTrain(TrainType.getType(trainType), nameOfTrain, trainSpawn.getBlock(), leftDir, carts, speedLimit);
	}



	/**
	 * Use this to create new Custom Trains on the map.
	 * 
	 * <p><b>Custom trains have:</b></p>
	 * <ol>
	 * 	<li>N Passenger Cart(s)</li>
	 * 	<li>N Storage Cart(s)</li>
	 * 	<li>1 Furnace Cart (Power)</li>
	 * </ol>
	 * 
	 * @param trainSpawn Location of where to spawn the train.
	 * @param nameOfTrain The name of the new train.
	 * @param numOfPassengers The amount of passenger seats available.
	 * @param numOfChests The amount of storage chests available.
	 * @param player The player spawning the train.
	 * @param speedLimit the limit of speed.
	 * @param trainType The type of train to be spawned.
	 */
	public static void newCustomTrain(Player player, String nameOfTrain, String trainType, int numOfPassengers, int numOfChests, double speedLimit) {

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

		carts.add(EntityType.MINECART_FURNACE);
		for(int i = 0; i < numOfPassengers; i++) {
			carts.add(EntityType.MINECART);
		}
		for(int i = 0; i < numOfChests; i++) {
			carts.add(EntityType.MINECART_CHEST);
		}


		makeTrain(TrainType.getType(trainType), nameOfTrain, trainSpawn.getBlock(), leftDir, carts, speedLimit);
	}

	/**
	 *  Use this to create new Passenger Trains on the map.
	 *  
	 * <p><b>Passenger trains have:</b></p>
	 * <ol>
	 * 	<li>N Passenger Cart(s)</li>
	 * 	<li>1 Furnace Cart (Power)</li>
	 * </ol>
	 *  
	 * @param trainSpawn Locaton of where to spawn the train
	 * @param nameOfTrain The name of the new train
	 * @param numOfPassengers The amount of passenger seats available
	 * @param player The player spawning the train.
	 * @param speedLimit the limit of speed.
	 * @param trainType The type of train to be spawned.
	 */
	public static void newPassengerTrain(Player player, String nameOfTrain, String trainType, int numOfPassengers, double speedLimit) {

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

		//Specifying the cart types to be created.
		List<EntityType> carts = new LinkedList<EntityType>();

		carts.add(EntityType.MINECART_FURNACE);
		for(int i = 0; i < numOfPassengers; i++) {
			carts.add(EntityType.MINECART);
		}

		makeTrain(TrainType.getType(trainType), nameOfTrain, trainSpawn.getBlock(), leftDir, carts, speedLimit);
	}

	/**
	 * Use this to create new Storage Trains on the map.
	 * 
	 * <p><b>Storage trains have:</b></p>
	 * <ol>
	 * 	<li>N Storage Cart(s)</li>
	 * 	<li>1 Furnace Cart (Power)</li>
	 * </ol>
	 * 
	 * @param trainSpawn {@link Location} of where to spawn the train.
	 * @param nameOfTrain The name of the new train.
	 * @param numOfChests The amount of storage {@link Chest}s available.
	 * @param player The player spawning the train.
	 * @param speedLimit the limit of speed.
	 * @param trainType The type of train to be spawned.
	 */
	public static void newStorageTrain(Player player, String nameOfTrain, String trainType, int numOfChests, double speedLimit) {
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

		//Specifying the cart types to be created.
		List<EntityType> carts = new LinkedList<EntityType>();
		carts.add(EntityType.MINECART_FURNACE);
		for(int i = 0; i < numOfChests; i++) {
			carts.add(EntityType.MINECART_CHEST);
		}

		makeTrain(TrainType.getType(trainType), nameOfTrain, trainSpawn.getBlock(), leftDir, carts, speedLimit);
	}

	/**
	 * Creates a train with a low speed, and that is "rideable" by players.
	 * 
	 * @param nameOfTrain The name for the train
	 * @param b The {@link Block} the first minecart will spawn on.
	 * @param face The direction the train will face.
	 * @param carts The {@link EntityType} Minecarts that will represent the train.
	 */
	private static void makePublicTrain(String nameOfTrain, Block b, BlockFace face, List<EntityType> carts, double speedLimit) {
		//Add carts to a group to link them
		MinecartGroup mg = MinecartGroup.spawn(b, face, carts);
		TrainProperties tp = TrainProperties.create();

		//Properties of a Public Transportation Train
		tp.setName(nameOfTrain);
		tp.setSpeedLimit(speedLimit);
		tp.setPickup(false);
		tp.setKeepChunksLoaded(true);
		mg.setProperties(tp);

		trains.put(nameOfTrain, mg);
	}

	/**
	 * Creates a train with a low speed, and that is for transporation of goods
	 * 
	 * @param nameOfTrain The name for the train
	 * @param b The {@link Block} the first minecart will spawn on.
	 * @param face The direction the train will face.
	 * @param carts The {@link EntityType} Minecarts that will represent the train.
	 */
	private static void makeTownTrain(String nameOfTrain, Block b, BlockFace face, List<EntityType> carts) {
		//Add carts to a group to link them
		MinecartGroup mg = MinecartGroup.spawn(b, face, carts);
		TrainProperties tp = TrainProperties.create();

		//Properties of a Town Train
		tp.setName(nameOfTrain);
		tp.setSpeedLimit(0.4);
		tp.setPickup(false);
		tp.setKeepChunksLoaded(true);
		tp.setManualMovementAllowed(false);
		mg.setProperties(tp);

		trains.put(nameOfTrain, mg);
	}

	/**
	 * Will add a cart to the specified train (via name) with a specific owner.
	 * 
	 * @param owner The owner of the minecart.
	 * @param trainName The name of the train to add it to.
	 * @param cart The type {@link EntityType} of cart it will be.
	 * 
	 */
	public static void addCart(Player owner, String trainName, EntityType cart) {

		//Iterate through the groups already created, and find the one requested.
		MinecartGroup trainToLink = null;
		MinecartGroup[] groups = MinecartGroup.getGroups();
		for(int i = 0; i < groups.length; i++) {
			if(groups[i].getProperties().getTrainName().equalsIgnoreCase(trainName)) {
				trainToLink = groups[i];
				break;
			}
		}

		//If there is no train to link, fail silently.
		if(trainToLink == null) return;

		//Get the last minecart in the train.
		MinecartMember<?> m1 = trainToLink.get(trainToLink.size() - 1);

		//Small block map to determine location placement.
		Discoverable disco = new SmallBlockMap(m1.getBlock());

		//Get the OPPOSITE of the train's facing direction, that way we can get the location we need (behind the train)
		BlockFace bf = m1.getDirection().getOppositeFace();

		//Switch to determine the location of the new minecart.
		Location toSpawn = null;
		switch(bf) {
		case EAST: toSpawn = disco.getEast().getLocation(); break;
		case NORTH: toSpawn = disco.getNorth().getLocation(); break;
		case SOUTH: toSpawn = disco.getSouth().getLocation(); break;
		case WEST: toSpawn = disco.getWest().getLocation(); break;
		default: break;
		}

		//This is just setting the location of the new minecart. It is required to be an Array by TrainCarts.
		Location[] loc = {toSpawn};

		MinecartGroup mg = MinecartGroup.spawn(loc, cart);

		//Set the pseudo-train's properties to the train we want to link it to.
		mg.setProperties(trainToLink.getProperties());

		//Get the minecart (should be the ONLY one at this point) of this "pseudo-train"
		MinecartMember<?> m2 = mg.get(0);

		//Link the new minecart to the train.
		MinecartGroup.link(m1, m2);

		//Add the owner and cart to the ownerList. Need to cast in order to get proper functionality....
		
		
		if(m2.getClass().isInstance(new MinecartMemberChest())) {
			//If they are not there, initialize a list and put the first cart in there.
			if(!ownerList.containsKey(owner)) {
				List<MinecartMemberChest>	addThis = new ArrayList<MinecartMemberChest>();
				addThis.add((MinecartMemberChest) m2);
				ownerList.put(owner, addThis);
			}
			//Otherwise, just add this cart to their list.
			else {
				ownerList.get(owner).add((MinecartMemberChest) m2);
			}
		} else {
			//If they are not there, initialize a list and put the first cart in there.
			if(!ownerRideable.containsKey(owner)) {
				List<MinecartMemberRideable> addThis = new ArrayList<MinecartMemberRideable>();
				addThis.add((MinecartMemberRideable) m2);
				ownerRideable.put(owner, addThis);
			}
			//Otherwise, just add this cart to their list.
			else {
				ownerRideable.get(owner).add((MinecartMemberRideable) m2);
			}
		}

	}

	/**
	 * Removes the specified minecart from the train, by getting the inventory the player owns. 
	 * 
	 * @param trainName
	 * @param owner
	 */
	public static void removeCart(String trainName, Player owner, EntityType cartType) {

		//Iterate through the groups already created, and find the one requested.
		MinecartGroup train = null;
		MinecartGroup[] groups = MinecartGroup.getGroups();
		for(int i = 0; i < groups.length; i++) {
			if(groups[i].getProperties().getTrainName().equalsIgnoreCase(trainName)) {
				train = groups[i];
				break;
			}
		}

		//Determine what we want to delete
		List<?> listOf = null;
		if(cartType.equals(EntityType.MINECART_CHEST)) {
			listOf = ownerList.get(owner);
			MinecartMemberChest toRemove = null;
			if(listOf != null) {
				int i = listOf.size() - 1;
				if(i == -1) return;
				toRemove = (MinecartMemberChest) listOf.get(i);
			}
			if(toRemove == null) return;

			for(MinecartMember<?> cart : train) {
				try {
					MinecartMemberChest chestCart = (MinecartMemberChest) cart;

					if(chestCart == toRemove) {
						ownerList.get(owner).remove(chestCart);
						train.remove(cart);
						cart.getGroup().destroy();
						break;
					}
				} catch (ClassCastException e) {}
			}
		}
		else if(cartType.equals(EntityType.MINECART)) {
			listOf = ownerRideable.get(owner);
			MinecartMemberRideable toRemove = null;
			if(listOf != null) {
				int i = listOf.size() - 1;
				if(i == -1) return;
				toRemove = (MinecartMemberRideable) listOf.get(i);
			}
			if(toRemove == null) return;

			for(MinecartMember<?> cart : train) {
				try {
					MinecartMemberRideable rideCart = (MinecartMemberRideable) cart;

					if(rideCart == toRemove) {
						ownerRideable.get(owner).remove(rideCart);
						train.remove(cart);
						cart.getGroup().destroy();
						break;
					}
				} catch (ClassCastException e) {}
			}
		}
	}

	/**
	 * Finds the owner of the inventory through a backwards process.
	 * 
	 * @param inv The inventory in question.
	 * @return The owner of the inventory.
	 */
	public static Player findOwnerByInv(Inventory inv) {
		for(Player p : ownerList.keySet()) {
			if(ownerList.get(p).contains(inv)) {
				return p;
			}
		}
		return null;
	}

	public static List<MinecartMemberChest> getInventoryList(Player p) {
		return ownerList.get(p);
	}

	public static List<MinecartMemberRideable> getRideList(Player p) {
		return ownerRideable.get(p);
	}

	public static boolean hasInvList(Player p) {
		return ownerList.containsKey(p);
	}

	public static boolean hasRideList(Player p) {
		return ownerRideable.containsKey(p);
	}

	/**
	 * Determines what type of train it will be. Either public or private at the moment.
	 * 
	 * @param trainType The {@link TrainType} type of train it will be. either {@link TrainType#PUBLIC} or {@link TrainType#PRIVATE} for now.
	 * @param trainName The name of the train to be created.
	 * @param b The {@link Block} the train will start on.
	 * @param face the {@link BlockFace} the train will be facing.
	 * @param carts The type of carts in this train.
	 * @param speedLimit How fast the cart is capable of going.
	 */
	private static void makeTrain(TrainType trainType, String trainName, Block b, BlockFace face, List<EntityType> carts, double speedLimit) {
		switch(trainType) {
		case PRIVATE: break;
		case PUBLIC: makePublicTrain(trainName, b, face, carts, speedLimit);  break;
		case TOWN: makeTownTrain(trainName, b, face, carts);	break;
		case TRANSPORT: break;
		default: break;
		}
	}
}
