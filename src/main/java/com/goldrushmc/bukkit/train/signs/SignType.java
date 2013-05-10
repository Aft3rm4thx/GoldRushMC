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
	ADD_CART(new Permission("goldrushmc.sign.addcart")),
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
}
