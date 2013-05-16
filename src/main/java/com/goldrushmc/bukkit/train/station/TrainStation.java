package com.goldrushmc.bukkit.train.station;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
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
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberFurnace;
import com.bergerkiller.bukkit.tc.events.GroupCreateEvent;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
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
	public static List<TrainStation> trainStations = new ArrayList<TrainStation>();
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
	protected final Block stopBlock;

	/**
	 * We require the JavaPlugin because this class must be able to access the database.
	 * This is the standard constructor, with the default stop block.
	 * 
	 * @param plugin
	 * @param stationName
	 * @param corners
	 * @param world
	 * @throws TooLowException
	 */
	public TrainStation(final JavaPlugin plugin, String stationName, Map<CardinalMarker, Location> corners, World world) throws TooLowException { 
		if(db == null) db = new DBAccess(plugin);
		this.stationName = stationName;
		this.corners = corners;
		List<Chunk> chunk = new ArrayList<Chunk>();
		for(Location loc : corners.values()) {
			if(world == null) world = loc.getWorld();
			if(!chunk.contains(loc.getChunk()))	chunk.add(loc.getChunk());
		}
		this.world = world;
		this.perimeter = generatePerimeter();
		this.surfaceBlocks = generateSurface();
		this.area = getFullArea(this.surfaceBlocks);
		this.trainArea = getTrainArea(this.surfaceBlocks);
		this.rails = findRails();
		this.stopBlock = this.findStopBlock(defaultStop);
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
	 * This is the standard constructor with a custom stop block.
	 * 
	 * @param plugin
	 * @param stationName
	 * @param corners
	 * @param world
	 * @param stopMat
	 * @throws TooLowException
	 */
	public TrainStation(final JavaPlugin plugin, String stationName, Map<CardinalMarker, Location> corners, World world, Material stopMat) throws TooLowException { 
		if(db == null) db = new DBAccess(plugin);
		this.stationName = stationName;
		this.corners = corners;
		List<Chunk> chunk = new ArrayList<Chunk>();
		for(Location loc : corners.values()) {
			if(world == null) world = loc.getWorld();
			if(!chunk.contains(loc.getChunk()))	chunk.add(loc.getChunk());
		}
		this.world = world;
		this.perimeter = generatePerimeter();
		this.surfaceBlocks = generateSurface();
		this.area = getFullArea(this.surfaceBlocks);
		this.trainArea = getTrainArea(this.surfaceBlocks);
		this.rails = findRails();
		this.stopBlock = this.findStopBlock(stopMat);
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


	public void createSidewalk() {
		for(Block b : this.perimeter) {
			b.setType(Material.STEP);
		}
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
	 * 
	 * @param type
	 * @param owner
	 */
	public void addCart(EntityType type, Player owner) {
		String trainName = this.departingTrain.getProperties().getTrainName();
		if(this.departingTrain == null) { owner.sendMessage("There are currently no trains to buy carts for."); return; }
		int trainSize = this.departingTrain.size();
		Block toSpawn = null;
		BlockFace dirToLook = this.direction.getOppositeFace();
		SmallBlockMap sbm = new SmallBlockMap(this.departingTrain.get(this.departingTrain.size() - 1).getBlock());
		for(int i = 0; i <= trainSize; i++) {
			sbm = new SmallBlockMap(sbm.getBlockAt(dirToLook));
			if(!sbm.nextIsRail(dirToLook)) {
				if(i < trainSize) {owner.sendMessage("There is no room in the station to buy a cart!"); return; }
			}
			if(i == trainSize) toSpawn = sbm.getBlockAt(dirToLook);
		}
		
		//If there is no room, do not spawn additional carts.
		if(toSpawn == null) { owner.sendMessage("There is not enough room to spawn additonal carts."); return; }
		
		//Spawn the cart and join it to the train.
		MinecartMember<?> toJoin = MinecartMemberStore.spawn(toSpawn.getLocation(), type);
		toJoin.getProperties().setOwner(owner.getName().toLowerCase());
		this.departingTrain.add(toJoin);
		this.departingTrain.getProperties().setName(trainName);
		GroupCreateEvent.call(this.departingTrain);
		
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
	public MinecartGroup findNextDeparture() {
		for(MinecartGroup mg : this.trains) {
			Bukkit.getLogger().info("Train found for next departure: " + mg.getProperties().getTrainName());
			for(MinecartMember<?> mm : mg) {
				if(mm instanceof MinecartMemberFurnace) {
					if(mm.getBlock().equals(this.stopBlock)) {
						Bukkit.getLogger().info("Found the correct cart!");
						//Mark the next train name.
						return mg;
					}
				}
			}
		}
		return null;
	}
	
	public MinecartGroup getDepartingTrain() {
		return departingTrain;
	}
	
	public void setDepartingTrain(MinecartGroup train) {
		this.departingTrain = train;
	}

	public boolean pushQueue() {
		MinecartGroup mg = findNextDeparture();
		if(mg == null) return false;
		for(MinecartMember<?> mm : mg) {
			if(mm instanceof MinecartMemberFurnace) {
				MinecartMemberFurnace power = (MinecartMemberFurnace) mm;
				power.addFuelTicks(10);
				if(!power.getDirection().equals(this.direction)) {
					mg.reverse();
				}
			}
		}
		if(this.trains.isEmpty()) return true;
		for(MinecartGroup train : this.trains) {
			if(!train.equals(mg)) {
				train.setForwardForce(.4);
			}
		}
		return true;
	}

	/**
	 * Changes the signs to reflect the buying and selling of carts for the specified train.
	 * 
	 * @param trainName
	 */
	public void changeSignLogic(String trainName) {
		this.signs.updateTrain(trainName);
	}

	/**
	 * Gets the block that trains will stop on, specified by a specific material.
	 * The material must be a unique one, <u>only used once</u> in the whole station.
	 * 
	 * @param m
	 * @return
	 */
	public Block findStopBlock(Material m) {
		for(Block b : this.trainArea) {
			if(b.getType().equals(m)) {
				return b.getRelative(BlockFace.UP);
			}
		}
		return null;
	}

	public Block getStopBlock() {
		return stopBlock;
	}

	/**
	 * This finds the rails which are within the train station.
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
