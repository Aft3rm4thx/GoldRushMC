package com.goldrushmc.bukkit.train.signs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

public class SignLogic implements ISignLogic {

	public Map<String, Sign> signs = new HashMap<String, Sign>();
	public Map<Sign, SignType> signTypes = new HashMap<Sign, SignType>();

	public Chunk chunk;

	public SignLogic(Chunk chunk) {
		this.chunk = chunk;
		findRelevantSigns(this.chunk);
	}


	@Override
	public Map<Sign, SignType> getSigns() {
		return signTypes;
	}

	@Override
	public void addSign(Sign sign, SignType type) {
		this.signTypes.put(sign, type);
	}
	
	@Override
	public void addSign(String signName, Sign sign) {
		this.signs.put(signName, sign);
		
	}

	@Override
	public void removeSign(String signName) {
		if(this.signs.containsKey(signName)) {
			Sign sign = this.signs.get(signName);
			if(this.signTypes.containsKey(sign)) this.signTypes.remove(sign);
		}
	}

	@Override
	public SignType getSignType(Sign sign) {
		String name = sign.getLine(1);
		return signTypes.get(name);
	}

	@Override
	public void findRelevantSigns(Chunk chunk) {
		if(!chunk.isLoaded()) chunk.load();

		BlockState[] states = chunk.getTileEntities();
		for(int j = 0; j < states.length; j++) {
			Material m = states[j].getType();
			if(m.equals(Material.SIGN) || m.equals(Material.SIGN_POST) || m.equals(Material.WALL_SIGN)) {
				Sign toAdd = (Sign) states[j];
				String[] lines = toAdd.getLines();
				if(lines.length == 4) {
					if(lines[0].equals("{train_station}")){
						String stationName = lines[1];
						addSign(stationName, toAdd);
						addSign(toAdd, SignType.TRAIN_STATION);
					}
				}
			}
		}
	}


	@Override
	public Sign getSign(String signName) {
		return this.signs.get(signName);
	}
}
