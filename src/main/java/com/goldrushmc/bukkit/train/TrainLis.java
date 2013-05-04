package com.goldrushmc.bukkit.train;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.tc.events.GroupLinkEvent;
import com.goldrushmc.bukkit.defaults.DefaultListener;
import com.goldrushmc.bukkit.train.util.TrainTools;

public class TrainLis extends DefaultListener {

	public TrainLis(JavaPlugin plugin) {
		super(plugin);
	}

		/**
		 * Checks to make sure that the groups connecting are of the same train, and if they aren't, cancel the event.
		 * 
		 * @param event The {@link GroupLinkEvent} in question.
		 */
		@EventHandler(priority = EventPriority.HIGHEST)
		public void onCartLink(GroupLinkEvent event) {
			
			event.setCancelled(true);
			
			String train1 = event.getGroup1().getProperties().getTrainName();
			String train2 = event.getGroup2().getProperties().getTrainName();
			
			if(train1.equals(train2)) {
				event.setCancelled(false);
			}
		}		

	/**
	 * Controls how the player sets coordinates for the creation of trains.
	 * 
	 * @param event The {@link PlayerInteractEvent} called.
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onRailClick(PlayerInteractEvent event) {

		Block block = event.getClickedBlock();
		
		//If the block is null, fail silently.
		if(block == null) return;


		/*The tool of choice is the blaze rod.
		* We check to make sure the blaze rod has the appropriate meta added to it.
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


				if(TrainFactory.selections.containsKey(p)) {
					TrainFactory.selections.get(p)[store] = block.getLocation();
				}
				else {
					Location[] loc = new Location[2];
					loc[store] = block.getLocation();
					TrainFactory.selections.put(p, loc);
				}

			}
		}
	}
}
