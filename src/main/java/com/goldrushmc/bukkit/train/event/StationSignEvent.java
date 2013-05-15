package com.goldrushmc.bukkit.train.event;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.goldrushmc.bukkit.train.signs.SignType;
import com.goldrushmc.bukkit.train.station.TrainStation;

public class StationSignEvent extends TrainStationEvent {

	private static final HandlerList handlers = new HandlerList();
	private final Sign sign;
	private final Player player;
	
	public StationSignEvent(final TrainStation station, final Sign sign, final Player player) {
		super(station);
		this.sign = sign;
		this.player = player;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	/**
	 * Gets the type of sign involved in the event.
	 * 
	 * @return The {@link SignType}, if any. Will return null if there is none.
	 */
	public SignType getSignType() {
		if(station.getSigns().getSign(SignType.ADD_STORAGE_CART).equals(sign)) return SignType.ADD_STORAGE_CART;
		else if(station.getSigns().getSign(SignType.ADD_RIDE_CART).equals(sign)) return SignType.ADD_RIDE_CART;
		else if(station.getSigns().getSign(SignType.REMOVE_STORAGE_CART).equals(sign)) return SignType.REMOVE_STORAGE_CART;
		else if(station.getSigns().getSign(SignType.REMOVE_RIDE_CART).equals(sign)) return SignType.REMOVE_RIDE_CART;
		else if(station.getSigns().getSign(SignType.FIX_BRIDGE).equals(sign)) return SignType.FIX_BRIDGE;
		else return null;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Sign getSign() {
		return sign;
	}

}
