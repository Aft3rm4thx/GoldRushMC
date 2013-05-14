package com.goldrushmc.bukkit.guns;

import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftProjectile;
import org.bukkit.entity.EntityType;
import com.goldrushmc.bukkit.guns.EntityBullet;

public class Bullet extends CraftProjectile{

	public Bullet(CraftServer server, EntityBullet entity) {
		super(server, entity);
	}

	@Override
	public EntityType getType() {
		return EntityType.SNOWBALL;
	}

	public EntityBullet getHandle() {
		return (EntityBullet) entity;
	}
}
