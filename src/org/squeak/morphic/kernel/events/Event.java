package org.squeak.morphic.kernel.events;

import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.system.HandMorph;

public abstract class Event {

	/**
	 * The hand associated with this event.
	 */
	public HandMorph hand;
	
					
	/**
	 * Keyboard and/or mouse event mask indicating that the SHIFT key
	 * was pushed on the keyboard when the event was generated
	 * (value is 1).
	 */
	public static final int SHIFT = 1;

	/**
	 * Keyboard and/or mouse event mask indicating that the CTRL key
	 * was pushed on the keyboard when the event was generated
	 * (value is 1&lt;&lt;1).
	 */
	public static final int CTRL = 1 << 1;

	/**
	 * Keyboard and/or mouse event mask indicating that the COMMAND key
	 * was pushed on the keyboard when the event was generated
	 * (value is 1&lt;&lt;2).
	 */
	public static final int COMMAND = 1 << 2;

	/**
	 * keyboard and/or mouse event mask indicating that the ALT key
	 * was pushed on the keyboard when the event was generated
	 * (value is 1&lt;&lt;3).
	 */
	public static final int ALT = 1 << 3;

	/**
	 * Keyboard and/or mouse event mask indicating that mouse button one
	 * was pushed when the event was generated. (value is 1&lt;&lt;4).
	 */
	public static final int BUTTON1 = 1 << 4;

	/**
	 * Keyboard and/or mouse event mask indicating that mouse button two
	 * was pushed when the event was generated. (value is 1&lt;&lt;3).
	 */
	public static final int BUTTON2 = 1 << 3;

	/**
	 * Keyboard and/or mouse event mask indicating that mouse button three
	 * was pushed when the event was generated. (value is 1&lt;&lt;2).
	 */
	public static final int BUTTON3 = 1 << 2;

	/**
	 * the state of the keyboard modifier keys at the time
	 * the event was generated.
	 */
	public int stateMask;

	public abstract boolean dispatchToMorph(Morph morph);
	
	public boolean isAltPressed() {
		return (stateMask & ALT) != 0;
	}

	public boolean isShiftPressed() {
		return (stateMask & SHIFT) != 0;
	}

	public boolean isCtrlPressed() {
		return (stateMask & CTRL) != 0;
	}

	public boolean isCommandPressed() {
		return (stateMask & COMMAND) != 0;
	}
}
