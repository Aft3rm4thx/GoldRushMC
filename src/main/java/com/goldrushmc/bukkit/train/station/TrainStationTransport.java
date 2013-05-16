package com.goldrushmc.bukkit.train.station;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberChest;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberFurnace;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.goldrushmc.bukkit.train.CardinalMarker;

public class TrainStationTransport extends TrainStation {



	public TrainStationTransport(JavaPlugin plugin, String stationName,	Map<CardinalMarker, Location> corners, World world) throws TooLowException {
		super(plugin, stationName, corners, world);
		if(this.rails != null) {
			createTransport();	
		}
	}
	public TrainStationTransport(JavaPlugin plugin, String stationName,	Map<CardinalMarker, Location> corners, World world, Material stopMat) throws TooLowException {
		super(plugin, stationName, corners, world, stopMat);
		if(this.rails != null) {
			createTransport();	
		}
	}

	public void createTransport() {

		if(this.trains == null) this.trains = new ArrayList<MinecartGroup>();
		int trainNum = this.trains.size() + 1;

		List<EntityType> carts = new ArrayList<EntityType>();
		carts.add(EntityType.MINECART_FURNACE);
		carts.add(EntityType.MINECART_CHEST);
		//Should make the furnace spawn right on top of the stop block.
		Block start = this.stopBlock.getRelative(this.direction.getOppositeFace());

		MinecartGroup train = MinecartGroup.spawn(start, this.direction, carts);
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
		//TODO TEMPORARY. SET TO FALSE WHEN TESTING IS DONE
		tp.setManualMovementAllowed(true);
		tp.setKeepChunksLoaded(true);
		tp.setPickup(false);
		train.setProperties(tp);

		this.addTrain(train);
		this.findNextDeparture();
		this.changeSignLogic(train.getProperties().getTrainName());
	}
}
