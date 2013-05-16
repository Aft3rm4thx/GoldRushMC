package com.goldrushmc.bukkit.guns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import com.bergerkiller.bukkit.common.events.EntityMoveEvent;
import com.goldrushmc.bukkit.defaults.DefaultListener;
import com.goldrushmc.bukkit.defaults.DefaultListener;


public class GunLis extends DefaultListener {

	public GunLis(JavaPlugin plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	public HashMap<Player, Revolver> revolverHash = new HashMap<Player, Revolver>();

	public HashMap<Entity, Vector> velocityHash = new HashMap<Entity, Vector>();
	public GunTools gunTools = new GunTools();
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRightClick(PlayerInteractEvent e) {
		Action eAction = e.getAction();
		Player p = e.getPlayer();
		if(eAction.equals(Action.RIGHT_CLICK_AIR) || eAction.equals(Action.RIGHT_CLICK_BLOCK)) {
			
			//Revolver
			if (p.getItemInHand().getType().equals(Material.GOLD_AXE)) {
				if (p.getItemInHand().getItemMeta().hasDisplayName()) {
					if (p.getItemInHand().getItemMeta().getDisplayName().equals("Colt 1851")) {
						if(!revolverHash.containsKey(p)){
							Revolver revolver = new Revolver(p);
							revolverHash.put(p, revolver);
						} else {
							revolverHash.get(p).fire();
						}
					}
				}
			}
			
		}		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLeftClick(PlayerInteractEvent e) {		
		Action eAction = e.getAction();
		Player p = e.getPlayer();
		if(eAction.equals(Action.LEFT_CLICK_AIR) || eAction.equals(Action.LEFT_CLICK_BLOCK)) {
			if(p.isSneaking()) {
				//Revolver
				if (p.getItemInHand().getType().equals(Material.GOLD_AXE)) {
					if (p.getItemInHand().getItemMeta().hasDisplayName()) {
						if (p.getItemInHand().getItemMeta().getDisplayName().equals("Colt 1851")) {
							if(!revolverHash.containsKey(p)){
								Revolver revolver = new Revolver(p);
								revolverHash.put(p, revolver);
							} else {
								revolverHash.get(p).reload();
							}
						}
					}
				}	
			} else {
				//Revolver
				if (p.getItemInHand().getType().equals(Material.GOLD_AXE)) {
					if (p.getItemInHand().getItemMeta().hasDisplayName()) {
						if (p.getItemInHand().getItemMeta().getDisplayName().equals("Colt 1851")) {
							if(!revolverHash.containsKey(p)){
								Revolver revolver = new Revolver(p);
								revolverHash.put(p, revolver);
							} else {
								revolverHash.get(p).cock();
							}
						}
					}
				}	
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void OnEntityMove(EntityMoveEvent e) {
		if(e.getEntityType().equals(EntityType.SNOWBALL)){ 
			if(!velocityHash.containsKey(e.getEntity())){
				velocityHash.put(e.getEntity(), e.getEntity().getVelocity());
			} else {
				e.getEntity().setVelocity(velocityHash.get(e.getEntity()));
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent e) {
		if (e.getCause().equals(DamageCause.PROJECTILE)) {
			if (e.getDamage() == 0) {
				e.setDamage(8);
			}
		}
	}
}
