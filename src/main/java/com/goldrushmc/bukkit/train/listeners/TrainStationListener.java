package com.goldrushmc.bukkit.train.station;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.goldrushmc.bukkit.defaults.DefaultListener;
import com.goldrushmc.bukkit.train.event.EnterTrainStation;
import com.goldrushmc.bukkit.train.event.ExitTrainStation;

/**
 * Currently, all this class really does is adds and removes {@link Player}s from the {@link TrainStation#visitors} list.
 * 
 * @author Diremonsoon
 *
 */
public class TrainStationListener extends DefaultListener {

	//Both Maps only exist for quick referential access. Otherwise, we would have to iterate through train stations each time.
	private static Map<Block, String> locToLookFor = new HashMap<Block, String>();
	private static Map<String, TrainStation> stationStore = new HashMap<String, TrainStation>();

	public TrainStationListener(JavaPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event) {
		Block from = event.getFrom().getBlock(), to = event.getTo().getBlock();
		
		//Has left the station!
		if(locToLookFor.containsKey(from)) {
			String station = locToLookFor.get(from);
			ExitTrainStation exit = new ExitTrainStation(stationStore.get(station), event.getPlayer());
			Bukkit.getServer().getPluginManager().callEvent(exit);
		}
		
		//Has entered the station!
		else if(locToLookFor.containsKey(to)) {
			String station = locToLookFor.get(to);
			EnterTrainStation enter = new EnterTrainStation(stationStore.get(station), event.getPlayer());
			Bukkit.getServer().getPluginManager().callEvent(enter);
		}
	}
	
	@EventHandler
	public void onEntrance(EnterTrainStation event) {
		TrainStation station = event.getTrainStation();
		Player p = event.getPlayer();
		p.sendMessage("Welcome!");
		if(!station.getVisitors().contains(p)) {
			station.addVisitor(p);
		}
	}
	
	@EventHandler
	public void onExit(ExitTrainStation event) {
		TrainStation station = event.getTrainStation();
		Player p = event.getPlayer();
		p.sendMessage("GoodBye!");
		if(station.getVisitors().contains(p)) {
			station.removeVisitor(p);
		}
	}
	
	public void populate() {
		if(!TrainStation.trainStations.isEmpty()) {
			for(TrainStation station : TrainStation.trainStations) {
				stationStore.put(station.getStationName(), station);
				for(Location loc : station.perimeter) {
					locToLookFor.put(loc.getBlock(), station.getStationName());
				}
			}
		}
	}
	public static void addStation(TrainStation station) {
		stationStore.put(station.getStationName(), station);
		for(Location loc : station.getPerimeter()) {
			locToLookFor.put(loc.getBlock(), station.getStationName());
		}
	}
	public static void removeStation(TrainStation station) {
		stationStore.remove(station.getStationName());
		for(Location loc : station.getPerimeter()) {
			locToLookFor.remove(loc);
		}
	}
}
