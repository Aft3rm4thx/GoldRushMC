package com.goldrushmc.bukkit.guns;

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

public class GunTool extends CommandDefault {

	public GunTool(JavaPlugin plugin) {
		super(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Player p = (Player) sender;
		ItemStack gunTool= new ItemStack(Material.CARROT_STICK);
		List<String> lore = new ArrayList<String>();
		lore.add("Pew Pew");
		ItemMeta meta = gunTool.getItemMeta();
		meta.setLore(lore);
		meta.setDisplayName("Colt");
		gunTool.setItemMeta(meta);
		gunTool.setDurability((short) 1);
		p.getInventory().addItem(gunTool);
		return true;
	}

}
