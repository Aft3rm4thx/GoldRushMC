package com.goldrushmc.bukkit.train.signs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class SignLogic implements ISignLogic {

	public Map<String, Sign> signs = new HashMap<String, Sign>();
	public Map<SignType, Sign> signTypes = new HashMap<SignType, Sign>();
	public List<Sign> signList = new ArrayList<Sign>();

	public SignLogic(List<Block> blocks) {
		findRelevantSigns(blocks);
	}


	@Override
	public List<Sign> getSigns() {
		return signList;
	}

	@Override
	public void addSign(Sign sign, SignType type) {
		this.signTypes.put(type, sign);
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
	public void findRelevantSigns(List<Block> blocks) {
		for(Block b : blocks) {
			if(b.getType().equals(Material.SIGN) || b.getType().equals(Material.WALL_SIGN) || b.getType().equals(Material.SIGN_POST)) {
				//			if(b.getState() instanceof Sign) {
				Sign s = (Sign) b.getState();
				String[] lines = s.getLines();
				//Make sure it has a length of 4.
				if(lines.length == 4) {
					if(lines[0].equals("{trains}")) {
						if(lines[1].equals("buy_storage")) {
							this.signList.add(s);
							this.signTypes.put(SignType.ADD_STORAGE_CART, s);
							this.signs.put(lines[1], s);
						}
						else if(lines[1].equals("sell_storage")) {
							this.signList.add(s);
							this.signTypes.put(SignType.REMOVE_STORAGE_CART, s);
							this.signs.put(lines[1], s);
						}
						else if(lines[1].equals("buy_ride")) {
							this.signList.add(s);
							this.signTypes.put(SignType.ADD_RIDE_CART, s);
							this.signs.put(lines[1], s);
						}
						else if(lines[1].equals("sell_ride")) {
							this.signList.add(s);
							this.signTypes.put(SignType.REMOVE_RIDE_CART, s);
							this.signs.put(lines[1], s);
						}
						else if(lines[1].equals("direction")) {
							Bukkit.getLogger().info("Getting train direction!");
							this.signList.add(s);
							this.signTypes.put(SignType.TRAIN_STATION_DIRECTION, s);
							this.signs.put(lines[1], s);
						}
					}
					//TODO Could be used for house signs.
					else if(lines[0].equals("{houses}")) {

					}
					//TODO Could be used for bank signs.
					else if(lines[0].equals("{banks}")) {

					}
				}
			}
		}
	}


	@Override
	public Sign getSign(String signName) {
		return this.signs.get(signName);
	}

	@Override
	public Sign getSign(SignType type) {
		return this.signTypes.get(type);
	}


	@Override
	public Map<SignType, Sign> getSignTypes() {
		return signTypes;
	}


	@Override
	public void updateTrain(String trainName) {
		List<Sign> signsToChange = new ArrayList<Sign>();
		//Get the signs to change.
		for(SignType s : this.signTypes.keySet()) {
			if(s.equals(SignType.ADD_RIDE_CART) || s.equals(SignType.ADD_STORAGE_CART) || s.equals(SignType.REMOVE_RIDE_CART) || s.equals(SignType.REMOVE_STORAGE_CART)) {
				signsToChange.add(this.signTypes.get(s));
			}
		}
		//Set the new value.
		for(Sign s : signsToChange) {
			s.setLine(3, trainName);
		}
		//Create new lists and update.
		this.signList = signsToChange;
		this.signTypes = new HashMap<SignType, Sign>();
		this.signs = new HashMap<String, Sign>();
		//Add to the map
		for(Sign s : this.signTypes.values()) {
			String[] lines = s.getLines();
			if(lines[1].equals("buy_storage")) {
				this.signTypes.put(SignType.ADD_STORAGE_CART, s);
				this.signs.put(lines[1], s);
			}
			else if(lines[1].equals("sell_storage")) {
				this.signTypes.put(SignType.REMOVE_STORAGE_CART, s);
				this.signs.put(lines[1], s);
			}
			else if(lines[1].equals("buy_ride")) {
				this.signTypes.put(SignType.ADD_RIDE_CART, s);
				this.signs.put(lines[1], s);
			}
			else if(lines[1].equals("sell_ride")) {
				this.signTypes.put(SignType.REMOVE_RIDE_CART, s);
				this.signs.put(lines[1], s);
			}
		}

	}
}
