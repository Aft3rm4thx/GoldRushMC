package com.goldrushmc.bukkit.guns;

import net.minecraft.server.v1_5_R3.EntitySnowball;
import net.minecraft.server.v1_5_R3.World;

public class EntityBullet extends EntitySnowball {

	public EntityBullet(World arg0) {
		super(arg0);
		a(1.0f, 1.0f);
	}


	public void h_() {
		this.fallDistance = 0f;
	}
}
