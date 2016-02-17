package org.squeak.morphic.examples;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Color;
import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.kernel.Point;

public class RadialTreeMorph extends Morph {

	@Override
	protected void draw(Canvas canvas) {
		canvas.setColor(Color.GRAY);
		for (Morph morph: submorphs) {
			canvas.drawLine(Point.O, morph.getPosition());
		}

		canvas.setTransformation(canvas.getTransformation().scaledBy(0.4f));
		
		canvas.setAlpha(0.7f);
		canvas.setFillColor(Color.GREEN);
		canvas.fillEllipse(getBounds());
		
		canvas.setAlpha(1.0f);
		canvas.setColor(Color.BLACK);
		canvas.drawEllipse(getBounds());
	}

	@Override
	protected void addMorphAtIndex(Morph morph, int index) {
		for (int i=0; i<submorphs.length; i++) {
			submorphs[i].orbitBy((float)(-Math.PI*2/submorphs.length)*i);
		}
		super.addMorphAtIndex(morph, index);
		for (int i=0; i<submorphs.length; i++) {
			submorphs[i].setPosition(getBounds().bottomCenter());
			submorphs[i].orbitBy((float)(Math.PI*2/submorphs.length)*i);
		}
	}

	@Override
	protected void removeMorphAtIndex(int index) {
		for (int i=0; i<submorphs.length; i++) {
			submorphs[i].orbitBy((float)(-Math.PI*2/submorphs.length)*i);
		}
		super.removeMorphAtIndex(index);
		for (int i=0; i<submorphs.length; i++) {
			submorphs[i].setPosition(getBounds().bottomCenter());
			submorphs[i].orbitBy((float)(Math.PI*2/submorphs.length)*i);
		}
	}

	@Override
	public void step(float dt) {
		//rotateBy(0.1f * dt);
	}

	@Override
	public float stepTime() {
		return 1.0f/10;
	}
	
	@Override
	public boolean wantsSteps() {
		return true;
	}
	
	@Override
	public boolean contains(Point point) {
		return point.radius() <= 0.4f;
	}
}
