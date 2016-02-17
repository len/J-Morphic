package org.squeak.morphic.examples;

import java.util.Date;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Color;
import org.squeak.morphic.kernel.Font;
import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.kernel.Point;
import org.squeak.morphic.kernel.Rectangle;

public class ClockMorph extends Morph {

	@Override
	protected void draw(Canvas canvas) {
		Rectangle bounds = getBounds();
		canvas.setClipping(bounds.intersection(canvas.getClipping()));
		canvas.setColor(Color.GREEN);
		canvas.setFont(new Font("Monospaced", Font.NORMAL, bounds.height/2));
		Date date = new Date();
		canvas.drawText(date.toString(), new Point(bounds.x, bounds.y+bounds.height*0.78f));
	}

	@Override
	public boolean contains(Point point) {
		return getBounds().contains(point);
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(0,0,20,1);
	}
	
	@Override
	public void step(float dt) {
		changed();
	}

	@Override
	public boolean wantsSteps() {
		return true;
	}
}
