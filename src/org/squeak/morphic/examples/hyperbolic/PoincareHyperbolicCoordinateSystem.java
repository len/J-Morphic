package org.squeak.morphic.examples.hyperbolic;

import org.squeak.morphic.kernel.CoordinateSystem;
import org.squeak.morphic.kernel.Point;

/*
 * This is the Poincare disk model of hyperbolic geometry.
 */
public class PoincareHyperbolicCoordinateSystem implements CoordinateSystem {

	public Point toCanonical(Point local) {
		// Klein-Beltrami coordinates
		float theta = local.angle();
		float r = local.radius();
		double tanhr = Math.tanh(r);
		float x = (float)(tanhr*Math.cos(theta));
		float y = (float)(tanhr*Math.sin(theta));
		
		// convert to Poincare coordinates
		float t = (float)(1 + (Math.sqrt(1-x*x-y*y)));
		x = x/t;
		y = y/t;
		
		if (Float.isNaN(x))
			System.out.println("nan converting "+local+" "+t+" "+x+" "+y);
		return new Point(x, y);
	}

	public Point toLocal(Point canonical) {
		//TODO
		return new Point(argtanh(canonical.x),argtanh(canonical.y));
	}
	
	private float argtanh(float x) {
		return (float) (Math.log((1+x)/(1-x)) / 2.0);
	}
}
