package org.squeak.morphic.kernel.events;

import org.squeak.morphic.kernel.Point;

public abstract class MouseEvent extends Event {

	/**
	 * Coordinates of the pointer at the time of the event
	 * (in the local coordinate system of the morph handling
	 * the event)
	 */
	public Point position;
	
	/**
	 * The button that was pressed or released; 1 for the
	 * first button, 2 for the second button, and 3 for the
	 * third button, etc.
	 */
	public int button;
	
	/**
	 * The number of times the mouse has been clicked, as defined
	 * by the operating system; 1 for the first click, 2 for the
	 * second click and so on. Used for mouse wheel too.
	 */
	public int count;
	
	public String toString() {
		return getClass().getSimpleName()+" "+position+" ("+button+" "+stateMask+" "+count+")";
	}
}
