package org.squeak.morphic.physics;

import java.util.ArrayList;
import java.util.List;

import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.physics.forces.SpringForce;

public class Simulation {
	public final List<Particle> particles = new ArrayList<Particle>();
	public final List<Force> forces = new ArrayList<Force>();
	
	public void step(float dt) {
		for (Particle p: particles) { // speed limit
			float n = p.vx * p.vx + p.vy * p.vy;
			if (n > 1.0f) {
				p.vx /= 10*n;
				p.vy /= 10*n;
			}
		}
		
		for (Particle p: particles) {
			p.updatePosition(dt);
		}

		for (Particle p: particles) {
			p.fx = p.fy = 0;
		}

		for (Force f: forces) {
			f.applyTo(this);
		}
		
		for (Particle p: particles) {
			p.updateVelocity(dt);
		}
	}
	
	public Particle addParticle(Morph morph) {
		for (Particle p: particles) {
			if (p.morph == morph)
				return p;
		}
		Particle p = new Particle(morph);
		particles.add(p);
		return p;
	}

	public Force addForce(Force f) {
		forces.add(f);
		return f;
	}
	
	public SpringForce addSpring(Morph m1, Morph m2) {
		return (SpringForce) addForce(new SpringForce(addParticle(m1), addParticle(m2)));
	}
}
