package com.goldrushmc.bukkit.train;

import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class Train implements ITrain {

	public Train() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Player> getPassengers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ItemStack> getPayload() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPassengers(List<Player> passengers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPayload(List<ItemStack> payload) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String nextStop() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getStops() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addStop(String stopName, Location stopLoc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeStop(String stopName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isBlocked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setBlocked(boolean isBlocked) {
		// TODO Auto-generated method stub
		
	}

}
