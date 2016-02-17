package org.squeak.morphic.kernel;

/**
 * A Rectangle in the Euclidean plane.
 * 
 * @see org.squeak.morphic.kernel.Point
 */
public class Rectangle {

	// the square encompassing the unit disk
	public static final Rectangle UNIT = new Rectangle(-1,-1,2,2);

	// the infinite rectangle for unbound morphs like the World
	public static final Rectangle INFINITE = new Rectangle(Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY);

	public final float x;
	public final float y;
	public final float width;
	public final float height;

	public static Rectangle encompassing(Point[] points) {
		Point topLeft = points[0];
		Point bottomRight = topLeft;
		for (int i=1; i<points.length; i++) {
			Point p = points[i];
			topLeft = topLeft.min(p);
			bottomRight = bottomRight.max(p);
		}
		return new Rectangle(topLeft.x, topLeft.y, bottomRight.x-topLeft.x, bottomRight.y-topLeft.y);
	}
	
	public Rectangle(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public boolean equals(Object object) {
		// WARNING this can be wrong because of floating point
		if (object == this) return true;
		if (!(object instanceof Rectangle)) return false;
		Rectangle r = (Rectangle)object;
		return (r.x == this.x) && (r.y == this.y) && (r.width == this.width) && (r.height == this.height);
	}

	public int hashCode() {
		return Float.floatToIntBits(x) ^ Float.floatToIntBits(y) ^ Float.floatToIntBits(width) ^ Float.floatToIntBits(height);
	}

	public boolean contains(float x, float y) {
		return (x >= this.x) && (y >= this.y) && ((x - this.x) < width) && ((y - this.y) < height);
	}
	
	public boolean contains(Point point) {
		return contains(point.x, point.y);
	}
	
	public Rectangle intersection(Rectangle rect) {
		if (this == rect) return this;
		float left = x > rect.x ? x : rect.x;
		float top = y > rect.y ? y : rect.y;
		float lhs = x + width;
		float rhs = rect.x + rect.width;
		float right = lhs < rhs ? lhs : rhs;
		lhs = y + height;
		rhs = rect.y + rect.height;
		float bottom = lhs < rhs ? lhs : rhs;
		return new Rectangle (
			right < left ? 0 : left,
			bottom < top ? 0 : top,
			right < left ? 0 : right - left,
			bottom < top ? 0 : bottom - top);
	}
	
	public boolean intersects(Rectangle rect) {
		return !intersection(rect).isEmpty();
	}

	public Rectangle union(Rectangle rect) {
		//TODO optimize to avoid creating rectangles
		float left = Math.min(x,rect.x);
		float top = Math.min(y,rect.y);
		float right = Math.max(x+width, rect.x+rect.width);
		float bottom = Math.max(y+height, rect.y+rect.height);
		return new Rectangle(left, top, right-left, bottom-top);
	}

	public Rectangle translatedBy(Point translation) {
		return new Rectangle(x+translation.x, y+translation.y, width, height);
	}

	public Rectangle translatedBackBy(Point translation) {
		return new Rectangle(x-translation.x, y-translation.y, width, height);
	}
	
	public Rectangle insetBy(float delta) {
		return new Rectangle(x+delta, y+delta, width-delta*2, height-delta*2);
	}
	
	public boolean isEmpty () {
		return (width <= 0) || (height <= 0);
	}
	
	public Point topLeft() {
		return new Point(x,y);
	}

	public Point topRight() {
		return new Point(x+width,y);
	}

	public Point bottomLeft() {
		return new Point(x,y+height);
	}
	
	public Point bottomRight() {
		return new Point(x+width,y+height);
	}

	public Point topCenter() {
		return new Point(x+width/2,y);
	}

	public Point bottomCenter() {
		return new Point(x+width/2,y+height);
	}

	public Point centerLeft() {
		return new Point(x,y+height/2);
	}

	public Point centerRight() {
		return new Point(x+width,y+height/2);
	}
	
	public Point center() {
		return new Point(x+width/2,y+height/2);
	}
	
	public String toString() {
		return "[("+x+","+y+") ("+(x+width)+","+(y+height)+")]";
	}
}
