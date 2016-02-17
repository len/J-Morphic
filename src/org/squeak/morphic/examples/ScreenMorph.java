package org.squeak.morphic.examples;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Color;
import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.system.EyeMorph;

public class ScreenMorph extends Morph {
	private EyeMorph eye;

	public ScreenMorph(EyeMorph eye) {
		this.eye = eye;
	}
	
	@Override
	protected void draw(Canvas canvas) {
		//FIXME wont redraw when the eye's changedArea changes
		canvas.setClipping(getBounds().intersection(canvas.getClipping()));
		eye.drawWorld(canvas);
		canvas.setColor(Color.BLACK);
		canvas.drawRectangle(getBounds());
	}
	
	@Override
	public boolean wantsSteps() {
		return true;
	}
	
	@Override
	public void step(float dt) {
		//HACK
		changed();
	}
}
