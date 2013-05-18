package com.goldrushmc.bukkit.train.signs;

import org.bukkit.permissions.Permission;

/**
 * This {@code enum} is for the different signs planned for the server.
 * It is a permissions constant enum.
 * 
 *
 * 
 * @author Diremonsoon
 *
 */
public enum SignType {
	TOWN(new Permission("goldrushmc.sign.town")),
	TRAIN_STATION(new Permission("goldrushmc.sign.station")),
	TRAIN_STATION_DIRECTION(new Permission("goldrushmc.sign.direction")),
	TRAIN_STATION_TIME(new Permission("goldrushmc.sign.time")),
	TRAIN_STATION_CART_COUNT(new Permission("goldrushmc.sign.cartcount")),
	ADD_STORAGE_CART(new Permission("goldrushmc.sign.addstorecart")),
	REMOVE_STORAGE_CART(new Permission("goldrushmc.sign.removestorecart")),
	ADD_RIDE_CART(new Permission("goldrushmc.sign.addridecart")),
	REMOVE_RIDE_CART(new Permission("goldrushmc.sign.removeridecart")),
	FIX_BRIDGE(new Permission("goldrushmc.sign.bridgerepair")),
	PAY_TAX(new Permission("goldrushmc.sign.paytax")),
	UPGRADE_HOUSE(new Permission("goldrushmc.sign.house.upgrade")),
	BUY_HOUSE(new Permission("goldrushmc.sign.house.buy")),
	BUILD_HOUSE(new Permission("goldrushmc.sign.house.build")),
	SELL_HOUSE(new Permission("goldrushmc.sign.house.sell")),
	REPAIR_HOUSE(new Permission("goldrushmc.sign.house.fix"));
	
	final Permission perm;
	
	private SignType(Permission perm) {
		this.perm = perm;
	}
	
	public static Permission getSignPermission(SignType type) {
		return type.perm;
	}
}
