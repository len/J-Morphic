package org.squeak.morphic.examples;

import java.awt.Image;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Color;
import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.kernel.Point;
import org.squeak.morphic.kernel.Rectangle;

public class ImageMorph extends Morph {

	private Image image;
	
	public ImageMorph(Image image) {
		this.image = image;
	}
	
	@Override
	protected void draw(Canvas canvas) {
		Rectangle bounds = getBounds();
//		canvas.setClipping(bounds);
		canvas.setColor(Color.MAGENTA);
		canvas.drawImage(image, bounds);
	}

	@Override
	public boolean contains(Point point) {
		return getBounds().contains(point);
	}
}
