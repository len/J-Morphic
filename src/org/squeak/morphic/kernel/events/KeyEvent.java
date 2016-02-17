package org.squeak.morphic.kernel.events;

public abstract class KeyEvent extends Event {
	
 	/**
 	 * the character represented by the key that was typed.  
	 * This is the final character that results after all modifiers have been
 	 * applied.  For example, when the user types Ctrl+A, the character value
 	 * is 0x01.  It is important that applications do not attempt to modify the
 	 * character value based on a stateMask (such as SWT.CTRL) or the resulting
 	 * character will not be correct.
 	 */
	public char character;
	
	/**
	 * the key code of the key that was typed,
	 * as defined by the key code constants in class <code>SWT</code>.
	 * When the character field of the event is ambiguous, this field
	 * contains the unicode value of the original character.  For example,
	 * typing Ctrl+M or Return both result in the character '\r' but the
	 * keyCode field will also contain '\r' when Return was typed.
	 */
	public int keyCode;
	
	public String toString() {
		return getClass().getSimpleName()+" "+character+" ("+keyCode+" "+stateMask+")";
	}
}
