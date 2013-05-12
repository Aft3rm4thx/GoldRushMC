package com.goldrushmc.bukkit.train.station;

import java.util.ArrayList;
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
import com.goldrushmc.bukkit.train.CardinalMarker;

public class TrainStationTransport extends TrainStation {



	public TrainStationTransport(JavaPlugin plugin, String stationName,	Map<CardinalMarker, Location> corners) {
		super(plugin, stationName, corners);
		// TODO Auto-generated constructor stub
	}

	public void createTransport() {
		
		if(this.trains == null) this.trains = new ArrayList<MinecartGroup>();
		int trainNum = this.trains.size() + 1;
		Block starting = getStartingRail();
		
		List<EntityType> carts = new ArrayList<EntityType>();
		carts.add(EntityType.MINECART_CHEST);
		carts.add(EntityType.MINECART_FURNACE);
		
		MinecartGroup train = MinecartGroup.spawn(starting, this.direction, carts);
		
		TrainProperties tp = train.getProperties();
		tp.setName(stationName + "_" + trainNum);
		tp.setColliding(false);
		tp.setSpeedLimit(0.4);
		tp.setPublic(false);
		tp.setManualMovementAllowed(false);
		train.setProperties(tp);
	}
	
	/**
	 * Starting rails should be denoted by an Iron Block
	 * 
	 * @return the Rail block
	 */
	public Block getStartingRail() {
		for(Block rail : this.rails) {
			Block toCheck = rail.getRelative(BlockFace.DOWN);
			if(toCheck.getType().equals(Material.IRON_BLOCK)) {
				return rail;
			}
		}
		return null;
	}
}
