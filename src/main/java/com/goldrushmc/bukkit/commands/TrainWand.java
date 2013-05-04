package com.goldrushmc.bukkit.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.goldrushmc.bukkit.defaults.CommandDefault;

public class TrainWand extends CommandDefault {

	public TrainWand(JavaPlugin plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!sender.hasPermission("goldrushmc.train.wand")) {
			sender.sendMessage("You need permission to use the wand!");
			return true;
		}
		
		Player p = (Player) sender;
		ItemStack blazeRod = new ItemStack(Material.BLAZE_ROD);
		List<String> lore = new ArrayList<String>();
		lore.add("TrainTool");
		ItemMeta meta = blazeRod.getItemMeta();
		meta.setLore(lore);
		meta.setDisplayName("Train Wand");
		blazeRod.setItemMeta(meta);
		p.setItemInHand(blazeRod);
		return true;
	}

}
