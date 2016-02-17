package org.squeak.morphic.kernel;

import java.awt.Image;

/**
 * A <code>Canvas</code> abstracts the drawing primitives of the underlying drawing system
 * that is ultimately employed to draw <code>Morphs</code>.
 * 
 * @see org.squeak.morphic.kernel.Morph
 */
public interface Canvas {

	/**
	 * Draw a single point (a pixel) at the given position
	 * 
	 * @param p a Point defining the position where the point will be drawn
	 */
	void drawPoint(Point p);
	
	/**
	 * Draw a line segment joining the two points
	 * 
	 * @param p1 a Point defining the coordinates where the line segment starts
	 * @param p2 a Point defining the coordinates where the line segment ends
	 */
	void drawLine(Point p1, Point p2);
	
	/**
	 * Draw a rectangle
	 * 
	 * @param rect the Rectangle to be drawn
	 */
	void drawRectangle(Rectangle rect);

	/**
	 * Fill a rectangle with the current <code>fill color</code>
	 * 
	 * @see #setFillColor(Color)
	 * @see #getFillColor()
	 * 
 	 * @param rect the Rectangle to be filled
	 */
	void fillRectangle(Rectangle rect);
	
	/**
	 * Draw an ellipse
	 * 
	 * @param rect the Rectangle defining the ellipse to be drawn
	 */
	void drawEllipse(Rectangle rect);

	/**
	 * Fill a ellipse with the current <code>fill color</code>
	 * 
	 * @see #setFillColor(Color)
	 * @see #getFillColor()
	 * 
 	 * @param rect the Rectangle bounding the ellipse to be filled
	 */
	void fillEllipse(Rectangle rect);

	/**
	 * Draw a (closed) polygon
	 * 
	 * @param points a float[] array of alternating x and y coordinates defining the vertices of the polygon to be drawn
	 */
	void drawPolygon(float[] points);
	
	/**
	 * Fill a polygon with the current <code>fill color</code>
	 * 
	 * @param points a float[] array of alternating <code>x</code> and <code>y</code> coordinates defining the vertices of the polygon to be filled
	 */
	void fillPolygon(float[] points);
	
	/**
	 * Draw a polyline
	 * 
	 * @param points a float[] array of alternating <code>x</code> and <code>y</code> coordinates defining the vertices of the polyline to be drawn
	 */
	void drawPolyline(float[] points);

	/**
	 * Draw a string of text.
	 * 
	 * @param text the text to be drawn
	 * @param p the position to draw the text
	 */
	void drawText(String text, Point p);
	
	/**
	 * Draw an image.
	 * 
	 * @param image the Image to be drawn
	 * @param destRect the rectangle where the image will be drawn
	 */
	void drawImage(Image image, Rectangle destRect);
	
	/**
	 * Draw a region of an image.
	 * 
	 * @param image the Image to be drawn
	 * @param srcRect the bounding box of the region of the image to be drawn
	 * @param destRect the rectangle where the image will be drawn
	 */
	void drawImage(Image image, Rectangle srcRect, Rectangle destRect);

	void drawMorph(Morph morph);

	/**
	 * Set the drawing color
	 * 
	 * @param color the Color to be used for drawing
	 */
	void setColor(Color color);
	
	/**
	 * Get the drawing color
	 * 
	 * @return the Color used for drawing
	 */
	Color getColor();

	/**
	 * Set the filling color
	 * 
	 * @param color the Color to be used for filling
	 */
	void setFillColor(Color color);
	
	/**
	 * Get the filling color
	 * 
	 * @return the Color used for filling
	 */
	Color getFillColor();

	/**
	 * Set the level of alpha-blending
	 * 
	 * @param alpha a float between 0 and 1 defining the level of alpha-blending (0 = transparent, 1 = solid)
	 */
	void setAlpha(float alpha);
	
	/**
	 * Get the level of alpha-blending
	 * 
	 * @return a float between 0 and 1 defining the level of alpha-blending (0 = transparent, 1 = solid)
	 */
	float getAlpha();
	
	/**
	 * Set the font to be used for drawing text
	 * 
	 * @param font a Font
	 */
	void setFont(Font font);

	/**
	 * Get the font being used for drawing text
	 * 
	 * @return a Font
	 */
	Font getFont();

	void setTransformation(Transformation transformation);
	Transformation getTransformation();
	
	/**
	 * Set a clipping area, so that drawing outside of the rectangle has no effect.
	 * 
	 * @param rect the Rectangle defining the clipping area
	 */
	void setClipping(Rectangle rect);
	
	
	/**
	 * Get the clipping area of the canvas (drawing outside of this rectangle has no effect)
	 * 
	 * @return the Rectangle defining the clipping area
	 */
	Rectangle getClipping();

	/**
	 * Return true if the given area is visible in this canvas,
	 * i.e. if it's reasonably big (>= 1 pixel) and if it intersects the
	 * clipping area
	 * 
	 * <p>This method is used to decide if some things don't need
	 * to be drawn.</p>
	 * 
	 * @param rect a Rectangle to be decided if it is visible or not
	 * @return true if the rectangle is visible
	 */
	boolean isVisible(Rectangle rect);
	
	/**
	 * The <i>scale</i> of a canvas is a measure of the zoom level that the canvas is rendering.
	 * More precisely, it is the square root of the area in the World that a unit square drawn
	 * with this canvas would have. At normal 1:1 zoom level the scale is 1.
	 * 
	 * <p>Morphs can use this for <i>semantic zooming</i>, implementing different ways of drawing
	 * at different zoom levels in the {@link Morph#draw(Canvas)} method. 
	 * 
	 * @return the scale of the canvas
	 */
	float getScale();
	
	/**
	 * The <i>viewport</i> is the visible portion of the world, i.e. the bounds of the eye that
	 * is rendering the world.
	 * 
	 * @see org.squeak.morphic.system.EyeMorph
	 * 
	 * @return a Rectangle, the visible portion of the world (bounds of the eye currently looking at the world)
	 */
	Rectangle getViewport();
}
