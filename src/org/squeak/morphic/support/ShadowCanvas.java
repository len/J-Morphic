package org.squeak.morphic.support;

import java.awt.Image;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Color;
import org.squeak.morphic.kernel.Point;
import org.squeak.morphic.kernel.Rectangle;
import org.squeak.morphic.kernel.Transformation;

public class ShadowCanvas extends DelegatingCanvas {

	public static Point OFFSET = new Point(0.05f, 0.05f);
	public static float ALPHA_SCALE = 0.3f;

	public ShadowCanvas(Canvas canvas) {
		super(canvas);
		canvas.setTransformation(Transformation.translation(OFFSET.x, OFFSET.y).with(canvas.getTransformation()));
		canvas.setAlpha(canvas.getAlpha()*ALPHA_SCALE);
		canvas.setColor(Color.BLACK);
		canvas.setFillColor(Color.BLACK);
	}

	public void drawImage(Image image, Rectangle destRect) {
		fillRectangle(destRect);
	}
	
	public void drawImage(Image image, Rectangle srcRect, Rectangle destRect) {
		fillRectangle(destRect);
	}

	public void setColor(Color color) {
	}
	
	public void setFillColor(Color color) {
	}

	public void setAlpha(float alpha) {
		super.setAlpha(alpha*ALPHA_SCALE);
	}

	public float getAlpha() {
		return super.getAlpha()/ALPHA_SCALE;
	}
}
