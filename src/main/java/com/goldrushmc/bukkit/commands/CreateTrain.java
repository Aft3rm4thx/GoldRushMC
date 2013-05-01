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

		if(sender instanceof Player) {
			Player player = (Player) sender;

			if(!sender.hasPermission("goldrushmc.train.create")) {
				sender.sendMessage("You don't have permission to create trains!");
				return true;
			}
			if(args[0].equalsIgnoreCase("Standard")) {
				if(args.length == 1) {
					TrainFactory.newStandardTrain(player.getEyeLocation(), args[1]);
					return true;
				}
			}
			if(args[0].equalsIgnoreCase("Custom")) {
				if(args.length == 4) {
					TrainFactory.newCustomTrain(player.getEyeLocation(), args[1], 1, 1);
					return true;
				}
			}

			else if(args[0].equalsIgnoreCase("Passenger")) {
				if(args.length == 2) {
					TrainFactory.newPassengerTrain(player.getEyeLocation(), args[1], 1);
					return true;
				}
			}
			else if(args[0].equalsIgnoreCase("Storage")) {
				if(args.length == 2) {
					TrainFactory.newStorageTrain(player.getEyeLocation(), args[1], 1);
					return true;
				}
			}
		}

		return false;
	}

}
