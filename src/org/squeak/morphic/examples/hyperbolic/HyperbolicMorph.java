package org.squeak.morphic.examples.hyperbolic;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Color;
import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.kernel.Point;

public class HyperbolicMorph extends Morph {

	public HyperbolicMorph() {
		setCoordinateSystem(new PoincareHyperbolicCoordinateSystem());
	}
	
	@Override
	protected void draw(Canvas canvas) {
		canvas.setColor(Color.BLACK);
		for (float v=-10; v<10; v+=0.5) {
			for (float u=-10; u<10; u+=0.1) {
				canvas.drawLine(new Point(u,v), new Point(u+0.1f,v));
				canvas.drawLine(new Point(v,u), new Point(v,u+0.1f));
			}
		}
	}

	public void step(float dt) {
//		rotateBy(0.5f);
//		orbitBy(-0.1f);
	}
}
