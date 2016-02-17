package org.squeak.morphic.physics.forces;

import org.squeak.morphic.kernel.Point;
import org.squeak.morphic.physics.Force;
import org.squeak.morphic.physics.Particle;
import org.squeak.morphic.physics.Simulation;

public class GravityForce implements Force {
	public Point g = new Point(0, 0.01f);

	public void applyTo(Simulation simulation) {
		for (Particle p: simulation.particles) {
			p.fx = p.fx+g.x*p.mass;
			p.fy = p.fy+g.y*p.mass;
		}
	}
}
