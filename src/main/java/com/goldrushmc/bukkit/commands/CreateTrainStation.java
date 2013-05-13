package com.goldrushmc.bukkit.commands;

import java.util.ArrayList;
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
		
		//Get the list of locations in the right order.
		List<Location> locs = getDirection(WandLis.wandLoc.get(p));
		
		//Store the locations in the appropriate map.
		Map<CardinalMarker, Location> corners = new HashMap<CardinalMarker, Location>();
		corners.put(CardinalMarker.NORTH_EAST_CORNER, locs.get(0));
		corners.put(CardinalMarker.SOUTH_EAST_CORNER, locs.get(1));
		corners.put(CardinalMarker.NORTH_WEST_CORNER, locs.get(2));
		corners.put(CardinalMarker.SOUTH_WEST_CORNER, locs.get(3));
				
		StationType type = StationType.findType(args[1]);
		if(type == null) { p.sendMessage("Please use a legitimate station type"); return true;}
		
		switch(type) {
		case DEFAULT: new TrainStationTransport(plugin, args[1], corners, p.getWorld()); break;
		case PASSENGER_TRANS:
		case STORAGE_TRANS: new TrainStationTransport(plugin, args[1], corners, p.getWorld()); break;
		}	
		
		WandLis.wandLoc.remove(p);
		p.sendMessage("Markers reset!");
		
		return true;
		
	}
	
	/**
	 * Determines the correct directions for each of the locations and orders them accordingly.
	 *
	 * @param locs
	 * @return
	 */
	public List<Location> getDirection(List<Location> locs) {
		
		
		Location loc1 = locs.get(0), loc2 = locs.get(1), loc3 = locs.get(2), loc4 = locs.get(3);
		
		List<Location> directions = new ArrayList<Location>();

		Location southwest = null, northwest = null, southeast = null, northeast = null;

		int x1 = loc1.getBlockX(), z1 = loc1.getBlockZ(), x2 = loc2.getBlockX(), z2 = loc2.getBlockZ();
		int x3 = loc3.getBlockX(), z3 = loc3.getBlockZ(), x4 = loc4.getBlockX(), z4 = loc4.getBlockZ();
		boolean u1 = false, u2 = false, u3 = false, u4 = false;

		if (x1 < x2 && z1 < z2) {

			if (x1 < x3 && z1 < z3) {

				if (x1 < x4 && z1 < z4) {

					northwest = loc1;
					u1 = true;
					
				} else {

					northwest = loc4;
					u4 = true;
				}
			} else if (x3 < x4 && z3 < z4) {

				northwest = loc3;
				u3 = true;
			} else {

				northwest = loc4;
				u4 = true;
			}

		} else if (x2 < x3 && z2 < z3) {

			if (x2 < x4 && z2 < z4) {

				northwest = loc2;
				u2 = true;
			} else {

				northwest = loc4;
				u4 = true;
			}

		} else if (x3 < x4 && z3 < z4) {

			northwest = loc3;
			u3 = true;
		} else {

			northwest = loc4;
			u4 = true;
		}

		if (x1 > x2 && z1 > z2) {

			if (x1 > x3 && z1 > z3) {

				if (x1 > x4 && z1 > z4) {

					northwest = loc1;
					u1 = true;
				} else {

					northwest = loc4;
					u4 = true;
				}
			} else if (x3 > x4 && z3 > z4) {

				northwest = loc3;
				u3 = true;
			} else {

				northwest = loc4;
				u4 = true;
			}

		} else if (x2 > x3 && z2 > z3) {

			if (x2 > x4 && z2 > z4) {

				northwest = loc2;
				u2 = true;
			} else {

				northwest = loc4;
				u4 = true;
			}

		} else if (x3 > x4 && z3 > z4) {

			northwest = loc3;
			u3 = true;
		} else {

			northwest = loc4;
			u4 = true;
		}

		if (x1 > x2 && z1 < z2) {

			if (x1 > x3 && z1 < z3) {

				if (x1 > x4 && z1 < z4) {

					northeast = loc1;
					u1 = true;
				} else {

					northeast = loc4;
					u4 = true;
				}
			} else if (x3 > x4 && z3 < z4) {

				northeast = loc3;
				u3 = true;
			} else {

				northeast = loc4;
				u4 = true;
			}

		} else if (x2 > x3 && z2 < z3) {

			if (x2 > x4 && z2 < z4) {

				northeast = loc2;
				u2 = true;
			} else {

				northeast = loc4;
				u4 = true;
			}

		} else if (x3 > x4 && z3 < z4) {

			northeast = loc3;
			u3 = true;
		} else {

			northeast = loc4;
			u4 = true;
		}

		if (x1 < x2 && z1 > z2) {

			if (x1 < x3 && z1 > z3) {

				if (x1 < x4 && z1 > z4) {

					southwest = loc1;
					u1 = true;
				} else {

					southwest = loc4;
					u4 = true;
				}
			} else if (x3 < x4 && z3 > z4) {

				southwest = loc3;
				u3 = true;
			} else {

				southwest = loc4;
				u4 = true;
			}

		} else if (x2 < x3 && z2 > z3) {

			if (x2 < x4 && z2 > z4) {

				southwest = loc2;
				u2 = true;
			} else {

				southwest = loc4;
				u4 = true;
			}

		} else if (x3 < x4 && z3 > z4) {

			southwest = loc3;
			u3 = true;
		} else {

			southwest = loc4;
			u4 = true;
		}
		
		if(!u1) southeast = loc1;
		else if(!u2) southeast = loc2;
		else if(!u3) southeast = loc3;
		else if(!u4) southeast = loc4;
		
		directions.add(northeast);
		directions.add(southeast);
		directions.add(northwest);
		directions.add(southwest);
		
		return directions;

	}
}
