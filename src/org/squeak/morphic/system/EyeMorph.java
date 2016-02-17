package org.squeak.morphic.system;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Color;
import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.kernel.Point;
import org.squeak.morphic.kernel.Rectangle;
import org.squeak.morphic.kernel.Transformation;
import org.squeak.morphic.kernel.events.MouseClickEvent;
import org.squeak.morphic.support.DelegatingCanvas;

/**
 * Eyes render the World <i>within</i> and <i>under</i> their bounds.
 * 
 * <p>When rendering the World from the viewpoint of an eye, everything
 * under the eye bounds is drawn in Z-order from the deepest up to the EyeMorph.
 * Anything <i>above</i> the eye in Z-order is not drawn, since
 * it is considered not visible from the eye viewpoint.</p>
 * 
 * <p>Different types of Eyes can implement different kinds of renderings, for example
 * spatial distortions (fish eye), or drawing all morphs with alpha (X-ray eyes).</p>
 * 
 * <p><i>Every World should have at least one eye.</i></p>
 *
 * @see org.squeak.morphic.system.WorldMorph
 * @see org.squeak.morphic.system.HandMorph
 */
public class EyeMorph extends Morph {
	
	private Rectangle changedArea = Rectangle.UNIT;

	@Override
	protected void draw(Canvas canvas) {
/*		canvas.setFillColor(Color.TURQUOISE);
		canvas.setAlpha(0.2f);
		canvas.fillRectangle(getBounds());
		canvas.setAlpha(1.0f);
*/		canvas.setColor(Color.DARK_GREEN);
		canvas.drawRectangle(getBounds());
		
/*		Rectangle originalClipping = canvas.getClipping();
		canvas.setClipping(getBounds());
		
		Transformation originalTransformation = canvas.getTransformation();

		drawWorld(new ShadowCanvas(canvas));
		
		canvas.setAlpha(1.0f);
		canvas.setTransformation(originalTransformation);
		canvas.setClipping(getBounds());
		
		drawWorld(canvas);
		
		canvas.setClipping(originalClipping);
*/	}

	@Override
	public boolean contains(Point point) {
		return getBounds().contains(toCanonical(point));
	}

	/**
	 * Notice that the given World area has changed and needs to be redrawn.
	 * @param rect area that have changed and needs redrawing
	 */
	public void invalidate(Rectangle rect) {
		if (rect != null) {
			rect = toInner(rect);
			rect = rect.intersection(getBounds()); // do this here or should it be done in WorldMorph?
			changedArea = changedArea == null ? rect : rect.union(changedArea);
		} else {
			changedArea = getBounds();
		}
	}

	/**
	 * Returns the rectangular area within the eye space that has changed and needs to be redrawn.
	 * It might have changed due to the passage of time (stepping), or due to user-input events
	 * (mouse or keyboard).
	 * 
	 * @return the rectangular area within the eye space that has changed and needs to be redrawn
	 */
	public Rectangle getChangedArea() {
		return changedArea;
	}
	
	/**
	 * Draw the World from the viewpoint of this eye. Everything under the eye's bounds
	 * is drawn on the canvas, in Z-order from the deepest up to the eye, including the
	 * eye submorphs. Anything strictly <i>in front</i> of the eye in Z-order
	 * is not drawn, since it is considered not visible from the eye viewpoint.
	 * 
	 * @param canvas the Canvas to draw on
	 */
	public void drawWorld(Canvas canvas) {
		Transformation outerTransformation = canvas.getTransformation();
		canvas.setTransformation(outerTransformation.with(transformation.inverse()));
		if (changedArea != null) {
//			canvas.setColor(Color.random());
//			canvas.drawRectangle(changedArea.insetBy(-0.01f));
//			canvas.setClipping(changedArea.insetBy(-0.01f));//FIXME
		}
		changedArea = null;
		(new DelegatingCanvas(canvas) {
			boolean draw = true;
			
			@Override
			public void drawMorph(Morph morph) {
				/* draw until this eye is reached, and then stop drawing
				 * (since everything else is above it in Z-order and thus
				 * not visible from this eye point of view)
				 */
				if (morph == EyeMorph.this) {
					draw = false;
				} else if (draw) {
					super.drawMorph(morph);
				}
			}
			
		}).drawMorph(getWorld());

		canvas.setTransformation(outerTransformation);
		drawSubmorphs(canvas);
	}
	
	public boolean handleEvent(MouseClickEvent e) {
		return false;
	}
}
