package com.dmillerw.remoteIO.client.fx;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class FXParticlePath extends EntityFX {

	public double tx;
	public double ty;
	public double tz;
	
	public float speed;

	public FXParticlePath(World world, double sx, double sy, double sz, double tx, double ty, double tz, float speed) {
		super(world, sx, sy, sz);
		
		this.tx = tx;
		this.ty = ty;
		this.tz = tz;
		this.speed = speed;
	}
	
	public FXParticlePath(World world, TileEntity start, TileEntity end, float speed) {
		this(world, start.xCoord + 0.5, start.yCoord + 0.5, start.zCoord + 0.5, end.xCoord + 0.5, end.yCoord + 0.5, end.zCoord + 0.5, speed);
	}
	
	public FXParticlePath(World world, TileEntity start, double tx, double ty, double tz, float speed) {
		this(world, start.xCoord + 0.5, start.yCoord + 0.5, start.zCoord + 0.5, tx, ty, tz, speed);
	}
	
	public void onUpdate() {
		Vec3 start = Vec3.createVectorHelper(posX, posY, posZ);
		Vec3 end = Vec3.createVectorHelper(tx, ty, tz);
		Vec3 direction = start.subtract(end);
		direction = direction.normalize();

		if (start.distanceTo(end) >= 1) {
			this.particleAge = 10;
		}
		
		this.motionX = direction.xCoord * this.speed;
		this.motionY = direction.yCoord * this.speed;
		this.motionZ = direction.zCoord * this.speed;

		this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
		
		super.onUpdate();
	}
	
}
