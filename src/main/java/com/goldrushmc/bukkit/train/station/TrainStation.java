package com.goldrushmc.bukkit.train.station;

import java.util.ArrayList;
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
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.goldrushmc.bukkit.db.TrainScheduleTbl;
import com.goldrushmc.bukkit.db.TrainTbl;
import com.goldrushmc.bukkit.defaults.DBAccess;
import com.goldrushmc.bukkit.defaults.DBTrainsAccessible;
import com.goldrushmc.bukkit.train.CardinalMarker;
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
	protected transient String departingTrain;
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
		Bukkit.getLogger().info("Creating a perimeter...");
		this.perimeter = generatePerimeter();
		this.surfaceBlocks = generateSurface();
		this.area = getFullArea(this.surfaceBlocks);
		this.trainArea = getTrainArea(this.surfaceBlocks);
		this.rails = findRails();
		this.stopBlock = this.findStopBlock(defaultStop);
		
		Bukkit.getLogger().info("Finding Signs...");
		this.signs = generateSignLogic();
		Sign dir = this.signs.getSign(SignType.TRAIN_STATION_DIRECTION);
		BlockFace tempDir = BlockFace.NORTH;
		if(dir != null) {
			if(dir.getLines().length == 3) {
				if(BlockFace.valueOf(dir.getLine(3)) != null) {
					tempDir = BlockFace.valueOf(dir.getLine(3));	
				}
			}
		}
		this.direction = tempDir;
		Bukkit.getLogger().info("There are " + signs.getSigns().size() + " signs in the station.");
		findWorkers();
		Bukkit.getLogger().info("Amount of Workers: " + getWorkers().size());
		findPlayers();
		Bukkit.getLogger().info("Amount of Visitors: " + getVisitors().size());
		
		Bukkit.getLogger().info("Adding to listener and listing...");
		//Add to the list of stations! IMPORTANT
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
		Bukkit.getLogger().info("Creating a perimeter...");
		this.perimeter = generatePerimeter();
		this.surfaceBlocks = generateSurface();
		this.area = getFullArea(this.surfaceBlocks);
		this.trainArea = getTrainArea(this.surfaceBlocks);
		this.rails = findRails();
		this.stopBlock = this.findStopBlock(stopMat);
		
		Bukkit.getLogger().info("Finding Signs...");
		this.signs = generateSignLogic();
		if(BlockFace.valueOf(this.signs.getSign(SignType.TRAIN_STATION_DIRECTION).getLine(3)) != null) {
			this.direction = BlockFace.valueOf(this.signs.getSign(SignType.TRAIN_STATION_DIRECTION).getLine(3));	
		}
		else {
			//Default to north.
			this.direction = BlockFace.NORTH;
		}
		Bukkit.getLogger().info("There are " + signs.getSigns().size() + " signs in the station.");
		findWorkers();
		Bukkit.getLogger().info("Amount of Workers: " + getWorkers().size());
		findPlayers();
		Bukkit.getLogger().info("Amount of Visitors: " + getVisitors().size());
		
		Bukkit.getLogger().info("Adding to listener and listing...");
		//Add to the list of stations! IMPORTANT
		trainStations.add(this);
		TrainStationLis.addStation(this);
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
	}
	
	public List<Block> getSurface() {
		return this.surfaceBlocks;
	}

	public void findPlayers() {
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
	 * Finds the next train queued for departure. Its front cart (furnace cart) will be just above the stop block.
	 * 
	 * @return
	 */
	public MinecartGroup findNextDeparture() {
		for(MinecartGroup mg : this.trains) {
			if(mg.get(0).getGroundBlock().equals(this.stopBlock)) {
				return mg;
			}
		}
		return null;
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
				return b;
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
