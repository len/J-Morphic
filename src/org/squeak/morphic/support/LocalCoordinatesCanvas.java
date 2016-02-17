package org.squeak.morphic.support;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.kernel.Point;
import org.squeak.morphic.kernel.Rectangle;

public class LocalCoordinatesCanvas extends DelegatingCanvas {

	private final Morph morph;
	
	public LocalCoordinatesCanvas(Canvas canvas, Morph morph) {
		super(canvas);
		this.morph = morph;
	}

	@Override
	protected Rectangle convert(Rectangle rect) {
		return morph.toCanonical(rect);
	}

	@Override
	protected Rectangle convertBack(Rectangle rect) {
		return morph.toLocal(rect);
	}
	
	@Override
	protected float[] convert(float[] points) {
		float[] canonicalPoints = new float[points.length];
		for (int i=0; i<points.length; i += 2) {
			Point local = new Point(points[i], points[i+1]);
			Point canonical = morph.toCanonical(local);
			canonicalPoints[i] = canonical.x;
			canonicalPoints[i+1] = canonical.y;
		}
		return canonicalPoints;
	}

	@Override
	protected Point convert(Point point) {
		return morph.toCanonical(point);
	}
}
