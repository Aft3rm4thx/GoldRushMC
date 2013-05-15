package com.goldrushmc.bukkit.train.station;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.goldrushmc.bukkit.train.CardinalMarker;

public class TrainStationTransport extends TrainStation {



	public TrainStationTransport(JavaPlugin plugin, String stationName,	Map<CardinalMarker, Location> corners, World world) throws TooLowException {
		super(plugin, stationName, corners, world);
		createTransport();
	}
	public TrainStationTransport(JavaPlugin plugin, String stationName,	Map<CardinalMarker, Location> corners, World world, Material stopMat) throws TooLowException {
		super(plugin, stationName, corners, world, stopMat);
		createTransport();
	}

	public void createTransport() {
		
		if(this.trains == null) this.trains = new ArrayList<MinecartGroup>();
		int trainNum = this.trains.size() + 1;
		
		List<EntityType> carts = new ArrayList<EntityType>();
		carts.add(EntityType.MINECART_CHEST);
		carts.add(EntityType.MINECART_FURNACE);
		
		MinecartGroup train = MinecartGroup.spawn(this.stopBlock, this.direction, carts);
		
		TrainProperties tp = train.getProperties();
		tp.setName(stationName + "_" + trainNum);
		tp.setColliding(false);
		tp.setSpeedLimit(0.4);
		tp.setPublic(false);
		tp.setManualMovementAllowed(false);
		tp.setKeepChunksLoaded(true);
		tp.setPickup(false);
		train.setProperties(tp);
	}
}
