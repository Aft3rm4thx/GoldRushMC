package com.goldrushmc.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.goldrushmc.bukkit.defaults.CommandDefault;
import com.goldrushmc.bukkit.train.listeners.TrainStationLis;
import com.goldrushmc.bukkit.train.station.TrainStation;

public class RemoveTrainStation extends CommandDefault {

	public RemoveTrainStation(JavaPlugin plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		int starting = TrainStation.getTrainStations().size();
		
		for(TrainStation station : TrainStation.getTrainStations()) {
			if(station.getStationName().equalsIgnoreCase(args[0])) {
				TrainStation.getTrainStations().remove(station);
				TrainStationLis.removeStation(station);
				break;
			}
		}
		
		if(TrainStation.getTrainStations().size() < starting) { sender.sendMessage("Train station removed successfully!"); return true; }
		
		return false;
	}

}
