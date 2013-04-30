package com.goldrushmc.bukkit.train;

import java.util.List;

import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import com.goldrushmc.bukkit.defaults.DefaultListener;

public class TrainLis extends DefaultListener {

	public TrainLis(JavaPlugin plugin) {
		super(plugin);
	}
	
	public void trainStart(VehicleMoveEvent e) {
		Vehicle cart = e.getVehicle();
		List<MetadataValue> meta = e.getVehicle().getMetadata(null);
		if(meta.contains("Train")) {
			cart.getLocation();
		}
		
	}

}
