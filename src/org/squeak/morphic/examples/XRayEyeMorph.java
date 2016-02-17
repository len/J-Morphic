package org.squeak.morphic.examples;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Color;
import org.squeak.morphic.kernel.Rectangle;
import org.squeak.morphic.kernel.Transformation;
import org.squeak.morphic.support.DelegatingCanvas;
import org.squeak.morphic.system.EyeMorph;

public class XRayEyeMorph extends EyeMorph {
	
	@Override
	protected void draw(Canvas canvas) {
		canvas.setFillColor(Color.BLACK);
		canvas.setAlpha(1.0f);
		canvas.fillRectangle(getBounds());

		Rectangle clipping = canvas.getClipping();
		Transformation transformation = canvas.getTransformation();

		canvas.setClipping(getBounds().intersection(canvas.getClipping()));

		canvas.setAlpha(0.5f);
		
		drawWorld(new DelegatingCanvas(canvas) {
			@Override
			public void setAlpha(float alpha) {
				super.setAlpha(alpha/2);
			}
			@Override
			public float getAlpha() {
				return super.getAlpha()*2;
			}
		});
		
		canvas.setTransformation(transformation);
		canvas.setClipping(clipping);
		
		canvas.setAlpha(1.0f);
		canvas.setColor(Color.BLACK);
		canvas.drawRectangle(getBounds());
	}
}
