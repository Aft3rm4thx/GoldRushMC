package com.goldrushmc.bukkit.train.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.tc.events.MemberBlockChangeEvent;
import com.goldrushmc.bukkit.defaults.DefaultListener;
import com.goldrushmc.bukkit.train.TrainFactory;
import com.goldrushmc.bukkit.train.event.EnterTrainStationEvent;
import com.goldrushmc.bukkit.train.event.ExitTrainStationEvent;
import com.goldrushmc.bukkit.train.event.StationSignEvent;
import com.goldrushmc.bukkit.train.event.TrainEnterStationEvent;
import com.goldrushmc.bukkit.train.event.TrainExitStationEvent;
import com.goldrushmc.bukkit.train.event.TrainFullStopEvent;
import com.goldrushmc.bukkit.train.signs.SignType;
import com.goldrushmc.bukkit.train.station.TrainStation;

/**
 * Currently, all this class really does is adds and removes {@link Player}s from the {@link TrainStation#visitors} list.
 * 
 * @author Diremonsoon
 *
 */
public class TrainStationLis extends DefaultListener {

	//Both Maps only exist for quick referential access. Otherwise, we would have to iterate through train stations each time.
	private static Map<Block, String> stationArea = new HashMap<Block, String>();
	private static Map<String, TrainStation> stationStore = new HashMap<String, TrainStation>();

	public TrainStationLis(JavaPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event) {
		//We look for the blocks to and from
		Block from = event.getFrom().getBlock(), to = event.getTo().getBlock();


		//Has left the station!
		if(stationArea.containsKey(from) && !stationArea.containsKey(to)) {
			String station = stationArea.get(from);
			ExitTrainStationEvent exit = new ExitTrainStationEvent(stationStore.get(station), event.getPlayer());
			Bukkit.getServer().getPluginManager().callEvent(exit);
		}

		//Has entered the station!
		else if(stationArea.containsKey(to) && !stationArea.containsKey(from)) {
			String station = stationArea.get(to);
			EnterTrainStationEvent enter = new EnterTrainStationEvent(stationStore.get(station), event.getPlayer());
			Bukkit.getServer().getPluginManager().callEvent(enter);
		}
	}

	@EventHandler
	public void onTrainMove(MemberBlockChangeEvent event) {
		Block to = event.getTo(), from = event.getFrom();

		if(stationArea.containsKey(to)) {
			if(!stationArea.containsKey(from)) {
				//Entering station
				String station = stationArea.get(to);
				TrainEnterStationEvent enter = new TrainEnterStationEvent(stationStore.get(station), event.getGroup());
				Bukkit.getServer().getPluginManager().callEvent(enter);	

			}
			//The train has hit the stop block, and needs to stop.
			else if(to.getRelative(BlockFace.DOWN).equals(stationStore.get(stationArea.get(to)).getStopBlock())) {
				String station = stationArea.get(to);
				TrainFullStopEvent stop = new TrainFullStopEvent(stationStore.get(station), event.getGroup());
				Bukkit.getServer().getPluginManager().callEvent(stop);
			}
		}
			//Leaving station
		else if(!stationArea.containsKey(to) && stationArea.containsKey(from)) {
			String station = stationArea.get(from);
			TrainExitStationEvent exit = new TrainExitStationEvent(stationStore.get(station), event.getGroup());
			Bukkit.getServer().getPluginManager().callEvent(exit);
		}
	}

	/**
	 * Does work with sign clicking events.
	 * 
	 * @param event The {@link Sign} click.
	 */
	@EventHandler
	public void onSignClick(PlayerInteractEvent event) {

		//We don't care about air blocks!
		if(event.getClickedBlock() == null) return;
		
		BlockState bs = event.getClickedBlock().getState();

		//Player can only right click to get this event to work. Otherwise we fail silently.
		if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
		//We don't care if it isn't a sign.
		if(!(bs.getType().equals(Material.SIGN) || bs.getType().equals(Material.SIGN) || bs.getType().equals(Material.SIGN))) return;
		//Make sure the player has permission to use signs at all.
		if(event.getPlayer().hasPermission("goldrushmc.station.signs")) return;
		//We don't want the player holding anything in their hand!
		if(event.getItem() != null) { event.getPlayer().sendMessage("Please put away your things before using signs!"); return; }

		//Collect Player and Sign instances.
		Player p = event.getPlayer();
		Sign sign = (Sign) bs;

		
		//If the player is within a station, we need to throw an event to handle this sign click.
		if(stationArea.containsKey(p.getLocation().getBlock())) {
			StationSignEvent sEvent = new StationSignEvent(stationStore.get(stationArea.get(p.getLocation().getBlock())), sign, p);
			Bukkit.getServer().getPluginManager().callEvent(sEvent);
		}
	}
	
	@EventHandler
	public void onStationSignUse(StationSignEvent event) {
		Player player = event.getPlayer();
		SignType type = event.getSignType();
		TrainStation station = event.getTrainStation();
		String trainName = station.getDepartingTrain();
		
		//Determine which thing to perform.
		switch(type) {
		case ADD_STORAGE_CART: TrainFactory.addCart(player, trainName, EntityType.MINECART_CHEST); break;
		case ADD_RIDE_CART: TrainFactory.addCart(player, trainName, EntityType.MINECART); break;
		case REMOVE_STORAGE_CART: TrainFactory.removeCart(trainName, player, EntityType.MINECART_CHEST); break;
		case REMOVE_RIDE_CART: TrainFactory.removeCart(trainName, player, EntityType.MINECART); break;
		case FIX_BRIDGE: //TODO This will deal with fixing the broken rail pathways. NO logic exists yet for path finding.
		default: break; //If it is none of these, we do not care right now.
		}
		
	}

	@EventHandler
	public void onEntrance(EnterTrainStationEvent event) {
		TrainStation station = event.getTrainStation();
		Player p = event.getPlayer();
		p.sendMessage("Welcome to " + station.getStationName() + "!");
		if(!station.getVisitors().contains(p)) {
			station.addVisitor(p);
		}
	}

	@EventHandler
	public void onExit(ExitTrainStationEvent event) {
		TrainStation station = event.getTrainStation();
		Player p = event.getPlayer();
		p.sendMessage(station.getStationName() + " wishes you a Goodbye..");
		if(station.getVisitors().contains(p)) {
			station.removeVisitor(p);
		}
	}

	@EventHandler
	public void onTrainDepart(TrainExitStationEvent event) {
	}
	
	//TODO This may be useless. Depends on how large the station is, and how many directions the station can take.
	@EventHandler
	public void onTrainArrive(TrainEnterStationEvent event) {
	}
	
	@EventHandler
	public void onTrainNextToDepart(TrainFullStopEvent event) {
		event.getTrain().stop(true);
		event.getTrainStation().changeSignLogic(event.getTrain().getProperties().getTrainName());
	}

	public void populate() {
		if(!TrainStation.trainStations.isEmpty()) {
			for(TrainStation station : TrainStation.trainStations) {
				stationStore.put(station.getStationName(), station);
				for(Block b : station.getArea()) {
					stationArea.put(b, station.getStationName());
				}
			}
		}
	}
	public static void addStation(TrainStation station) {
		stationStore.put(station.getStationName(), station);
		for(Block b : station.getArea()) {
			stationArea.put(b, station.getStationName());
		}
	}
	public static void removeStation(TrainStation station) {
		stationStore.remove(station.getStationName());
		for(Block b : station.getArea()) {
			stationArea.remove(b);
		}
	}
}
