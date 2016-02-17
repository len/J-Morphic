package org.squeak.morphic.kernel;


/**
 * A Point in the Euclidean plane.
 * 
 * @see org.squeak.morphic.kernel.Transformation
 */
public class Point {
	public final float x;
	public final float y;

	public static final Point O = new Point(0,0); // the origin (0,0)

	/**
	 * Return a random point in the closed unit disk.
	 * 
	 * @return a point inside the circle of radius 1 centered at (0,0)
	 */
	public static Point random() {
		return Point.fromPolar((float)Math.random(),(float)(Math.random()*Math.PI*2));
	}
	
	public static Point fromPolar(float r, float theta) {
		float cos = (float) Math.cos(theta);
		float sin = (float) Math.sin(theta);
		return new Point(r * cos, r * sin);
	}
	
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public int hashCode() {
		return Float.floatToIntBits(x) ^ Float.floatToIntBits(y);
	}
	
	public boolean equals(Object o) {
		// WARNING this can be wrong because of floating point
		if (o instanceof Point)
			return ((Point)o).x == x && ((Point)o).y == y;
		return false;
	}
	
	public boolean isInfinite() {
		return Float.isInfinite(x) || Float.isInfinite(y);
	}

	public boolean isNaN() {
		return Float.isNaN(x) || Float.isNaN(y);
	}

	public Point min(Point p) {
		return new Point(Math.min(x, p.x), Math.min(y, p.y));
	}

	public Point max(Point p) {
		return new Point(Math.max(x, p.x), Math.max(y, p.y));
	}

	/* products */
	
	// dot product
	public float dot(Point p) {
		return x * p.x + y * p.y;
	}

	// cross product
	public float cross(Point p) {
		return x * p.y - y * p.x;
	}

	/* metric and norm */

	public float distance2(Point p) {
		float x = this.x-p.x;
		float y = this.y-p.y;
		return (x*x) + (y*y);
	}

	public float distance(Point p) {
		return (float)Math.sqrt(distance2(p));
	}

	public float norm2() {
		return x*x + y*y;
	}

	public float norm() {
		return (float)Math.sqrt(norm2());
	}
	
	/* transformations */
	
	public Point translatedBy(float x, float y) {
		return new Point(this.x+x, this.y+y);
	}
	
	public Point translatedBy(Point translation) {
		return translatedBy(translation.x, translation.y);
	}

	public Point translatedBackBy(Point translation) {
		return new Point(x-translation.x, y-translation.y);
	}

	public Point scaledBy(float scalar) {
		return new Point(x*scalar, y*scalar);
	}
	
	public Point scaledBy(Point scale) {
		return new Point(x*scale.x, y*scale.y);
	}

	public Point rotatedBy(float theta) {
		float sin = (float) Math.sin(theta);
		float cos = (float) Math.cos(theta);
		return new Point(x * cos - y * sin, y * cos + x * sin);
	}

	/* polar coordinates */
	
	public float radius() {
		return norm();
	}

	public float angle() {
		// return the angle between -Pi and Pi
		return (float) Math.atan2(y, x);
	}
	
	/* rectangles */
	
	public Rectangle to(Point corner) {
		Point topLeft = min(corner);
		Point bottomRight = max(corner);
		return new Rectangle(topLeft.x, topLeft.y, bottomRight.x-topLeft.x, bottomRight.y-topLeft.y);
	}

	//NOTE: extent must be positive
/*	public Rectangle extent(Point extent) {
		return new Rectangle(x, y, extent.x, extent.y);
	}
*/	
	/* printing */
	
	public String toString() {
		return "("+x+", "+y+")";
	}
}
