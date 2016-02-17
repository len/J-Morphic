package org.squeak.morphic.examples;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Color;
import org.squeak.morphic.kernel.Font;
import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.kernel.Point;
import org.squeak.morphic.kernel.Rectangle;

public class FractalMorph extends Morph {
	
	@Override
	protected void draw(Canvas canvas) {
		Rectangle r = canvas.getViewport().intersection(getBounds());
		canvas.setFillColor(Color.AMETHYST);
		canvas.fillEllipse(r);
		canvas.setColor(Color.BLACK);
		canvas.setFont(new Font("Monospaced", Font.NORMAL, 0.1f/canvas.getScale()));
		canvas.drawText(""+canvas.getScale(), new Point(0.1f,0.1f));
	}
	
	@Override
	public boolean contains(Point point) {
		return point.radius() <= 1;
	}
}
