package com.goldrushmc.bukkit.train.station;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberChest;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberFurnace;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.goldrushmc.bukkit.db.TrainScheduleTbl;
import com.goldrushmc.bukkit.db.TrainStationLocationTbl;
import com.goldrushmc.bukkit.db.TrainStationTbl;
import com.goldrushmc.bukkit.db.TrainTbl;
import com.goldrushmc.bukkit.defaults.DBAccess;
import com.goldrushmc.bukkit.defaults.DBTrainsAccessible;
import com.goldrushmc.bukkit.train.CardinalMarker;
import com.goldrushmc.bukkit.train.SmallBlockMap;
import com.goldrushmc.bukkit.train.listeners.TrainStationLis;
import com.goldrushmc.bukkit.train.signs.ISignLogic;
import com.goldrushmc.bukkit.train.signs.SignLogic;
import com.goldrushmc.bukkit.train.signs.SignType;
import com.goldrushmc.bukkit.train.util.TrainTools;

/**
 * Will control the functionality behind train stations.
 *
 * @author Diremonsoon 
 * 
 */
public abstract class TrainStation {

	//For tracking of the stations.
	protected static List<TrainStation> trainStations = new ArrayList<TrainStation>();
	public static final Material defaultStop = Material.BEDROCK;
	public static DBTrainsAccessible db;

	protected String stationName;
	protected volatile MinecartGroup departingTrain;
	protected final ISignLogic signs;
	protected final BlockFace direction;
	protected volatile List<Player> visitors = new ArrayList<Player>();
	protected List<HumanEntity> workers = new ArrayList<HumanEntity>();
	protected Map<CardinalMarker, Location> corners;
	protected final List<Block> perimeter;
	protected final List<Block> surfaceBlocks;
	protected final List<Block> area;
	protected final List<Block> trainArea;
	protected final World world;
	protected volatile List<MinecartGroup> trains;
	protected final List<Block> rails;
	protected final List<Block> stopBlocks;

	/**
	 * We require the JavaPlugin because this class must be able to access the database.
	 * This is the standard constructor, with the default stop block material..
	 * 
	 * @param plugin
	 * @param stationName
	 * @param corners
	 * @param world
	 * @throws TooLowException
	 */
	public TrainStation(final JavaPlugin plugin, final String stationName, final Map<CardinalMarker, Location> corners, final World world) throws TooLowException { 
		if(db == null) db = new DBAccess(plugin);
		this.stationName = stationName;
		this.corners = corners;
		this.world = world;
		this.perimeter = generatePerimeter();
		this.surfaceBlocks = generateSurface();
		this.area = getFullArea(this.surfaceBlocks);
		this.trainArea = getTrainArea(this.surfaceBlocks);
		this.rails = findRails();
		this.stopBlocks = this.findStopBlocks(defaultStop);
		this.signs = generateSignLogic();
		Sign dir = this.signs.getSign(SignType.TRAIN_STATION_DIRECTION);
		BlockFace tempDir = null;
		if(dir != null) {
			tempDir = TrainTools.getDirection(dir.getLine(2));
		}
		if(tempDir != null) {
			this.direction = tempDir;	
		}
		else {
			this.direction = BlockFace.SELF;
		}
		findWorkers();
		//Add to the list of stations for both the listener and static class instance! IMPORTANT
		trainStations.add(this);
		TrainStationLis.addStation(this);
	}

	/**
	 * We require the JavaPlugin because this class must be able to access the database.
	 * This is the standard constructor with a custom stop block material.
	 * 
	 * @param plugin
	 * @param stationName
	 * @param corners
	 * @param world
	 * @param stopMat
	 * @throws TooLowException
	 */
	public TrainStation(final JavaPlugin plugin, final String stationName, final Map<CardinalMarker, Location> corners, final World world, Material stopMat) throws TooLowException { 
		if(db == null) db = new DBAccess(plugin);
		this.stationName = stationName;
		this.corners = corners;
		this.world = world;
		this.perimeter = generatePerimeter();
		this.surfaceBlocks = generateSurface();
		this.area = getFullArea(this.surfaceBlocks);
		this.trainArea = getTrainArea(this.surfaceBlocks);
		this.rails = findRails();
		this.stopBlocks = this.findStopBlocks(stopMat);
		this.signs = generateSignLogic();
		Sign dir = this.signs.getSign(SignType.TRAIN_STATION_DIRECTION);
		BlockFace tempDir = null;
		if(dir != null) {
			tempDir = TrainTools.getDirection(dir.getLine(2));
		}
		if(tempDir != null) {
			this.direction = tempDir;	
		}
		else {
			this.direction = BlockFace.SELF;
		}
		findWorkers();

		//Add to the list of stations for both the listener and static class instance! IMPORTANT
		trainStations.add(this);
		TrainStationLis.addStation(this);
	}
	
	public abstract void createTransport();

	/**
	 * Adds the train station to the database, in case of a server wide crash.
	 */
	public void addToDB() {
		TrainStationTbl station = new TrainStationTbl();
		station.setStationName(stationName);
		Set<TrainStationLocationTbl> corners = new HashSet<TrainStationLocationTbl>();
		for(int i = 0; i < 4; i++) {
			TrainStationLocationTbl corner = new TrainStationLocationTbl();
			CardinalMarker cm = null;
			//Iterate through corners.
			switch(i) {
			case 0: cm = CardinalMarker.NORTH_EAST_CORNER; break;
			case 1: cm = CardinalMarker.NORTH_WEST_CORNER; break;
			case 2: cm = CardinalMarker.SOUTH_EAST_CORNER; break;
			case 3: cm = CardinalMarker.SOUTH_WEST_CORNER; break;
			}
			//Set corner
			switch(cm) {
			case NORTH_EAST_CORNER: corner.setCorner("North_East"); break;
			case NORTH_WEST_CORNER:	corner.setCorner("North_West"); break;
			case SOUTH_EAST_CORNER:	corner.setCorner("South_East"); break;
			case SOUTH_WEST_CORNER:	corner.setCorner("South_West"); break;
			}
			Location loc = this.corners.get(cm);
			corner.setStation(station);
			corner.setX(loc.getBlockX());
			corner.setY(loc.getBlockY());
			corner.setZ(loc.getBlockZ());
			corners.add(corner);
		}
		db.getDB().save(corners);
		station.setCorners(corners);
		db.getDB().save(station);
	}


	public MinecartGroup[] getDepartingTrains() {
		long current = this.world.getTime();
		MinecartGroup[] toDepart = new MinecartGroup[trains.size()];
		for(MinecartGroup train : trains) {
			TrainTbl check = db.getTrain(train.getProperties().getTrainName());
			Set<TrainScheduleTbl> schedules = check.getSchedule();
			int i = 0;
			for(TrainScheduleTbl schedule : schedules) {
				if(schedule.isNext()) {
					long departure = schedule.getTimeToDepart();
					if(departure == current) {
						toDepart[i] = train;	
					}						
				}
			}
		}
		return toDepart;
	}
	
	/**
	 * The standard train creation method. This is an optional way to create a train with one furnace and one chest cart.
	 * 
	 * @param stop The {@link Block} to spawn the train on.
	 */
	public void createBuyableTrain(Block stop) {

		if(this.trains == null) this.trains = new ArrayList<MinecartGroup>();
		int trainNum = this.trains.size() + 1;

		List<EntityType> carts = new ArrayList<EntityType>();
		carts.add(EntityType.MINECART_CHEST);
		carts.add(EntityType.MINECART_FURNACE);
		//Should make the furnace spawn right on top of the stop block.
		MinecartGroup train = MinecartGroup.spawn(stop, this.direction.getOppositeFace(), carts);
		for(MinecartMember<?> mm : train) {
			if(mm instanceof MinecartMemberChest) {
//				ItemStack coal = new ItemStack(Material.COAL, 64);
//				MinecartMemberChest coalChest = (MinecartMemberChest) mm;
				//			coalChest.getEntity().getInventory().addItem(new ItemStack[]{coal, coal, coal, coal, coal, coal});			
			}
			if(mm instanceof MinecartMemberFurnace) {
			}
		}

		TrainProperties tp = train.getProperties();
		tp.setName(stationName + "_" + trainNum);
		tp.setColliding(false);
		tp.setSpeedLimit(0.4);
		tp.setPublic(false);
		tp.setManualMovementAllowed(false);
		tp.setKeepChunksLoaded(true);
		tp.setPickup(false);
		train.setProperties(tp);

		this.addTrain(train);
		this.findNextDeparture();
		this.changeSignLogic(train.getProperties().getTrainName());
	}


	/**
	 * Creates a perimeter around the train station.
	 * 
	 * East = X + 1
	 * West = X - 1
	 * South = Z + 1
	 * North = Z - 1
	 * @return
	 */
	protected List<Block> generatePerimeter() {

		//Get all of the locations for each corner.
		Location northEast = corners.get(CardinalMarker.NORTH_EAST_CORNER),
				northWest = corners.get(CardinalMarker.NORTH_WEST_CORNER),
				southEast = corners.get(CardinalMarker.SOUTH_EAST_CORNER),
				southWest = corners.get(CardinalMarker.SOUTH_WEST_CORNER);

		//Iterate through each line of locations, and add them to the perimeter. This should make a rectangle.
		List<Block> perimeter = new ArrayList<Block>();
		for(int i = northWest.getBlockZ() + 1; i < southWest.getBlockZ(); i++) {
			Location loc = new Location(this.world, northWest.getBlockX(), northWest.getBlockY(), i);
			perimeter.add(loc.getBlock());
		}
		for(int i = southWest.getBlockX() + 1; i < southEast.getBlockX(); i++) {
			Location loc = new Location(this.world, i, southWest.getBlockY(), southWest.getBlockZ());
			perimeter.add(loc.getBlock());
		}
		for(int i = southEast.getBlockZ() - 1; i > northEast.getBlockZ(); i--) {
			Location loc = new Location(this.world, southEast.getBlockX(), southEast.getBlockY(), i);
			perimeter.add(loc.getBlock());
		}
		for(int i = northEast.getBlockX() - 1; i > northWest.getBlockX(); i--) {
			Location loc = new Location(this.world, i, northEast.getBlockY(), northEast.getBlockZ());
			perimeter.add(loc.getBlock());
		}

		return perimeter;
	}

	/**
	 * Gets the surface of the {@link TrainStation}. (FLAT)
	 * 
	 * @return The {@code List}<{@link Block}> that contains all surface blocks.
	 * @throws TooLowException 
	 */
	public List<Block> generateSurface() throws TooLowException {

		List<Block> blocks = new ArrayList<Block>();

		Location northEast = this.corners.get(CardinalMarker.NORTH_EAST_CORNER),
				//		southEast = this.corners.get(CardinalMarker.SOUTH_EAST_CORNER),
				northWest = this.corners.get(CardinalMarker.NORTH_WEST_CORNER),
				southWest = this.corners.get(CardinalMarker.SOUTH_WEST_CORNER);

		if(northEast.getY() < 5)throw new TooLowException(northEast);
		else if(northWest.getY() < 5)throw new TooLowException(northWest);
		else if(southWest.getY() < 5) throw new TooLowException(southWest);

		for(int i = northWest.getBlockZ(); i <= southWest.getBlockZ(); i++) {
			Location start = new Location(this.world, northWest.getX(), northWest.getY(), i);
			blocks.add(start.getBlock());
			for(int j = northWest.getBlockX(); j <= northEast.getBlockX(); j++) {
				Location increment = new Location(this.world, j, start.getY(), start.getZ());
				blocks.add(increment.getBlock());
			}
		}
		return blocks;
	}

	public List<Block> getArea() {
		return this.area;
	}
	/**
	 * Gets all of the blocks of all Y coordinates for each X,Z position.
	 * @return
	 */
	protected List<Block> getFullArea(List<Block> blocks) {
		List<Block> full = new ArrayList<Block>();
		for(Block b : blocks) {
			//Gets 5 blocks below the surface of the train. This can be changed later.
			//Gets 50 blocks above the b value. This is the max wall height we want anyways.
			for(int i = (b.getY() - 5); i < (b.getY() + 50); i++) {
				full.add(world.getBlockAt(b.getX(), i, b.getZ()));
			}
		}
		return full;
	}

	protected List<Block> getTrainArea(List<Block> blocks) {
		List<Block> full = new ArrayList<Block>();
		for(Block b : blocks) {
			//Gets 2 blocks below, just in case the train dips.
			//Gets 8 blocks above, in case of incline.
			for(int i = (b.getY() - 2); i < (b.getY() + 8); i++) {
				full.add(world.getBlockAt(b.getX(), i, b.getZ()));
			}
		}
		return full;
	}

	protected void findWorkers() {
		//TODO
	}

	public List<Block> getSurface() {
		return this.surfaceBlocks;
	}

	public void addVisitor(Player visitor) {
		this.visitors.add(visitor);
	}

	public void removeVisitor(Player visitor) {
		this.visitors.remove(visitor);
	}

	protected ISignLogic generateSignLogic() {
		return new SignLogic(this.area);
	}

	public void findStillTrains() {
		MinecartGroup[] trains = MinecartGroup.getGroups();
		for(int i = 0; i < trains.length; i++) {
			if(!trains[i].isMoving()) {
				//At least the front of the train must be WITHIN the station.
				Block ground = trains[i].get(0).getGroundBlock();
				for(Block b : this.trainArea) {
					//If the train is within the station grounds, accept it.
					if(b.equals(ground)) {
						this.trains.add(trains[i]);
					}
				}
			}
		}
	}
	
	/**
	 * Adds a cart to the train scheduled for departure
	 * <p>
	 * DEFAULT IMPLEMENTATION.
	 * 
	 * @param type
	 * @param owner
	 */
	public void addCart(EntityType type, Player owner) {
		
		//Check if the departing train does not exist. this may happen, for a brief period, between the departing train leaving and the arriving train arriving.
		if(this.departingTrain == null) { owner.sendMessage("There are currently no trains to buy carts for."); return; }
		
		//Get train name, in case the departing train forgets!
		String trainName = this.departingTrain.getProperties().getTrainName();
//		int trainSize = this.departingTrain.size() - 1;
		Block toSpawn = null;
		BlockFace dirToLook = this.direction.getOppositeFace();
		//Make sure that we are on the right end of the train, to spawn.
		MinecartMember<?> toJoinTo = null;
		if(this.departingTrain.get(0) instanceof MinecartMemberFurnace) {
			toJoinTo = this.departingTrain.get(this.departingTrain.size() - 1);
		}
		else {
			toJoinTo = this.departingTrain.get(0);
		}
		//Set the block map to the correct minecart member's block.
		SmallBlockMap sbm = new SmallBlockMap(toJoinTo.getBlock());
		if(sbm.isRail(sbm.getBlockAt(dirToLook))) {
				toSpawn = sbm.getBlockAt(dirToLook);
		}
		//If there is no room, do not spawn additional carts.
		if(toSpawn == null) { owner.sendMessage("There is not enough room to spawn additonal carts."); return; }
		
		Location fixed = toSpawn.getLocation();
		switch(this.direction) {
		case NORTH: fixed.setZ(fixed.getZ() + 0.75); break;
		case SOUTH: fixed.setZ(fixed.getZ() - 0.75); break;
		case EAST: fixed.setX(fixed.getX() - 0.75); break;
		case WEST: fixed.setX(fixed.getX() + 0.75); break;
		default: break;
		}
		
		//Spawn the cart and join it to the train.
		MinecartMember<?> toJoin = MinecartMemberStore.spawn(fixed, type);
		//Set the new minecarts group properties to the same as the departing train.
		toJoin.getGroup().setProperties(this.departingTrain.getProperties());
		//Set the owner (hoping this works) to the person who bought the cart.
		toJoin.getProperties().setOwner(owner.getName().toLowerCase());
		MinecartGroup.link(toJoin, toJoinTo);
		//Re-set the name of the departing train, after linking.
		this.departingTrain.getProperties().setName(trainName);
		
		//Send a message saying it has been done.
		if(type.equals(EntityType.MINECART)) owner.sendMessage("You bought a passenger cart");
		else if(type.equals(EntityType.MINECART_CHEST)) owner.sendMessage("You bought a storage cart");
	}
	
	
	/**
	 * Removes a cart from the departing train.
	 * 
	 * @param type
	 * @param remover
	 */
	public void removeCart(EntityType type, Player remover) {
		if(this.departingTrain == null) { remover.sendMessage("There is no train in the station."); return; }
		MinecartGroup train = this.departingTrain;
		for(MinecartMember<?> cart : train) {
			CartProperties cartP = cart.getProperties();
			if(cartP.getOwners().contains(remover.getName().toLowerCase())) {
				train.removeSilent(cart); 
				cart.getGroup().destroy();
				return;
			}
		}
		remover.sendMessage("You have no train carts to remove.");
	}

	/**
	 * Finds the next train queued for departure. Its front cart (furnace cart) will be just above the stop block.
	 * 
	 * @return
	 */
	public abstract MinecartGroup findNextDeparture();
	
	public MinecartGroup getDepartingTrain() {
		return departingTrain;
	}
	
	public void setDepartingTrain(MinecartGroup train) {
		this.departingTrain = train;
	}

	public abstract boolean pushQueue();
	/**
	 * Changes the signs to reflect the buying and selling of carts for the specified train.
	 * 
	 * @param trainName
	 */
	public abstract void changeSignLogic(String trainName);

	/**
	 * Gets the block that trains will stop on, specified by a specific material.
	 * The material must be a unique one, <u>only used once</u> in the whole station.
	 * 
	 * @param m
	 * @return
	 */
	public abstract List<Block> findStopBlocks(Material m);

	public List<Block> getStopBlock() {
		return stopBlocks;
	}

	/**
	 * This finds the rails which are within the train station.
	 * <p>
	 * This DOES NOT SHOW PATHWAYS. That is to be determined for each subclass.
	 * 
	 * @return
	 */
	public List<Block> findRails() {
		List<Block> rails = new ArrayList<Block>();
		for(Block b : this.area) {
			if(TrainTools.isRail(b)) {
				rails.add(b);
			}
		}
		return rails;
	}

	//TODO Various Getters and Setters for the Train Station class.

	public void addTrain(MinecartGroup train) {
		this.trains.add(train);
	}
	public void removeTrain(MinecartGroup train) {
		this.trains.remove(train);
	}
	/**
	 * Gets all of the existing train stations.
	 * 
	 * @return
	 */
	public static List<TrainStation> getTrainStations() {return trainStations;}

	/**
	 * Could potentially return null, if no train stations exist.
	 * 
	 * @return
	 */
	public static DBTrainsAccessible getDb() {	return db;}

	public String getStationName() {return stationName;}

	public void setStationName(String stationName) {this.stationName = stationName;}

	public ISignLogic getSigns() {return signs;}

	public BlockFace getDirection() { return direction;}

	public List<Player> getVisitors() {	return visitors;}	

	public List<HumanEntity> getWorkers() {	return workers;}

	public Map<CardinalMarker, Location> getCorners() {	return corners;}

	public List<Block> getPerimeter() {return perimeter;}

	public List<MinecartGroup> getTrains() {return trains;}

	public List<Block> getRails() {return rails;}

	public World getWorld() {return world;}
}
