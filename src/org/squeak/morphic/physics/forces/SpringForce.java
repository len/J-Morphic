package org.squeak.morphic.physics.forces;

import org.squeak.morphic.kernel.Point;
import org.squeak.morphic.physics.Force;
import org.squeak.morphic.physics.Particle;
import org.squeak.morphic.physics.Simulation;

/**
 * This is the force of a spring joining two particles, per <i>Hooke's law</i> of elasticity.
 * The spring force is defined by a tension constant, a rest length and a damping factor.
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Hooke's_law">Hooke's law (wikipedia)</a>
 */
public class SpringForce implements Force {
	public float tension = 0.1f; // spring constant or rate
	public float restLength = 0.5f;
	public float damping = 0.1f;
	public final Particle p1, p2;

	public SpringForce(Particle p1, Particle p2) {
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public void applyTo(Simulation simulation) {
		Point pos1 = p1.morph.getPosition();
		Point pos2 = p2.morph.getPosition();
		Point delta = pos1.translatedBackBy(pos2);
		float distance = delta.norm();
		if (distance == 0) {
			delta = new Point(((float)Math.random() - 0.5f) * 0.01f, ((float)Math.random() - 0.5f) * 0.01f);
			distance = delta.norm();
		}

//		float k = tension * (distance - restLength) / distance;
//		k += damping * (delta.x * (p1.vx - p2.vx) + delta.y * (p1.vy - p2.vy)) / distance;

		float dd = distance < restLength ? restLength  : distance;
		
		float k = tension * (distance - restLength);
		k += damping * (delta.x * (p1.vx - p2.vx) + delta.y * (p1.vy - p2.vy)) / dd;
		k /= dd;
		
		Point force = delta.scaledBy(-k);
		
		p1.fx += force.x;
		p1.fy += force.y;
		
		p2.fx -= force.x;
		p2.fy -= force.y;
	}
}
