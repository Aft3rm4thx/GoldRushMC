package com.goldrushmc.bukkit.train.signs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class SignLogic implements ISignLogic {

	private Map<String, Sign> signs = new HashMap<String, Sign>();
	private Map<Sign, SignType> signTypes = new HashMap<Sign, SignType>();
	private List<Sign> signList = new ArrayList<Sign>();

	public SignLogic(List<Block> blocks) {
		findRelevantSigns(blocks);
	}


	@Override
	public List<Sign> getSigns() {
		return signList;
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
	public void findRelevantSigns(List<Block> blocks) {
		for(Block b : blocks) {
			if(b.getType().equals(Material.SIGN) || b.getType().equals(Material.WALL_SIGN) || b.getType().equals(Material.SIGN_POST)) {
				Sign s = (Sign) b.getState();
				String[] lines = s.getLines();
				//Make sure it has a length of 4. It always should...
				if(lines.length == 4) {
					if(lines[0].equals("{trains}")) {
						if(lines[1].equals("Departing:")) {
							this.signList.add(s);
							this.signTypes.put(s, SignType.TRAIN_STATION);
							this.signs.put(lines[1], s);
						}
						if(lines[1].equals("buy_storage")) {
							this.signList.add(s);
							this.signTypes.put(s, SignType.ADD_STORAGE_CART);
							this.signs.put(lines[1], s);
						}
						else if(lines[1].equals("sell_storage")) {
							this.signList.add(s);
							this.signTypes.put(s, SignType.REMOVE_STORAGE_CART);
							this.signs.put(lines[1], s);
						}
						else if(lines[1].equals("buy_ride")) {
							this.signList.add(s);
							this.signTypes.put(s, SignType.ADD_RIDE_CART);
							this.signs.put(lines[1], s);
						}
						else if(lines[1].equals("sell_ride")) {
							this.signList.add(s);
							this.signTypes.put(s, SignType.REMOVE_RIDE_CART);
							this.signs.put(lines[1], s);
						}
						else if(lines[1].equals("direction")) {
							this.signList.add(s);
							this.signTypes.put(s, SignType.TRAIN_STATION_DIRECTION);
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
	public SignType getSignType(Sign sign) {
		return this.signTypes.get(sign);
	}


	@Override
	public Map<Sign, SignType> getSignTypes() {
		return signTypes;
	}
	
	@Override
	public Sign getSign(SignType type) {
		for(Sign sign : this.signTypes.keySet()) {
			if(this.signTypes.get(sign).equals(type)) {
				return sign;
			}
		}
		return null;
	}


	@Override
	public void updateTrain(String trainName) {
		List<Sign> signsToChange = new ArrayList<Sign>();
		//Get the signs to change.
		for(Sign sign : this.signTypes.keySet()) {
			SignType s = this.signTypes.get(sign);
			if(s.equals(SignType.TRAIN_STATION) || s.equals(SignType.ADD_RIDE_CART) || s.equals(SignType.ADD_STORAGE_CART) || s.equals(SignType.REMOVE_RIDE_CART) || s.equals(SignType.REMOVE_STORAGE_CART)) {
				signsToChange.add(sign);
			}
		}
		//Set the new value.
		for(Sign s : signsToChange) {
			s.setLine(2, trainName);
			s.update();
		}
		//Create new lists and update.
		this.signList = signsToChange;
		this.signTypes = new HashMap<Sign, SignType>();
		this.signs = new HashMap<String, Sign>();
		//Add to the map
		for(Sign s : signsToChange) {
			String[] lines = s.getLines();
			if(lines[1].equals("Departing:")) {
				this.signTypes.put(s, SignType.TRAIN_STATION);
				this.signs.put(lines[1], s);
			}
			if(lines[1].equals("buy_storage")) {
				this.signTypes.put(s, SignType.ADD_STORAGE_CART);
				this.signs.put(lines[1], s);
			}
			else if(lines[1].equals("sell_storage")) {
				this.signTypes.put(s, SignType.REMOVE_STORAGE_CART);
				this.signs.put(lines[1], s);
			}
			else if(lines[1].equals("buy_ride")) {
				this.signTypes.put(s, SignType.ADD_RIDE_CART);
				this.signs.put(lines[1], s);
			}
			else if(lines[1].equals("sell_ride")) {
				this.signTypes.put(s, SignType.REMOVE_RIDE_CART);
				this.signs.put(lines[1], s);
			}
		}

	}
}
