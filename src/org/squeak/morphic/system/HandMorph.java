package org.squeak.morphic.system;

import java.util.ArrayList;
import java.util.List;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.kernel.Point;
import org.squeak.morphic.kernel.Rectangle;
import org.squeak.morphic.kernel.Transformation;
import org.squeak.morphic.kernel.events.KeyEvent;
import org.squeak.morphic.kernel.events.MouseEvent;
import org.squeak.morphic.support.ShadowCanvas;

/**
 * Hands embody the user-input device (keyboard and mouse). User-input events are
 * delivered to the World through the Hand.
 * 
 * <p>Different types of hands can implement different gestures to act on the
 * World. For example, a hand that allows to manipulate the morphs geometry and
 * structure, or a hand that implements panning and zooming.</p>
 * 
 * <p><i>Every World should have at least one hand.</i></p>
 *
 * @see org.squeak.morphic.system.WorldMorph
 * @see org.squeak.morphic.system.EyeMorph
 */
public class HandMorph extends Morph {

	private Morph mouseFocus;
	private Morph keyboardFocus;
	
	private List<Morph> grabbedMorphs = new ArrayList<Morph>();

	@Override
	protected void draw(Canvas canvas) {
	}

	@Override
	protected void drawSubmorphs(Canvas canvas) {
		// submorphs of the hand are drawn casting a shadow over the morphs behind them (in z-order)
		Transformation transformation = canvas.getTransformation();
		float alpha = canvas.getAlpha();
		super.drawSubmorphs(new ShadowCanvas(canvas));
		canvas.setTransformation(transformation);
		canvas.setAlpha(alpha);
		super.drawSubmorphs(canvas);
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(0,0,0,0);
	}

	public void setKeyboardFocus(Morph morph) {
		if (keyboardFocus != null)
			keyboardFocus.handleKeyboardLeave(this);
		keyboardFocus = morph;
		if (keyboardFocus != null)
			keyboardFocus.handleKeyboardEnter(this);
	}
	
	public void dispatchEvent(KeyEvent e) {
		if (keyboardFocus != null)
			e.dispatchToMorph(keyboardFocus);
	}

	public void dispatchEvent(MouseEvent e) {
		e.hand = this;
		setPosition(e.position);
		updateGrabbedMorphs();
		Morph morph = owner.pick(e.position, this);
		if (morph != null) {
			if (mouseFocus != morph) {
				if (mouseFocus != null) {
					mouseFocus.handleMouseLeave(this);
				}
				mouseFocus = morph;
				mouseFocus.handleMouseEnter(this);
			}
			e.position = morph.toLocal(morph.toInner(e.position, owner));
			e.dispatchToMorph(morph);
		} else {
			if (mouseFocus != null) {
				mouseFocus.handleMouseLeave(this);
				mouseFocus = null;
			}
		}
	}
	
	public void pickUpMorph(Morph morph) {
		addMorph(morph);
		morph.setPosition(Point.O);
	}
	
	public void dropAllMorphs() {
		for (Morph morph: submorphs) {
			owner.addMorphFront(morph, this); //XXX or back?
			morph.setPosition(getPosition());
		}
	}
	
	public void grabMorph(Morph morph) {
		ungrabAllMorphs();
		grabbedMorphs.add(morph);
		updateGrabbedMorphs();
	}
	
	public void ungrabMorph(Morph morph) {
		grabbedMorphs.remove(morph);
	}
	
	public void ungrabAllMorphs() {
		grabbedMorphs.clear();
	}
	
	private void updateGrabbedMorphs() {
		Point handPositionInWorld = getPosition();
		for (Morph morph: grabbedMorphs) {
			Point handPosition = morph == owner ? handPositionInWorld : morph.getOwner().toInner(handPositionInWorld, owner);
			morph.setPosition(handPosition);
		}
	}
}
