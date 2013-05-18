package com.goldrushmc.bukkit.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.goldrushmc.bukkit.defaults.CommandDefault;
import com.goldrushmc.bukkit.train.CardinalMarker;
import com.goldrushmc.bukkit.train.FindCorners;
import com.goldrushmc.bukkit.train.listeners.WandLis;
import com.goldrushmc.bukkit.train.station.StationType;
import com.goldrushmc.bukkit.train.station.StopBlockMismatchException;
import com.goldrushmc.bukkit.train.station.TooLowException;
import com.goldrushmc.bukkit.train.station.TrainStationTransport;


/**
 * For the command "CreateStation"
 * 
 * @author Diremonsoon
 *
 */
public class CreateTrainStation extends CommandDefault {

	public CreateTrainStation(JavaPlugin plugin) {
		super(plugin);
	}

	/**
	 * Needs the type of station followed by the name of the station
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!sender.hasPermission("goldrushmc.station.create")) return false;
		if(!(args.length == 2) && !(args.length == 3)) return false;

		Player p = (Player) sender;

		if(!WandLis.wandLoc.containsKey(p)) { sender.sendMessage("Create some markers first!"); return true; }

		int size = WandLis.wandLoc.get(p).size();

		if(!(size == 4)) { sender.sendMessage("You need " + (4 - size) + " more markers"); return true; }

		List<Location> locs = WandLis.wandLoc.get(p);

		FindCorners fc = new FindCorners(locs.get(0),locs.get(1),locs.get(2),locs.get(3));

		//Store the locations in the appropriate map.
		Map<CardinalMarker, Location> corners = new HashMap<CardinalMarker, Location>();
		corners.put(CardinalMarker.NORTH_EAST_CORNER, fc.getNorthEast());
		corners.put(CardinalMarker.SOUTH_EAST_CORNER, fc.getSouthEast());
		corners.put(CardinalMarker.NORTH_WEST_CORNER, fc.getNorthWest());
		corners.put(CardinalMarker.SOUTH_WEST_CORNER, fc.getSouthWest());

		StationType type = StationType.findType(args[1]);
		if(type == null) { p.sendMessage("Please use a legitimate station type"); return true;}

		boolean trainSpawn = false;
		if(args.length == 3 && args[2].equals("train")) trainSpawn = true;
		
		try {
			switch(type) {
			case DEFAULT: new TrainStationTransport(plugin, args[1], corners, p.getWorld(), trainSpawn);	break;
			case PASSENGER_TRANS:
			case STORAGE_TRANS: new TrainStationTransport(plugin, args[1], corners, p.getWorld(), trainSpawn); break;
			}
		}catch (TooLowException | StopBlockMismatchException e) {
			Bukkit.getLogger().warning(e.getMessage());
		}

		//for a visual representation of the surface.
//		for(Block b : s.getSurface()) {
//			b.setType(Material.GOLD_BLOCK);
//		}

		WandLis.wandLoc.remove(p);
		p.sendMessage("Markers reset!");

		return true;

	}
}