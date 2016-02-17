package org.squeak.morphic.physics.forces;

import org.squeak.morphic.kernel.Point;
import org.squeak.morphic.physics.Force;
import org.squeak.morphic.physics.Particle;
import org.squeak.morphic.physics.Simulation;

/**
 * This is a force that applies to all particles of a simulation, either attracting
 * them to one another or pulling them apart from each other (depending on the
 * sign of <code>g</code>), following the <i>Inverse-square law</i>.
 * 
 * <p>This force is appropriate to simulate gravitation between massive particles
 * (positive <code>g</code>), as well as the electrostatic force (Coulomb's law)
 * that pulls them apart (in this case the <code>mass</code> of the particle
 * is interpreted as <i>electrostatic charge</i>).</p>
 *
 * @see <a href="http://en.wikipedia.org/wiki/Inverse-square_law">Inverse-square law (wikipedia)</a>
 */
public class NBodyForce implements Force {
	public float g = -0.01f;
	public float maxDistance = 1.0f;
	public float minDistance = 0.1f;

	public NBodyForce() {
	}
	
	public NBodyForce(float g, float maxDistance) {
		this.g = g;
		this.maxDistance = maxDistance;
	}
	
	public void applyTo(Simulation simulation) {
		for (Particle p1: simulation.particles) {
			Point pos1 = p1.morph.getPosition();
			Point force = Point.O;
			for (Particle p2: simulation.particles) {
				if (p2 == p1) continue;
				Point pos2 = p2.morph.getPosition();
				Point delta = pos2.translatedBackBy(pos1);
				float distance = delta.norm();
				if (distance < maxDistance) {
					Point direction = delta.scaledBy(1.0f / distance);
					if (distance < minDistance)
						distance = minDistance;
					force = force.translatedBy(direction.scaledBy(g * p1.mass * p2.mass / (distance * distance)));
				}
			}
			p1.fx += force.x;
			p1.fy += force.y;
			//TODO if here we update p2 force too, the second loop can be from the index of p1 + 1 to n, complexity is halved
		}
	}
}
