package com.goldrushmc.bukkit.train.signs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.block.Sign;

public class SignLogic implements ISignLogic {

	public Map<Sign, SignType> signs = new HashMap<Sign, SignType>();
	
	
	public World world;
	
	public SignLogic(World world) {
		this.world = world;
		findRelevantSigns(this.world);
	}
	
	
	@Override
	public Map<Sign, SignType> getSigns() {
		return signs;
	}

	@Override
	public void addSign(Sign sign, SignType type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSign(Sign sign) {
		// TODO Auto-generated method stub

	}

	@Override
	public SignType getSignType(Sign sign) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void findRelevantSigns(World world) {

	}

}
