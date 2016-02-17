package org.squeak.morphic.kernel.events;

import org.squeak.morphic.kernel.Morph;

public class MouseDownEvent extends MouseEvent {

	@Override
	public boolean dispatchToMorph(Morph morph) {
		return morph.handleEvent(this);
	}
	
}
