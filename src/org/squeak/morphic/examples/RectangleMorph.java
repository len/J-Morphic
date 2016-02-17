package org.squeak.morphic.examples;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Color;
import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.kernel.Point;
import org.squeak.morphic.system.HandMorph;

public class RectangleMorph extends Morph {

	private Color color = Color.GREEN;
	
	@Override
	protected void draw(Canvas canvas) {
		canvas.setFillColor(color);
		canvas.fillRectangle(getBounds());

		canvas.setColor(Color.BLACK);
		canvas.drawRectangle(getBounds());
	}
	
	@Override
	public boolean contains(Point point) {
		return point.x >= -1 && point.x <= 1 && point.y >= -1 && point.y <= 1;
	}

	public void setColor(Color color) {
		this.color = color;
		changed();
	}
	
	@Override
	public void handleMouseEnter(HandMorph hand) {
		setColor(Color.HOT_PINK);
	}

	@Override
	public void handleMouseLeave(HandMorph hand) {
		setColor(Color.YELLOW);
	}
}
