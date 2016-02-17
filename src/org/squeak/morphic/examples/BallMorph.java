package org.squeak.morphic.examples;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Color;
import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.kernel.Point;

public class BallMorph extends Morph {

	private Color color = Color.random();
	private Point velocity = Point.random().scaledBy(0.01f);

	@Override
	protected void draw(Canvas canvas) {
		canvas.setFillColor(color);
		canvas.fillEllipse(getBounds());
		
		canvas.setColor(Color.BLACK);
		canvas.drawEllipse(getBounds());
	}

	@Override
	public void step(float dt) {
		Point p = getPosition();
		if (p.x < -1.0 || p.x > 1.0)
			velocity = new Point(velocity.x * -1, velocity.y);
		if (p.y < -1.0 || p.y > 1.0)
			velocity = new Point(velocity.x, velocity.y * -1);
		translateBy(velocity);
	}

	@Override
	public float stepTime() {
		return 0.05f;
	}
	
	@Override
	public boolean wantsSteps() {
		return true;
	}
	
	@Override
	public boolean contains(Point point) {
		return point.radius() <= 1;
	}
}
