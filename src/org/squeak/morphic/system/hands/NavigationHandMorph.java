package org.squeak.morphic.system.hands;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Color;
import org.squeak.morphic.kernel.Point;
import org.squeak.morphic.kernel.Transformation;
import org.squeak.morphic.kernel.events.KeyEvent;
import org.squeak.morphic.kernel.events.MouseDownEvent;
import org.squeak.morphic.kernel.events.MouseEvent;
import org.squeak.morphic.kernel.events.MouseMoveEvent;
import org.squeak.morphic.kernel.events.MouseUpEvent;
import org.squeak.morphic.kernel.events.MouseWheelEvent;
import org.squeak.morphic.system.EyeMorph;
import org.squeak.morphic.system.HandMorph;

/**
 * This Hand implements gestures to navigate a morphic World:
 * - Left-Click + move: Panning
 * - Right-Click + move up/down: Zooming
 * - Mouse wheel: Zooming
 * 
 * @see org.squeak.morphic.system.HandMorph
 */
public class NavigationHandMorph extends HandMorph {

	private static int PAN_BUTTON = 1;
	private static int ZOOM_BUTTON = 3;
	
	private final EyeMorph eye;
	
	private boolean prePanning, panning, zooming;
	private Point anchor;
	private Transformation lastTransformation = Transformation.IDENTITY;
	
/*	protected void draw(Canvas canvas) {
		canvas.setColor(Color.GREEN);
		canvas.drawString(getPosition().toString(), Point.O);
	}
*/
	public NavigationHandMorph(EyeMorph eye) {
		this.eye = eye;
	}
	
	@Override
	public void dispatchEvent(KeyEvent e) {
		//TODO keyboard
		super.dispatchEvent(e);
	}

	@Override
	public void dispatchEvent(MouseEvent e) {
		e.hand = this;
		setPosition(e.position);
		if (!e.dispatchToMorph(this))
			super.dispatchEvent(e);
	}
	
	@Override
	public boolean handleEvent(MouseDownEvent e) {
		if (!panning && e.button == PAN_BUTTON) {
			anchor = e.position;
			Point origin = eye.toInner(Point.O);
			lastTransformation = Transformation.translation(-origin.x, -origin.y);
			prePanning = true;
//			setCursor(panningCursor);
			return true;
		}
		if (!zooming && e.button == ZOOM_BUTTON) {
			anchor = e.position;
			lastTransformation = Transformation.IDENTITY;
			zooming = true;
//			setCursor(zoomingCursor);
			return true;
		}
		return false;
	}

	@Override
	public boolean handleEvent(MouseUpEvent e) {
		if (prePanning || panning || zooming) {
			anchor = null;
			prePanning = false;
			panning = false;
			zooming = false;
//			setCursor(null);
			lastTransformation = Transformation.IDENTITY;
			return true;
		}
		return false;
	}

	@Override
	public boolean handleEvent(MouseWheelEvent e) {
		anchor = e.position;
		if (e.isShiftPressed()) {
			rotate((float)(e.count*Math.PI/50));
		} else {
			zoom((float)Math.pow(1.1, e.count));
		}
		return true;
	}

	@Override
	public boolean handleEvent(MouseMoveEvent e) {
		if (prePanning /*&& anchor.distance(e.position) > ... */) {
			panning = true;
			prePanning = false;
//			setCursor(panningCursor);
		}
		if (panning) {
			pan(e.position);
			return true;
		}
		if (zooming) {
			Point innerAnchor = eye.toInner(anchor);
			Point innerPosition = eye.toInner(e.position);
			float scale = innerAnchor.norm() / innerPosition.norm();
			zoom(scale);
			anchor = e.position;
			return true;
		}
		return false;
	}
	
	/**
	 * Adjust eye in order to move the anchor point to the given world position
	 */
	private void pan(Point position) {
		Point delta = eye.toInner(position.translatedBackBy(anchor));
		Transformation newTransformation = Transformation.translation(-delta.x, -delta.y);
		eye.transformBy(lastTransformation.inverse().with(newTransformation));
		lastTransformation = newTransformation;
	}
	
	private void zoom(float scale) {
		Point innerAnchor = eye.toInner(anchor);
		Transformation translation = Transformation.translation(-innerAnchor.x, -innerAnchor.y);
		Transformation newTransformation = translation.inverse().with(Transformation.scale(scale).with(translation));
		eye.transformBy(newTransformation);
	}

	/**
	 * Adjust eye rotation with center at the anchor point
	 */
	private void rotate(float angle) {
		Point innerAnchor = eye.toInner(anchor);
		Transformation translation = Transformation.translation(-innerAnchor.x, -innerAnchor.y);
		Transformation newTransformation = translation.inverse().with(Transformation.rotation(angle).with(translation));
		eye.transformBy(newTransformation);
	}
}
