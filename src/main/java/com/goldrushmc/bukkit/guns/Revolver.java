package com.goldrushmc.bukkit.guns;

import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;

public class Revolver {
	Player p;

	public HashMap<Player, Boolean> cockHash = new HashMap<Player, Boolean>();
	public HashMap<Player, Boolean> hasReloadedHash = new HashMap<Player, Boolean>();
	public GunTools gunTools = new GunTools();

	public Revolver(Player player) {
		p = player;
	}

	public void fire() {
		if (p.getItemInHand().getDurability() < 31) {
			if (cockHash.get(p)) {
				
				//gun fire sound
				p.playSound(p.getLocation(), Sound.ZOMBIE_METAL,1, -3f);
				
				//gun fire smoke effect
				Location smokePos = gunTools.getSpawnLoc(p);
				p.playEffect(smokePos, Effect.SMOKE, 0);
				
				//smoke and gun for other players
				List<Player> plList = gunTools.getPlayersWithin(p, 50);
				double distance = 0;
				for(int i = 0; i < plList.size();i++) {
					plList.get(i).playEffect(smokePos, Effect.SMOKE, 0);
					distance = plList.get(i).getLocation().distance(p.getLocation());
					if(distance <=50 && distance > 0){
						plList.get(i).playSound(smokePos, Sound.ZOMBIE_METAL, (float)  (1 - (distance / 50)), -3f);
					}
				}
				
				Snowball snowball = p.launchProjectile(Snowball.class);
				snowball.setVelocity(p.getLocation().getDirection().multiply(5));
				
				p.getItemInHand().setDurability((short) (p.getItemInHand().getDurability() + 6));
				cockHash.put(p, false);
			} else {
				p.playSound(p.getLocation(), Sound.NOTE_BASS_DRUM,10, -1f);
			}
		} else {
			p.playSound(p.getLocation(), Sound.CLICK,5, 2f);
		}
	}

	public void reload() {
		hasReloadedHash.put(p, false);
		if (p.getItemInHand().getDurability() > 1 && p.getItemInHand().getDurability() < 32) {
			for (int i = 0; i < 36; i++) {
				if (p.getInventory().getItem(i) != null) {
					if (p.getInventory().getItem(i).getTypeId() == 332) {
						if (p.getInventory().getItem(i).getAmount() == 1) {
							p.getInventory().clear(i);
						} else {
							p.getInventory().getItem(i).setAmount(p.getInventory().getItem(i).getAmount() - 1);
						}
						p.playSound(p.getLocation(), Sound.CLICK, 5, 2f);
						p.getItemInHand().setDurability((short) (p.getItemInHand().getDurability() - 6));
						hasReloadedHash.put(p, true);
						break;
					}
				}
			}
			if (!hasReloadedHash.get(p)) {
				p.sendMessage(ChatColor.DARK_RED + "Out of Ammo!");
			}
		}
	}
	
	public void cock() {
		if (p.getItemInHand().getDurability() < 32) {
			cockHash.put(p, true);
			p.playSound(p.getLocation(), Sound.DOOR_OPEN, 5, 1);
		}		
	}		
}
