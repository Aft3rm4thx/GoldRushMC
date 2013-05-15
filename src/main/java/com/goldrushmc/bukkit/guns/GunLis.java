package com.goldrushmc.bukkit.guns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import com.goldrushmc.bukkit.defaults.DefaultListener;

public class GunLis extends DefaultListener {

	public GunLis(JavaPlugin plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	public HashMap<Player, Boolean> cockHash = new HashMap<Player, Boolean>();
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRightClick(PlayerInteractEvent e) {		
		Action eAction = e.getAction();
		Player p = e.getPlayer();
		
		if(!cockHash.containsKey(p)){
			cockHash.put(p, false);
		}
		
		if (eAction.equals(Action.RIGHT_CLICK_AIR)) {
			if (p.getItemInHand().getType().equals(Material.CARROT_STICK)) {
				if (p.getItemInHand().getItemMeta().hasDisplayName()) {
					if (p.getItemInHand().getItemMeta().getDisplayName()
							.equals("Colt")) {
						int max = Material.DIAMOND_HOE.getMaxDurability();
						if (p.getItemInHand().getDurability() < 25) {
							if (cockHash.get(p)) {
								Bullet bullet = p.launchProjectile(Bullet.class);
								p.sendMessage(String.valueOf(bullet.getEntityId()));
								//p.getWorld().spawnEntity(p.getEyeLocation(), EntityBullet);
								
								p.playSound(p.getLocation(), Sound.ZOMBIE_METAL,10, -1f);
								Snowball snowball = p.launchProjectile(Snowball.class);
								snowball.setVelocity(p.getLocation().getDirection().multiply(7));
								snowball.setFallDistance(0f);
								p.getItemInHand().setDurability((short) (p.getItemInHand().getDurability() + 4));
								cockHash.put(p, false);
							} else {
								p.playSound(p.getLocation(), Sound.NOTE_BASS_DRUM,10, -1f);
							}
						} else {
							p.playSound(p.getLocation(), Sound.CLICK,5, 2f);
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
		
		if(p.isSneaking()){
			Reload(p);
		} else {
		if (eAction.equals(Action.LEFT_CLICK_AIR)) {
			if (p.getItemInHand().getType().equals(Material.CARROT_STICK)) {
				if (p.getItemInHand().getItemMeta().hasDisplayName()) {
					if (p.getItemInHand().getItemMeta().getDisplayName()
							.equals("Colt")) {
						int max = Material.DIAMOND_HOE.getMaxDurability();
						if (p.getItemInHand().getDurability() < 25) {
							cockHash.put(p, true);
							p.playSound(p.getLocation(), Sound.DOOR_OPEN, 5, 1);
						}
					}
				}
			}
		}
		}
	}
	
	public void Reload(Player p) {
		ReloadTask rt = new ReloadTask(p);
		for(int i = 0; i < 6; i++){
			Bukkit.getServer().getScheduler().runTaskLater(plugin, rt, i * 10);
		}
	}

	class ReloadTask implements Runnable{
		Player p;
		ReloadTask(Player player){
			p = player;
		}

		@Override
		public void run() {			
			if (p.getItemInHand().getType().equals(Material.CARROT_STICK)) {
				if (p.getItemInHand().getItemMeta().hasDisplayName()) {
					if (p.getItemInHand().getItemMeta().getDisplayName().equals("Colt")) {
						if (p.getItemInHand().getDurability() > 1) {
							p.playSound(p.getLocation(), Sound.CLICK,5, 2f);
							p.getItemInHand().setDurability((short) (p.getItemInHand().getDurability() - 4));
						}
					}
				}
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

	public Location getSpawnLoc(Player p) {
		if (getCardinalDirection(p) == "N") {
			Location loc = p.getEyeLocation().add(
					p.getEyeLocation().getDirection()
							.add(new Vector(0.5, 0, -0.5)));
			return loc;
		} else if (getCardinalDirection(p) == "NW") {
			Location loc = p.getEyeLocation().add(
					p.getEyeLocation().getDirection()
							.add(new Vector(-0.5, 0, -0.5)));
			return loc;
		} else if (getCardinalDirection(p) == "W") {
			Location loc = p.getEyeLocation().add(
					p.getEyeLocation().getDirection()
							.add(new Vector(-0.5, 0, 0.5)));
			return loc;
		} else if (getCardinalDirection(p) == "SW") {
			Location loc = p.getEyeLocation().add(
					p.getEyeLocation().getDirection()
							.add(new Vector(-0.5, 0, -0.5)));
			return loc;
		} else {
			Location loc = p.getEyeLocation().add(
					p.getEyeLocation().getDirection()
							.add(new Vector(0.5, 0, 0.5)));
			return loc;
		}
	}

	public static String getCardinalDirection(Player player) {
		double rotation = (player.getLocation().getYaw() - 90) % 360;
		if (rotation < 0) {
			rotation += 360.0;
		}
		if (0 <= rotation && rotation < 22.5) {
			return "N";
		} else if (22.5 <= rotation && rotation < 67.5) {
			return "NE";
		} else if (67.5 <= rotation && rotation < 112.5) {
			return "E";
		} else if (112.5 <= rotation && rotation < 157.5) {
			return "SE";
		} else if (157.5 <= rotation && rotation < 202.5) {
			return "S";
		} else if (202.5 <= rotation && rotation < 247.5) {
			return "SW";
		} else if (247.5 <= rotation && rotation < 292.5) {
			return "W";
		} else if (292.5 <= rotation && rotation < 337.5) {
			return "NW";
		} else if (337.5 <= rotation && rotation < 360.0) {
			return "N";
		} else {
			return null;
		}
	}

	public LivingEntity getTarget(Player p) {
		List<Entity> nearbyE = p.getNearbyEntities(50, 50, 50);
		ArrayList<LivingEntity> livingE = new ArrayList<LivingEntity>();

		for (Entity e : nearbyE) {
			if (e instanceof LivingEntity) {
				livingE.add((LivingEntity) e);
			}
		}

		// plugin.target = null;
		BlockIterator bItr = new BlockIterator(p, 50);
		Block block;
		Location loc;
		LivingEntity target = null;
		int bx, by, bz;
		double ex, ey, ez;
		// loop through player's line of sight
		while (bItr.hasNext()) {
			block = bItr.next();
			bx = block.getX();
			by = block.getY();
			bz = block.getZ();
			// check for entities near this block in the line of sight
			for (LivingEntity e : livingE) {
				loc = e.getLocation();
				ex = loc.getX();
				ey = loc.getY();
				ez = loc.getZ();
				if ((bx - .75 <= ex && ex <= bx + 1.75)
						&& (bz - .75 <= ez && ez <= bz + 1.75)
						&& (by - 1 <= ey && ey <= by + 2.5)) {
					// entity is close enough, set target and stop
					target = e;
					break;
				}
			}
		}
		return target;
	}
}
