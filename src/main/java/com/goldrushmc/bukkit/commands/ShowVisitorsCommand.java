package com.goldrushmc.bukkit.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.goldrushmc.bukkit.defaults.CommandDefault;
import com.goldrushmc.bukkit.train.station.TrainStation;

public class ShowVisitorsCommand extends CommandDefault {

	public ShowVisitorsCommand(JavaPlugin plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		List<TrainStation> stations = TrainStation.getTrainStations();
		
		if(stations.isEmpty()) { sender.sendMessage("There are no stations available at this time."); return true; }
		
		for(TrainStation station : stations) {
			sender.sendMessage(ChatColor.GREEN + station.getStationName() + ChatColor.RESET + " has " + "(" 
					+ ChatColor.GREEN + station.getVisitors().size() + ChatColor.RESET + ") current visitors");
		}
		
		return true;
	}

}
