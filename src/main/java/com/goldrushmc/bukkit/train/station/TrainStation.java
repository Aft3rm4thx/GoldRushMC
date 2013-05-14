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
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.goldrushmc.bukkit.db.TrainScheduleTbl;
import com.goldrushmc.bukkit.db.TrainTbl;
import com.goldrushmc.bukkit.defaults.DBTrainsAccessible;
import com.goldrushmc.bukkit.defaults.TrainDB;
import com.goldrushmc.bukkit.train.CardinalMarker;
import com.goldrushmc.bukkit.train.listeners.TrainStationListener;
import com.goldrushmc.bukkit.train.signs.ISignLogic;
import com.goldrushmc.bukkit.train.signs.SignLogic;

/**
 * Will control the functionality behind train stations.
 *
 * @author Diremonsoon 
 * 
 */
public abstract class TrainStation {

	//For tracking of the stations.
	public static List<TrainStation> trainStations = new ArrayList<TrainStation>();
	public static DBTrainsAccessible db;
	
	protected String stationName;
	protected List<ISignLogic> signList = new ArrayList<ISignLogic>();
	protected BlockFace direction;
	protected volatile List<Player> visitors = new ArrayList<Player>();
	protected List<HumanEntity> workers = new ArrayList<HumanEntity>();
	protected Map<CardinalMarker, Location> corners;
	protected final List<Block> perimeter;
	protected final List<Block> surfaceBlocks;
	protected final World world;
	protected final List<Chunk> chunks;
	protected List<MinecartGroup> trains;
	protected List<Block> rails;
	protected boolean isBidirectional;

	/**
	 * We require the JavaPlugin because this class must be able to access the database.
	 * 
	 * @param plugin
	 * @param stationName
	 * @param corners
	 */
	public TrainStation(final JavaPlugin plugin, String stationName, Map<CardinalMarker, Location> corners, World world) { 
		if(db == null) db = new TrainDB(plugin);
		this.stationName = stationName;
		this.corners = corners;
		List<Chunk> chunk = new ArrayList<Chunk>();
		for(Location loc : corners.values()) {
			if(world == null) world = loc.getWorld();
			if(!chunk.contains(loc.getChunk()))	chunk.add(loc.getChunk());
		}
		this.world = world;
		this.chunks = chunk;
		Bukkit.getLogger().info("Creating a perimeter...");
		this.perimeter = generatePerimeter();
		this.surfaceBlocks = generateSurface();
		Bukkit.getLogger().info("Finding Signs...");
		initSigns();
		Bukkit.getLogger().info("There are " + signList.size() + " chunks");
		Bukkit.getLogger().info("Finding workers...");
		findWorkers();
		Bukkit.getLogger().info("Finding players...");
		findPlayers();
		
		Bukkit.getLogger().info("Adding to listener and listing...");
		//Add to the list of stations! IMPORTANT
		trainStations.add(this);
		TrainStationListener.addStation(this);
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

	public void initSigns() {
		signList = new ArrayList<ISignLogic>();
		for(Chunk c : this.chunks) {
			signList.add(new SignLogic(c));
		}
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
	public List<Block> generatePerimeter() {

		//Get all of the locations for each corner.
		Location northEast = corners.get(CardinalMarker.NORTH_EAST_CORNER),
				northWest = corners.get(CardinalMarker.NORTH_WEST_CORNER),
				southEast = corners.get(CardinalMarker.SOUTH_EAST_CORNER),
				southWest = corners.get(CardinalMarker.SOUTH_WEST_CORNER);

		//Iterate through each line of locations, and add them to the perimeter. This should make a rectangle.
		//TODO make a cube, instead of a rectangle, if possible or necessary.
		List<Block> perimeter = new ArrayList<Block>();
		for(int i = northWest.getBlockZ() + 1; i < southWest.getBlockZ(); i++) {
			Location loc = new Location(this.world, northWest.getBlockX(), northWest.getBlockY(), i);
//			loc.getWorld().getBlockAt(loc).setType(Material.STONE);
			perimeter.add(loc.getBlock());
		}
		for(int i = southWest.getBlockX() + 1; i < southEast.getBlockX(); i++) {
			Location loc = new Location(this.world, i, southWest.getBlockY(), southWest.getBlockZ());
//			loc.getWorld().getBlockAt(loc).setType(Material.STONE);
			perimeter.add(loc.getBlock());
		}
		for(int i = southEast.getBlockZ() - 1; i > northEast.getBlockZ(); i--) {
			Location loc = new Location(this.world, southEast.getBlockX(), southEast.getBlockY(), i);
//			loc.getWorld().getBlockAt(loc).setType(Material.STONE);
			perimeter.add(loc.getBlock());
		}
		for(int i = northEast.getBlockX() - 1; i > northWest.getBlockX(); i--) {
			Location loc = new Location(this.world, i, northEast.getBlockY(), northEast.getBlockZ());
//			loc.getWorld().getBlockAt(loc).setType(Material.STONE);
			perimeter.add(loc.getBlock());
		}

		return perimeter;
	}
	
	/**
	 * Gets the surface of the {@link TrainStation}. (FLAT)
	 * 
	 * @return The {@code List}<{@link Block}> that contains all surface blocks.
	 */
	public List<Block> generateSurface() {
		
		List<Block> blocks = new ArrayList<Block>();
		
		Location northEast = this.corners.get(CardinalMarker.NORTH_EAST_CORNER),
//		southEast = this.corners.get(CardinalMarker.SOUTH_EAST_CORNER),
		northWest = this.corners.get(CardinalMarker.NORTH_WEST_CORNER),
		southWest = this.corners.get(CardinalMarker.SOUTH_WEST_CORNER);
		
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

	public void findWorkers() {
		this.workers = new ArrayList<HumanEntity>();
		for(Chunk c : this.chunks) {
			Entity[] workers = c.getEntities();
			for(int i = 0; i < workers.length; i++) {
				if(workers[i] instanceof HumanEntity) {
					if(!(workers[i] instanceof Player)) {
						this.workers.add((HumanEntity) workers[i]);
					}
				}
			}
		}
	}
	
	public List<Block> getSurface() {
		return this.surfaceBlocks;
	}

	public void findPlayers() {
		if(this.visitors == null) this.visitors = new ArrayList<Player>();
		for(Chunk c : this.chunks) {
			Entity[] visiting = c.getEntities();
			for(int i = 0; i < visiting.length; i++) {
				if(visiting[i] instanceof HumanEntity) {
					if(visiting[i] instanceof Player) {
						this.visitors.add((Player) visiting[i]);
					}
				}
			}
		}
	}

	public void addVisitor(Player visitor) {
		this.visitors.add(visitor);
	}
	
	public void removeVisitor(Player visitor) {
		this.visitors.remove(visitor);
	}

	public ISignLogic getLogicForChunk(Chunk c) {
		for(ISignLogic logic : signList) {
			if(logic.getChunk().equals(c)) {
				return logic;
			}
		}
		return null;
	}

	public void findStillTrains() {
		MinecartGroup[] trains = MinecartGroup.getGroups();
		for(int i = 0; i < trains.length; i++) {
			if(!trains[i].isMoving()) {
				Chunk trainChunk = trains[i].getProperties().getLocation().getChunk();
				for(Chunk c : this.chunks) {
					if(trainChunk.equals(c)) {
						this.trains.add(trains[i]);
					}
				}
			}
		}
	}

	/**
	 * This may not be safe... It attempts to find blocks, but not with a snapshot chunk.
	 */
	public void findRails() {
		if(this.rails == null) this.rails = new ArrayList<Block>();
		for(Chunk c : this.getChunks()) {
			for(int x = 0; x < 16; x++) {
				for(int z = 0; z < 16; z++) {
					for(int y = 0; y < 128; y++) {
						Block block = c.getBlock(x, y, z);
						Material type = block.getType();
						if(type.equals(Material.RAILS) || type.equals(Material.ACTIVATOR_RAIL)
								|| type.equals(Material.DETECTOR_RAIL) || type.equals(Material.POWERED_RAIL)) {
							this.rails.add(block);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Gets all of the existing train stations.
	 * 
	 * @return
	 */
	public static List<TrainStation> getTrainStations() {
		return trainStations;
	}

	public DBTrainsAccessible getDb() {
		return db;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public List<ISignLogic> getSignList() {
		return signList;
	}

	public void setSignList(List<ISignLogic> signList) {
		this.signList = signList;
	}

	public BlockFace getDirection() {
		return direction;
	}

	public void setDirection(BlockFace direction) {
		this.direction = direction;
	}

	public List<Player> getVisitors() {
		return visitors;
	}	

	public void setVisitors(List<Player> visitors) {
		this.visitors = visitors;
	}

	public List<HumanEntity> getWorkers() {
		return workers;
	}

	public void setWorkers(List<HumanEntity> workers) {
		this.workers = workers;
	}

	public Map<CardinalMarker, Location> getCorners() {
		return corners;
	}

//	/**
//	 * Sets new corners of the station. WILL re-generate the station's perimeter.
//	 * 
//	 * @param corners
//	 */
//	public void setCorners(Map<CardinalMarker, Location> corners) {
//		this.corners = corners;
//		this.perimeter = this.generatePerimeter();
//	}

	public List<Block> getPerimeter() {
		return perimeter;
	}

	public List<MinecartGroup> getTrains() {
		return trains;
	}

	public List<Block> getRails() {
		return rails;
	}

	public World getWorld() {
		return world;
	}

	public List<Chunk> getChunks() {
		return chunks;
	}
}
