package org.squeak.morphic.examples;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Color;
import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.kernel.Point;

public class EllipseMorph extends Morph {

	private Color color = Color.YELLOW;

	@Override
	protected void draw(Canvas canvas) {
		canvas.setFillColor(color);
		canvas.fillEllipse(getBounds());
		
		canvas.setColor(Color.BLACK);
		canvas.drawEllipse(getBounds());
	}

	@Override
	public boolean contains(Point point) {
		return point.radius() <= 1;
	}
}
