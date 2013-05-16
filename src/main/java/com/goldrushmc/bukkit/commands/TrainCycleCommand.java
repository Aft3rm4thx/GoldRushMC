package com.goldrushmc.bukkit.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.goldrushmc.bukkit.defaults.CommandDefault;
import com.goldrushmc.bukkit.train.scheduling.Departure;

public class TrainCycleCommand extends CommandDefault {

	private static int taskID = 0;
	
	public TrainCycleCommand(JavaPlugin plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args[0].equalsIgnoreCase("Start")) {
			taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Departure(plugin), 100, 6000);
		}
		else if(args[0].equalsIgnoreCase("Stop")) {
			Bukkit.getScheduler().cancelTask(taskID);
		}
		return false;
	}

}
