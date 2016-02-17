package org.squeak.morphic.physics.forces;

import org.squeak.morphic.physics.Force;
import org.squeak.morphic.physics.Particle;
import org.squeak.morphic.physics.Simulation;

/**
 * This is the <i>drag</i> force (resistance, friction) experienced by particles
 * moving through a viscous fluid. This force is linearly proportional to
 * the particle velocity, but in opposite direction.
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Drag_%28physics%29#Very_low_Reynolds_numbers_.E2.80.94_Stokes.27_drag">Stokes' drag (wikipedia)</a>
 */
public class DragForce implements Force {
	public float factor;

	public DragForce() {
		this(0.1f);
	}
	
	public DragForce(float factor) {
		this.factor = factor;
	}
	
	public void applyTo(Simulation simulation) {
		if (factor == 0)
			return;
		for (Particle p: simulation.particles) {
			p.fx -= p.vx*factor;
			p.fy -= p.vy*factor;
		}
	}
}
