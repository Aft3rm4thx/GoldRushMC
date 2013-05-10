package com.goldrushmc.bukkit.train;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.goldrushmc.bukkit.train.signs.ISignLogic;
import com.goldrushmc.bukkit.train.signs.SignType;

public abstract class TrainStationClass {

	public ISignLogic sl;
	public Map<Sign, SignType> signs;
	public BlockFace direction;
	public volatile List<Player> occupants;
	public final Map<CardinalMarker, Location> corners;
	public final Set<Location> perimeter;
	public final World world;
	public final List<Chunk> chunk;

	public TrainStationClass(Map<CardinalMarker, Location> corners) {
		this.corners = corners;
		World world = null;
		List<Chunk> chunk = new ArrayList<Chunk>();
		for(Location loc : corners.values()) {
			if(world == null) world = loc.getWorld();
			if(!chunk.contains(loc.getChunk()))	chunk.add(loc.getChunk());
		}
		this.world = world;
		this.chunk = chunk;
		this.perimeter = generatePerimeter();
	}

	public void initDepartSchedule() {

	}

	public void initSigns() {
	}

	public Set<Location> generatePerimeter() {
		Location northEast = corners.get(CardinalMarker.NORTH_EAST_CORNER),
		northWest = corners.get(CardinalMarker.NORTH_WEST_CORNER),
		southEast = corners.get(CardinalMarker.SOUTH_EAST_CORNER),
		southWest = corners.get(CardinalMarker.SOUTH_WEST_CORNER);
		
		
		
		return null;
	}
}
