package org.squeak.morphic.physics;

import org.squeak.morphic.kernel.Morph;

public class Particle {
	public float vx, vy;
	public float _vx, _vy;
	public float fx, fy;
	public float mass = 0.15f;

	public final Morph morph;
	
	public Particle(Morph morph) {
		this.morph = morph;
	}
	
	public void updatePosition(float dt) {
		float dt1 = dt/2;
		float dt2 = dt*dt/2;
		float ax = fx / mass;
		float ay = fy / mass;
		morph.translateBy(vx*dt + ax*dt2, vy*dt + ay*dt2);
		_vx = vx+ax*dt1;
		_vy = vy+ay*dt1;
	}
	
	public void updateVelocity(float dt) {
		float dt1 = dt/2;
//		float dt2 = dt*dt/2;
		float ax = dt1 / mass;
		vx = _vx + fx*ax;
		vy = _vy + fy*ax;
	}
}
