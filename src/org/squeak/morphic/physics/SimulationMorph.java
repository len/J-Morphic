package org.squeak.morphic.physics;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.kernel.Point;
import org.squeak.morphic.kernel.Rectangle;
import org.squeak.morphic.physics.forces.SpringForce;

public class SimulationMorph extends Morph {

	protected final Simulation simulation = new Simulation();
	public boolean enforceBounds = false;
	
	@Override
	protected void draw(Canvas canvas) {
	}
	
	@Override
	public Rectangle getBounds() {
		if (!enforceBounds) return null;
		return super.getBounds();
	}

	@Override
	public boolean wantsSteps() {
		return true;
	}
	
	@Override
	public float stepTime() {
		return 0.05f; //50 milliseconds
	}

	@Override
	public void step(float dt) {
		fullChanged();
		simulation.step(dt);
		if (enforceBounds)
			enforceBounds();
		fullChanged();
	}
	
	private void enforceBounds() {
		for (Particle p: simulation.particles) {
			Point position = p.morph.getPosition();
			if (position.radius() > 1) {
				p.morph.setPosition(Point.fromPolar(1, position.angle()));
			}
		}
	}

	public Particle addParticle(Morph m) {
		addMorph(m);
		return simulation.addParticle(m);
	}
	
	public SpringForce addSpring(Morph m1, Morph m2) {
		return simulation.addSpring(m1, m2);
	}

	public Force addForce(Force f) {
		simulation.addForce(f);
		fullChanged();
		return f;
	}
}
