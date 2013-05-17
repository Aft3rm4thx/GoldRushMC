package com.goldrushmc.bukkit.train.station;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberChest;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberFurnace;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.goldrushmc.bukkit.train.CardinalMarker;

public class TrainStationTransport extends TrainStation {

	private final int maxStopBlocks = 4;
	private final Material stopMat;
	private Block mainStop;

	public TrainStationTransport(JavaPlugin plugin, String stationName,	Map<CardinalMarker, Location> corners, World world) throws TooLowException, StopBlockMismatchException {
		super(plugin, stationName, corners, world);
		this.stopMat = defaultStop;
		for(Block b : this.stopBlocks) {
			if(b.getRelative(this.direction).getRelative(BlockFace.DOWN).getType().equals(this.stopMat) 
					&& b.getRelative(this.direction.getOppositeFace()).getRelative(BlockFace.DOWN).getType().equals(this.stopMat)) {
				this.mainStop = b;
				break;
			}
		}
		//Default to the first Block in the list of stopBlocks.
		if(this.mainStop == null) this.mainStop = this.stopBlocks.get(0);
		if(this.rails != null) {
			if(this.stopBlocks.size() > maxStopBlocks) throw new StopBlockMismatchException();
			createTransport();	
		}
	}
	public TrainStationTransport(JavaPlugin plugin, String stationName,	Map<CardinalMarker, Location> corners, World world, Material stopMat) throws TooLowException {
		super(plugin, stationName, corners, world, stopMat);
		this.stopMat = stopMat;
		if(this.rails != null) {
			createTransport();	
		}
	}

	@Override
	public void createTransport() {

		if(this.trains == null) this.trains = new ArrayList<MinecartGroup>();
		int trainNum = this.trains.size() + 1;

		List<EntityType> carts = new ArrayList<EntityType>();
		carts.add(EntityType.MINECART_CHEST);
		carts.add(EntityType.MINECART_FURNACE);
		//Should make the furnace spawn right on top of the stop block.
		MinecartGroup train = MinecartGroup.spawn(this.mainStop, this.direction.getOppositeFace(), carts);
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
	 * Find the stop blocks for a typical transport station.
	 * <p>
	 * Assumption:
	 * <li> There are a max of 4 stop blocks, all of which are on the same length of track, all clumped together.
	 * <li> The blocks will be ordered as such:
	 * <ol>
	 * <li> 0 : The block which the train shall spawn on (furthest in the train station's designated direction).
	 * <li> 1, 2, 3 : The other blocks which will support the departure function.
	 * 
	 * 
	 */
	@Override
	public List<Block> findStopBlocks(Material m) {
		List<Block> stopBlocks = new ArrayList<Block>();
		for(Block b : this.trainArea) {
			if(b.getType().equals(m)) {
				stopBlocks.add(b.getRelative(BlockFace.UP));
			}
		}
		return stopBlocks;
	}
	
	@Override
	public MinecartGroup findNextDeparture() {
		for(MinecartGroup mg : this.trains) {
			for(MinecartMember<?> mm : mg) {
				if(mm instanceof MinecartMemberFurnace) {
					if(this.stopBlocks.contains(mm.getBlock())) {
						return mg;
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public boolean pushQueue() {
		MinecartGroup mg = findNextDeparture();
		if(mg == null) return false;
		mg.getProperties().setSpeedLimit(0.4);
		for(MinecartMember<?> mm : mg) {
			if(mm instanceof MinecartMemberFurnace) {
				MinecartMemberFurnace power = (MinecartMemberFurnace) mm;
				power.addFuelTicks(1000);
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
	
	public void changeSignLogic(String trainName) {
		this.signs.updateTrain(trainName);
	}
	
	public Block getMainStopBlock() {
		return this.mainStop;
	}
}
