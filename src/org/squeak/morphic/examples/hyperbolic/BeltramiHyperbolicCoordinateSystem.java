package org.squeak.morphic.examples.hyperbolic;

import org.squeak.morphic.kernel.CoordinateSystem;
import org.squeak.morphic.kernel.Point;
import org.squeak.morphic.kernel.coordinates.PolarCoordinateSystem;

/*
 * This is the Klein-Beltrami model of hyperbolic geometry.
 */
public class BeltramiHyperbolicCoordinateSystem implements CoordinateSystem {

	static {
		CoordinateSystem beltrami = new BeltramiHyperbolicCoordinateSystem();
		CoordinateSystem poincare = new PoincareHyperbolicCoordinateSystem();
		CoordinateSystem polar = new PolarCoordinateSystem();
		Point p = new Point(0.78f, 0.51f);
		System.out.println("axial = "+p);
		System.out.println("polar = "+polar.toLocal(poincare.toCanonical(p)));
		System.out.println("beltrami = "+beltrami.toCanonical(p));
		System.out.println("beltrami back = "+beltrami.toLocal(beltrami.toCanonical(p)));
		System.out.println("poincare = "+poincare.toCanonical(p));
		System.out.println("poincare back = "+poincare.toLocal(poincare.toCanonical(p)));
	}
	
	public Point toCanonical(Point local) {
		return new Point((float)Math.tanh(local.x), (float)Math.tanh(local.y));
	}

	public Point toCanonical2(Point local) {
		float theta = local.angle();
		float r = local.radius();
		double tanhr = Math.tanh(r);
		return new Point((float)(tanhr*Math.cos(theta)), (float)(tanhr*Math.sin(theta)));
	}
	
	public Point toLocal(Point canonical) {
		return new Point(argtanh(canonical.x),argtanh(canonical.y));
	}
	
	private float argtanh(float x) {
		return (float) (Math.log((1+x)/(1-x)) / 2.0);
	}
}
