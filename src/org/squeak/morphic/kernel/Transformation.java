package org.squeak.morphic.kernel;


/**
 * <p>Instances of Transformation are <i>Affine Transformations</i> in the Euclidean plane.
 * Examples include translations, rotations, reflections, scaling and shear.</p>
 * 
 * <p>They can be composed, and the composition is also an affine transformation. However
 * composition is non-commutative (for example, translate-and-scale is not the same as
 * scale-and-translate).</p>
 * 
 * <p>Geometrically, affine transformations in Euclidean space are <i>collinear</i> (parallelism
 * is preserved). Rigid motions like translations, rotations and reflections (and any
 * arbitrary composition of them) are <i>isometries</i> (they preserve distances). Isometries
 * plus homogeneous scaling are often called <i>similarity transformations</i> and they
 * are <i>conformal</i> (they preserve angles).</p>
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Affine_transformation">Affine Transformations (wikipedia)</a>
 * @see <a href="http://en.wikipedia.org/wiki/Euclidean_plane_isometry">Euclidean Plane Isometry (wikipedia)</a>
 * 
 * @see org.squeak.morphic.kernel.Point
 *
 */
public class Transformation {
	
	public static final Transformation IDENTITY = new Transformation(1, 0, 0, 1, 0, 0);
	
	private static final int M00 = 0, M10 = 1, M01 = 2, M11 = 3, M02 = 4, M12 = 5;
	private static final int DX = M02, DY = M12;
	
	public final float[] m;
	
	private Transformation inverse;

	
	public Transformation(float[] elements) {
		this.m = elements;
	}

	public Transformation(float m00, float m10, float m01, float m11, float dx, float dy) {
		this(new float[] {m00, m10, m01, m11, dx, dy});
	}
	
	public static Transformation translation(float dx, float dy) {
		return new Transformation(1, 0, 0, 1, dx, dy);
	}

	public static Transformation rotation(float theta) {
		float cos = (float) Math.cos(theta);
		float sin = (float) Math.sin(theta);
		return new Transformation(cos, sin, -sin, cos, 0, 0);
	}

	public static Transformation shear(float shx, float shy) {
		return new Transformation(1, shy, shx, 1, 0, 0);
	}
	
	public static Transformation scale(float factor) {
		return scale(factor, factor);
	}
	
	public static Transformation scale(float sx, float sy) {
		return new Transformation(sx, 0, 0, sy, 0, 0);
	}

	/**
	 * Return the reflection through the origin.
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/Reflection_through_the_origin">Reflection through the origin (wikipedia)</a>
	 */
	public static Transformation reflection() {
		return new Transformation(-1, 0, 0, -1, 0, 0);
	}

	public static Transformation reflection(float theta) {
		float cos = (float) Math.cos(theta);
		float sin = (float) Math.sin(theta);
		return new Transformation(cos, sin, sin, -cos, 0, 0);
	}

	public float determinant() {
		return m[M00] * m[M11] - m[M10] * m[M01];
	}

	/**
	 * Compose the receiver with the transformation given as an argument.
	 * 
	 * <p>For all <code>x</code>, the composition of <code>T</code> and <code>U</code> in <code>x</code> is equivalent to <code>T(U(x))</code>.</p>
	 * 
	 * @param transformation the Transformation to compose the receiver with
	 * @return a new Transformation that is the composition of the receiver with the argument
	 */
	public Transformation with(Transformation transformation) {
		if (this == IDENTITY) return transformation;
		if (transformation == IDENTITY) return this;
		
		float[] t = transformation.m;
		float m0 = m[M00];
		float m1 = m[M01];
		float m00 = t[M00] * m0 + t[M10] * m1;
		float m01 = t[M01] * m0 + t[M11] * m1;
		float dx = t[DX] * m0 + t[DY] * m1 + m[DX];

		m0 = m[M10];
		m1 = m[M11];
		float m10 = t[M00] * m0 + t[M10] * m1;
		float m11 = t[M01] * m0 + t[M11] * m1;
		float dy = t[DX] * m0 + t[DY] * m1 + m[DY];
		
		return new Transformation(m00, m10, m01, m11, dx, dy);
	}

	public Transformation inverse() {
		if (inverse == null) {
			float det = determinant();
			if (Math.abs(det) <= Float.MIN_VALUE) {
					throw new RuntimeException("Non-invertible transformation "+this);
		    }
			inverse = new Transformation( m[M11] / det, -m[M10] / det,
					-m[M01] / det,  m[M00] / det,
					(m[M01] * m[M12] - m[M11] * m[M02]) / det,
					(m[M10] * m[M02] - m[M00] * m[M12]) / det);
			inverse.inverse = this;
		}
		return inverse;
	}
	
	public float getTranslationX() {
		return m[DX];
	}

	public float getTranslationY() {
		return m[DY];
	}

	public Transformation getLinearTransformation() {
		return translatedBy(-getTranslationX(), -getTranslationY());
	}

	public Transformation translatedBy(float dx, float dy) {
		return new Transformation(m[M00], m[M10], m[M01], m[M11], m[DX]+dx, m[DY]+dy);
	}

	public Transformation scaledBy(float sx, float sy) {
		return with(Transformation.scale(sx, sy));
	}

	public Transformation scaledBy(float scalar) {
		return with(Transformation.scale(scalar, scalar));
	}

	public Transformation rotatedBy(float theta) {
		return with(Transformation.rotation(theta));
	}
	
	public Transformation interpolate(Transformation target, float lambda) {
		float t[] = new float[m.length];
		for (int i=0; i<m.length; i++)
			t[i] = m[i] * (1-lambda) + target.m[i] * lambda;
		return new Transformation(t);
	}

	public Point applyTo(Point p) {
		return applyTo(p.x, p.y);
	}

	public Point applyTo(float x, float y) {
		return new Point(m[M00]*x + m[M01]*y + m[DX], m[M10]*x + m[M11]*y + m[DY]);
	}
	
	public void applyTo(float[] points) {
		for (int i=0; i<points.length; i+=2) {
			float x = points[i];
			float y = points[i+1];
			points[i] = m[M00]*x + m[M01]*y + m[DX];
			points[i+1] = m[M10]*x + m[M11]*y + m[DY];
		}
	}

	public String toString() {
		return getClass().getSimpleName()+" {" + m[0] + "," + m[1] + "," +m[2] + "," +m[3] + "," +m[4] + "," +m[5] + "}";
	}
}
