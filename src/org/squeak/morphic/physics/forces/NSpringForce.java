package org.squeak.morphic.physics.forces;

import org.squeak.morphic.kernel.Point;
import org.squeak.morphic.physics.Force;
import org.squeak.morphic.physics.Particle;
import org.squeak.morphic.physics.Simulation;

public class NSpringForce implements Force {
	public float tension = 0.1f;
	public float restLength = 0.5f;
	public float damping = 0.1f;

	public NSpringForce() {
	}
	
	public NSpringForce(float tension, float restLength) {
		this.tension = tension;
		this.restLength = restLength;
	}

	public void applyTo(Simulation simulation) {
		for (Particle p1: simulation.particles) {
			Point pos1 = p1.morph.getPosition();
			for (Particle p2: simulation.particles) {
				Point pos2 = p2.morph.getPosition();
				Point delta = pos1.translatedBackBy(pos2);
				float distance = delta.norm();
				if (distance == 0) {
					delta = new Point(((float)Math.random() - 0.5f) * 0.01f, ((float)Math.random() - 0.5f) * 0.01f);
					distance = delta.norm();
				}
				float dd = distance < restLength ? restLength : distance;
		
				float k = tension * (distance - restLength);
				k += damping * (delta.x * (p1.vx - p2.vx) + delta.y * (p1.vy - p2.vy)) / dd;
				k /= dd;
				
				Point force = delta.scaledBy(-k);
				
				p1.fx += force.x;
				p1.fy += force.y;
		
				p2.fx -= force.x;
				p2.fy -= force.y;
			}
			//TODO this can be optimized reducing complexity by half, by changing the p2 loop to index of p1 to n
		}
	}
}
