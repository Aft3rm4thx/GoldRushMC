package com.goldrushmc.bukkit.train;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberChest;
import com.bergerkiller.bukkit.tc.controller.type.MinecartMemberRideable;
import com.bergerkiller.bukkit.tc.events.GroupLinkEvent;
import com.bergerkiller.bukkit.tc.events.GroupRemoveEvent;
import com.bergerkiller.bukkit.tc.events.MemberRemoveEvent;
import com.bergerkiller.bukkit.tc.events.MemberSpawnEvent;
import com.goldrushmc.bukkit.defaults.DefaultListener;
import com.goldrushmc.bukkit.train.util.TrainTools;

public class TrainLis extends DefaultListener {

	public TrainLis(JavaPlugin plugin) {
		super(plugin);
	}
	
	public void onTrainSpawn(MemberSpawnEvent event) {
	}

	/**
	 * This will facilitate the need to update the {@link TrainFactory#ownerStorage} list, because if the inventory changes, the instance changes.
	 * 
	 * @param e The {@link InventoryMoveItemEvent} associated with the chest.
	 */
	public void onMinecartChestInventory(InventoryMoveItemEvent e) {
		Inventory inv = e.getInitiator();

		//This simulataneously figures out if it is a minecart inventory, and the owner of that inventory.
		Player p = TrainFactory.findOwnerByInv(inv);

		//if we have no owner, then get the opposite inventory, in hopes that it will be a minecart owned inventory.
		@SuppressWarnings("unused")
		Inventory toUse = null;
		if(p == null) {
			Inventory source = e.getSource(), dest = e.getDestination();
			if(inv == source) {
				toUse = dest;
			}
			else {
				toUse = source;
			}
			//It is safe to assume at this point that this inventory is indeed part of an owned minecart, so we proceed.
		} else {
			toUse = inv;
		}

	}

	/**
	 * Checks to make sure that the groups connecting are of the same train, and if they aren't, cancel the event.
	 * 
	 * @param event The {@link GroupLinkEvent} in question.
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCartLink(GroupLinkEvent event) {

		//Cancelled by default
		event.setCancelled(true);

		MinecartGroup mg1 = event.getGroup1(), mg2 = event.getGroup2();
		String train1 = mg1.getProperties().getTrainName(), train2 = mg2.getProperties().getTrainName();

		if(train1.equals(train2)) {
			event.setCancelled(false);
		}
	}


	/**
	 *  THIS EVENT IS NOT BEING USED AT THE MOMENT. WE NEED TO FIGURE OUT WHAT TO DO WITH BROKEN CARTS
	 * @param event
	 */
	//	@EventHandler(priority = EventPriority.MONITOR)
	public void onGroupBreak(GroupRemoveEvent event) {
		MinecartGroup mg = event.getGroup();
		String train = event.getGroup().getProperties().getTrainName();
		if(TrainFactory.trains.containsKey(train)) {
			TrainFactory.trains.remove(train);
		}

		for(MinecartMember<?> mm : mg) {			
			//Determine class type, and try to remove the minecart or inventory from the appropriate list.
			try {
				MinecartMemberRideable mmr = (MinecartMemberRideable) mm;
				for(List<MinecartMemberRideable> m : TrainFactory.ownerRideable.values()) {
					if(m.contains(mmr)) {
						m.remove(mmr);
						return;
					}
				}
			} catch (ClassCastException e) {
				try {
					MinecartMemberChest mmc = (MinecartMemberChest) mm;
					for(List<MinecartMemberChest> list : TrainFactory.ownerStorage.values()) {
						if(list.contains(mmc)) {
							list.remove(mmc);
							return;
						}
					}
				} catch (ClassCastException c) {}
			}
		}
	}

	/**
	 *  THIS EVENT IS NOT BEING USED AT THE MOMENT. WE NEED TO FIGURE OUT WHAT TO DO WITH BROKEN CARTS
	 * @param event
	 */
	//	@EventHandler(priority = EventPriority.MONITOR)
	public void onCartBreak(MemberRemoveEvent event) {
		MinecartMember<?> mm = event.getMember();
		String train = event.getGroup().getProperties().getTrainName();
		if(TrainFactory.trains.containsKey(train)) {
			TrainFactory.trains.remove(train);
		}

		//Determine class type, and try to remove the minecart or inventory from the appropriate list.
		try {
			MinecartMemberRideable mmr = (MinecartMemberRideable) mm;
			for(List<MinecartMemberRideable> m : TrainFactory.ownerRideable.values()) {
				if(m.contains(mmr)) {
					m.remove(mmr);
					return;
				}
			}
		} catch (ClassCastException e) {
			//If the first cast fails, we try the chest minecart cast.
			try {
				MinecartMemberChest mmc = (MinecartMemberChest) mm;
				for(List<MinecartMemberChest> list : TrainFactory.ownerStorage.values()) {
					//If the minecart is contained within the list, remove it from the list.
					if(list.contains(mmc)) {
						list.remove(mmc);
						return;
					}
				}
			} catch (ClassCastException c) {}
		}
	}

	/**
	 * Handles the interaction of each player, determines what method should be called in each case.
	 * 
	 * @param event The {@link PlayerInteractEvent} called.
	 */
	@EventHandler
	public void onInteraction(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();

		//If block is null, fail silently.
		if(block == null) return;
		//If the block type is a rail, we pass it to the method in charge of rail clicking.
		if(TrainTools.isRail(block)) onRailClick(event);
		if(block.getType().equals(Material.WALL_SIGN) || block.getType().equals(Material.SIGN) || block.getType().equals(Material.SIGN_POST)) onSignClick(event);
	}
	
	/**
	 * Controls how the player sets coordinates for the creation of trains.
	 * 
	 * @param event The {@link PlayerInteractEvent} called.
	 */	
	public void onRailClick(PlayerInteractEvent event) {

		Block block = event.getClickedBlock();

		/*The tool of choice is the blaze rod.
		 * We check to make sure the blaze rod has the appropriate itemmeta added to it.
		 */
		if(event.getItem() == null) return;
		if(!event.getItem().getItemMeta().hasLore()) return;
		ItemStack item = event.getItem();
		List<String> lore = item.getItemMeta().getLore();
		boolean isRod = false;
		for(String s : lore) {
			if(s.equalsIgnoreCase("TrainTool")) {
				isRod = true;
			}
		}

		if(isRod) {
			//Check the block type. If it isn't a rail, we don't store the value.
			if(TrainTools.isRail(block)) {

				Player p = event.getPlayer();

				//Will determine what slot in the array to put the location.
				int store = 3;

				//Checking what type of action it was
				if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
					p.sendMessage("You stored the first value!");
					store = 0;
				}
				else if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					p.sendMessage("You stored the second value!");
					store = 1;
				}

				//If this is not changed, it was not a relevant action.
				if(store == 3) return;

				//Store the player's selected location in the selections map.
				if(TrainFactory.selections.containsKey(p)) {
					TrainFactory.selections.get(p)[store] = block.getLocation();
				}
				//If the player does not have selections, instantiate one and put the variables in.
				else {
					Location[] loc = new Location[2];
					loc[store] = block.getLocation();
					TrainFactory.selections.put(p, loc);
				}
			}
		}
	}

	/**
	 * Prevents the Train Tool from breaking blocks.
	 * 
	 * @param event The {@link BlockBreakEvent} associated with the block.
	 */
	@EventHandler
	public void onRailBreak(BlockBreakEvent event) {
		try {
			if(event.getPlayer().getItemInHand().getItemMeta().getLore().contains("TrainTool")) event.setCancelled(true);
		} catch (NullPointerException e) {
			return;
		}
	}

	/**
	 * Does work with sign clicking events.
	 * 
	 * @param event The {@link Sign} click.
	 */
	public void onSignClick(PlayerInteractEvent event) {

		BlockState bs = event.getClickedBlock().getState();

		Sign sign = (Sign) bs;

		Player player = event.getPlayer();
		//These lines will be used later to determine currency, quite possibly.

		//		PlayerInventory ip = player.getInventory();
		//		ItemStack[] items = ip.getContents();		
		String[] lines = sign.getLines();

		if(lines.length != 4) return;

		//TODO Incomplete logic! Need to add in the economy stuffs.
		if(lines[0].equals("{trains}")) {
			//Make sure that the train exists in our map.
			if(TrainFactory.trains.containsKey(lines[2])) {

				if(lines[1].equalsIgnoreCase("buy_storage")) {
					player.sendMessage("You bought a cart for storage!");
					TrainFactory.addCart(player, lines[2], EntityType.MINECART_CHEST);
				}
				else if(lines[1].equalsIgnoreCase("buy_ride")) {
					player.sendMessage("You bought a cart for riding!");
					TrainFactory.addCart(player, lines[2], EntityType.MINECART);
				}
				else if(lines[1].equalsIgnoreCase("sell_storage")) {
					if(!TrainFactory.hasStoreCarts(player)) {player.sendMessage("You haven no storage carts to sell."); return; }
					player.sendMessage("You sold your cart for storage!");
					TrainFactory.removeCart(lines[2], player, EntityType.MINECART_CHEST);
				}
				else if(lines[1].equalsIgnoreCase("sell_ride")) {
					if(!TrainFactory.hasRideCarts(player)) {player.sendMessage("You haven no passenger carts to sell."); return; }
					player.sendMessage("You sold your cart for riding!");
					TrainFactory.removeCart(lines[2], player, EntityType.MINECART);
				}
			}
		}
	}

	/**
	 * This will be used to facilitate the permissions of chest minecarts.
	 * 
	 * @param event The {@link InventoryOpenEvent} associated with the chest.
	 */
	public void onChestClick(InventoryOpenEvent event) {

		event.setCancelled(true);

		Player p = (Player) event.getPlayer();
		Inventory i = event.getInventory();

		if(TrainFactory.ownerStorage.containsKey(p)) {
			for(MinecartMemberChest chest : TrainFactory.getInventoryList(p)) {
				if(chest.getEntity().getInventory() == i) {
					event.setCancelled(false);
					break;
				}
			}
		}
	}
}
