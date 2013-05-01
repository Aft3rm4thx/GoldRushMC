package com.goldrushmc.bukkit.train;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.events.GroupLinkEvent;
import com.bergerkiller.bukkit.tc.events.MemberRemoveEvent;
import com.goldrushmc.bukkit.defaults.DefaultListener;

public class TrainLis extends DefaultListener {

	public TrainLis(JavaPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCartLink(GroupLinkEvent e) {
		e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCartBreak(MemberRemoveEvent e) {
		MinecartGroup mg = e.getGroup();
		String trainName = mg.getProperties().getTrainName();
	}

}
