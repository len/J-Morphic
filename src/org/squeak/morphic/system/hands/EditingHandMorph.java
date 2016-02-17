package org.squeak.morphic.system.hands;

import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.kernel.events.KeyEvent;
import org.squeak.morphic.kernel.events.MouseDownEvent;
import org.squeak.morphic.kernel.events.MouseEvent;
import org.squeak.morphic.kernel.events.MouseUpEvent;
import org.squeak.morphic.system.HandMorph;

/**
 * This Hand implements gestures to edit a morphic World:
 * - Click: move morphs changing their position within their current owner
 * - Shift-Click: grab a morph and drop it into another morph (changing owner)
 * - Control-Click: create a clone of a morph, drop it into the world
 *
 * @see org.squeak.morphic.system.HandMorph
 */
public class EditingHandMorph extends HandMorph {

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
		if (e.button == 1) {
			Morph morph = owner.pick(e.position, this);
			if (morph != null && morph != owner) {
				if (e.isCtrlPressed())
					pickUpMorph(morph.clone());
				else if (e.isShiftPressed())
					pickUpMorph(morph);
				else
					grabMorph(morph);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean handleEvent(MouseUpEvent e) {
		ungrabAllMorphs();
		dropAllMorphs();
		return true;
	}

}
