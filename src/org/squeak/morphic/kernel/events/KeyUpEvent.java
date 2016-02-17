package org.squeak.morphic.kernel.events;

import org.squeak.morphic.kernel.Morph;

public class KeyUpEvent extends KeyEvent {

	@Override
	public boolean dispatchToMorph(Morph morph) {
		return morph.handleEvent(this);
	}
	
}
