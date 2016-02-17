package org.squeak.morphic.kernel.coordinates;

import org.squeak.morphic.kernel.CoordinateSystem;
import org.squeak.morphic.kernel.Point;

/**
 * <p>A <i>Cartesian coordinate system</i> linearly maps points in the square <code>[-1,1]x[-1,1]</code>
 * to coordinates in the box defined by <code>minX,minY,maxX,maxY</code>. The orientation depends
 * on the signs of <code>maxX-minX</code> and <code>maxY-minY</code> (e.g., <code>maxX < minX</code>
 * is allowed and it flips the plane horizontally, and <code>maxY < minY</code> flips it vertically).</p>
 * 
 * @see org.squeak.morphic.kernel.CoordinateSystem
 */
public class CartesianCoordinateSystem implements CoordinateSystem {
	private final float minX, minY, maxX, maxY;


	public static final CartesianCoordinateSystem CANONICAL = new CartesianCoordinateSystem(-1.0f, -1.0f, 1.0f, 1.0f);

	/* NOTE: the linear mapping works fine with the Equirectangular projection
	 * http://en.wikipedia.org/wiki/Equirectangular_projection
	 */
	public static final CartesianCoordinateSystem GEOGRAPHIC = new CartesianCoordinateSystem(-180, -90, 180, 90);
	
	public static final CartesianCoordinateSystem UPSIDE_DOWN = new CartesianCoordinateSystem(-1.0f, 1.0f, 1.0f, -1.0f);

	public static final CartesianCoordinateSystem MIRROR = new CartesianCoordinateSystem(1.0f, -1.0f, -1.0f, 1.0f);

	public CartesianCoordinateSystem(float minX, float minY, float maxX, float maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}
	
	public Point toCanonical(Point local) {
		float x = (local.x - minX) / (maxX - minX) * 2.0f - 1.0f;
		float y = (local.y - minY) / (maxY - minY) * 2.0f - 1.0f;
		return new Point(x,y);
	}

	public Point toLocal(Point canonical) {
		float x = (canonical.x + 1.0f) * (maxX - minX) / 2.0f + minX;
		float y = (canonical.y + 1.0f) * (maxY - minY) / 2.0f + minY;
		return new Point(x,y);
	}
}
