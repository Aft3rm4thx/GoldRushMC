package com.goldrushmc.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.goldrushmc.bukkit.defaults.CommandDefault;
import com.goldrushmc.bukkit.train.TrainFactory;

public class CreateTrain extends CommandDefault {

	public CreateTrain(JavaPlugin plugin) {	super(plugin);	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		//Command cannot be used from the console
		if(sender instanceof Player) {
			Player player = (Player) sender;

			//User must have permission to use this suite of commands
			if(!sender.hasPermission("goldrushmc.train.create")) {
				sender.sendMessage("You don't have permission to create trains!");
				return true;
			}

			if(!TrainFactory.selections.containsKey(player)) { 
				sender.sendMessage("Please add some coordinates first!");
				return true;
			}

			if(args[0].equalsIgnoreCase("Standard")) {
				if(args.length == 3) {
					TrainFactory.newStandardTrain(player, args[1], args[2]);
					return true;
				}
			}

			if(TrainFactory.selections.get(player).length < 2) { 
				sender.sendMessage("Please choose two coordinates first!");
				return true;
			}

			if(args[0].equalsIgnoreCase("Custom")) {
				if(args.length == 5) {
					if(isInt(args[3]) && isInt(args[4])) {
						TrainFactory.newCustomTrain(player, args[1], args[2], Integer.valueOf(args[3]), Integer.valueOf(args[4]));
						return true;
					}
				}
			}

			else if(args[0].equalsIgnoreCase("Passenger")) {
				if(args.length == 4) {
					if(isInt(args[3])) {
						TrainFactory.newPassengerTrain(player, args[1], args[2], Integer.valueOf(args[3]));
						return true;
					}
				}
			}
			else if(args[0].equalsIgnoreCase("Storage")) {
				if(args.length == 4) {
					if(isInt(args[3])) {
						TrainFactory.newStorageTrain(player, args[1], args[2], Integer.valueOf(args[3]));
						return true;
					}
				}
			}
		}

		return false;
	}
}
