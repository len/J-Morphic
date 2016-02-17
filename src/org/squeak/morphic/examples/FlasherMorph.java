package org.squeak.morphic.examples;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Color;
import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.kernel.Point;
import org.squeak.morphic.kernel.events.MouseUpEvent;

public class FlasherMorph extends Morph {

	private boolean isOn;
	
	@Override
	protected void draw(Canvas canvas) {
		canvas.setFillColor(isOn ? Color.RED : Color.DARK_RED);
		canvas.fillEllipse(getBounds());

		canvas.setColor(Color.BLACK);
		canvas.drawEllipse(getBounds());
	}

	@Override
	public boolean wantsSteps() {
		return true;
	}
	
	@Override
	public float stepTime() {
		return 0.5f;
	}

	@Override
	public void step(float dt) {
		isOn = !isOn;
		changed();
	}

	@Override
	public boolean handleEvent(MouseUpEvent e) {
		step(0);
		return true;
	}
	
	@Override
	public boolean contains(Point point) {
		return point.radius() <= 1;
	}
}
