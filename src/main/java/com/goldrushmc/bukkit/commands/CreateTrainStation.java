package com.goldrushmc.bukkit.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.goldrushmc.bukkit.defaults.CommandDefault;
import com.goldrushmc.bukkit.train.CardinalMarker;
import com.goldrushmc.bukkit.train.WandLis;
import com.goldrushmc.bukkit.train.station.StationType;
import com.goldrushmc.bukkit.train.station.TrainStation;
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
		if(args.length < 2) return false;
		
		Player p = (Player) sender;
		
		if(!WandLis.wandLoc.containsKey(p)) { sender.sendMessage("Create some markers first!"); return true; }
		
		int size = WandLis.wandLoc.get(p).size();
		
		if(!(size == 4)) { sender.sendMessage("You need " + (4 - size) + " more markers"); return true; }
		
		//Get the list of locations
		Location[] finalOrder = orderLocations(WandLis.wandLoc.get(p));
		
		//Store the locations in the appropriate map.
		Map<CardinalMarker, Location> corners = new HashMap<CardinalMarker, Location>();
		corners.put(CardinalMarker.NORTH_EAST_CORNER, finalOrder[0]);
		corners.put(CardinalMarker.NORTH_WEST_CORNER, finalOrder[1]);
		corners.put(CardinalMarker.SOUTH_EAST_CORNER, finalOrder[2]);
		corners.put(CardinalMarker.SOUTH_WEST_CORNER, finalOrder[3]);
		
		TrainStation station = null;
		
		StationType type = StationType.findType(args[1]);
		if(type == null) { p.sendMessage("Please use a legitimate station type"); return true;}
		
		switch(type) {
		case DEFAULT: station = new TrainStationTransport(plugin, args[2], corners);
		case PASSENGER_TRANS:
		case STORAGE_TRANS: station = new TrainStationTransport(plugin, args[2], corners);
		}
		
		//Add the train station to the list.
		TrainStation.trainStations.add(station);
		
		
		
		return false;
		
	}
	
	/** 
	 * East = X + 1
	 * West = X - 1
	 * South = Z + 1
	 * North = Z - 1
	 * 
	 * North East = X + 1, Z - 1 Highest x, Lowest z
	 * North West = X - 1, Z - 1 Lowest x, Lowest z
	 * South East = X + 1, Z + 1 Highest x, Highest z
	 * South West = X - 1, Z + 1 Lowest x, Highest z
	 * @return
	 */
	public Location[] orderLocations(List<Location> locs) {
		if(!(locs.size() == 4)) return null;
		
		//Get the locations
		Location l1 = locs.get(0), l2 = locs.get(1), l3 = locs.get(2), l4 = locs.get(3);
		//Get the values of x and z for each location.
		double x1 = l1.getX(), z1 = l1.getZ(), x2 = l2.getX(), z2 = l2.getZ(), x3 = l3.getX(), z3 = l3.getZ(), x4 = l4.getX(), z4 = l4.getZ();
		
		
		//North East
		if(x1 > x2 && x1 > x3 && x1 > x4 && z1 < z2 && z1 < z3 && z1 < z4);
		//North West
		if(x1 < x2 && z1 < z2);
		//South West
		if(x1 < x2 && z1 > z2);
		
		
		return null;
	}
}
