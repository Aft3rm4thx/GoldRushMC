package com.goldrushmc.bukkit.train.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.goldrushmc.bukkit.defaults.DefaultListener;
import com.goldrushmc.bukkit.train.event.EnterTrainStation;
import com.goldrushmc.bukkit.train.event.ExitTrainStation;
import com.goldrushmc.bukkit.train.station.TrainStation;

/**
 * Currently, all this class really does is adds and removes {@link Player}s from the {@link TrainStation#visitors} list.
 * 
 * @author Diremonsoon
 *
 */
public class TrainStationListener extends DefaultListener {

	//Both Maps only exist for quick referential access. Otherwise, we would have to iterate through train stations each time.
	private static Map<Block, String> stationArea = new HashMap<Block, String>();
	private static Map<String, TrainStation> stationStore = new HashMap<String, TrainStation>();

	public TrainStationListener(JavaPlugin plugin) {
		super(plugin);
	}

	//TODO FIX TO IGNORE THE Y DIRECTION
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event) {
		//We look for the block below the player.
		Block from = event.getFrom().getBlock().getRelative(BlockFace.DOWN), to = event.getTo().getBlock().getRelative(BlockFace.DOWN);

		//Has left the station!
		if(stationArea.containsKey(from) && !stationArea.containsKey(to)) {
			String station = stationArea.get(from);
			ExitTrainStation exit = new ExitTrainStation(stationStore.get(station), event.getPlayer());
			Bukkit.getServer().getPluginManager().callEvent(exit);
		}

		//Has entered the station!
		else if(stationArea.containsKey(to) && !stationArea.containsKey(from)) {
			String station = stationArea.get(to);
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
		p.sendMessage("Goodbye..");
		if(station.getVisitors().contains(p)) {
			station.removeVisitor(p);
		}
	}

	public void populate() {
		if(!TrainStation.trainStations.isEmpty()) {
			for(TrainStation station : TrainStation.trainStations) {
				stationStore.put(station.getStationName(), station);
				for(Block b : station.getSurface()) {
					stationArea.put(b, station.getStationName());
				}
			}
		}
	}
	public static void addStation(TrainStation station) {
		stationStore.put(station.getStationName(), station);
		for(Block b : station.getSurface()) {
			stationArea.put(b, station.getStationName());
		}
	}
	public static void removeStation(TrainStation station) {
		stationStore.remove(station.getStationName());
		for(Block b : station.getSurface()) {
			stationArea.remove(b);
		}
	}
}
