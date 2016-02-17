package org.squeak.morphic.kernel.coordinates;

import org.squeak.morphic.kernel.CoordinateSystem;
import org.squeak.morphic.kernel.Point;

/**
 * <p>The <i>Polar coordinate system</i> maps points in the unit disk to coordinates in <code>[0,1]x[-Pi,Pi)</code>.</p>
 * 
 * @see org.squeak.morphic.kernel.CoordinateSystem
 */
public class PolarCoordinateSystem implements CoordinateSystem {

	public Point toCanonical(Point local) {
		return Point.fromPolar(local.x, local.y);
	}

	public Point toLocal(Point canonical) {
		float r = canonical.radius();
		float theta = canonical.angle();
		return new Point(r,theta);
	}
}
