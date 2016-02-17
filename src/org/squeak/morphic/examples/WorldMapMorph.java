package org.squeak.morphic.examples;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Color;
import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.kernel.Point;
import org.squeak.morphic.kernel.Rectangle;
import org.squeak.morphic.kernel.coordinates.CartesianCoordinateSystem;

public class WorldMapMorph extends Morph {

	private Image image;
	
	public WorldMapMorph(Image image) {
		this.image = image;
		setCoordinateSystem(CartesianCoordinateSystem.GEOGRAPHIC);
	}
	
	public WorldMapMorph() throws IOException {
		this(ImageIO.read(new File("4_no_ice_clouds_mts_8k.jpg")));
	}
	
	@Override
	protected void draw(Canvas canvas) {
		Rectangle bounds = getBounds();
		canvas.setColor(Color.MAGENTA);
		canvas.drawImage(image, bounds);
	}

	@Override
	public boolean contains(Point point) {
		return getBounds().contains(point);
	}
}
