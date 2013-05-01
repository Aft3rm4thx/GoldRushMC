package com.goldrushmc.bukkit.defaults;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CommandDefault implements CommandExecutor {

	public final JavaPlugin plugin;
	
	public CommandDefault(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public abstract boolean onCommand(CommandSender sender, Command cmd, String label, String[] args);
}
