package org.squeak.morphic.examples;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.kernel.Point;

public class LinePlotMorph extends Morph {

	@Override
	protected void draw(Canvas canvas) {
		Point lastPoint = Point.O;
		for (float t=0; t<1; t+=0.01) {
			Point p = new Point(t,function(t));
			System.out.println(p);
			canvas.drawLine(lastPoint, p);
			lastPoint = p;
		}
	}
	
	public float function(float t) {
		return (float) Math.cos(t);
	}
}
