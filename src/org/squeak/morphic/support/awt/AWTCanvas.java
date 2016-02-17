package org.squeak.morphic.support.awt;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Color;
import org.squeak.morphic.kernel.Font;
import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.kernel.Point;
import org.squeak.morphic.kernel.Rectangle;
import org.squeak.morphic.kernel.Transformation;

public class AWTCanvas implements Canvas {
	private final Graphics2D graphics;

	private final int x, y, width, height;
	private Rectangle clippingArea;

	private Transformation transformation = Transformation.IDENTITY;
	private Font font;
	private float alpha = 1.0f;
	
	public AWTCanvas(Graphics2D graphics, int x, int y, int width, int height) {
		this.graphics = graphics;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		java.awt.Rectangle rect = graphics.getClipBounds();
		if (rect != null) {
			clippingArea = new Rectangle(rect.x-x, rect.y-y, rect.width, rect.height);
		} else {
			clippingArea = new Rectangle(0, 0, width, height);
			graphics.setClip(x, y, width, height);
		}
		
		alpha = graphics.getColor().getAlpha() / 255.0f;
		
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}
	
	public void drawPoint(Point p) {
		p = toIntegers(p);
		graphics.drawLine((int)p.x, (int)p.y, (int)p.x, (int)p.y);
	}

	public void drawLine(Point p1, Point p2) {
		p1 = toIntegers(p1);
		p2 = toIntegers(p2);
		graphics.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
	}

	public void drawRectangle(Rectangle rect) {
		rect = toIntegers(rect);
		graphics.drawRect((int)rect.x, (int)rect.y, (int)rect.width, (int)rect.height);
	}

	public void fillRectangle(Rectangle rect) {
		rect = toIntegers(rect);
		graphics.fillRect((int)rect.x, (int)rect.y, (int)rect.width, (int)rect.height);
	}

	public void drawEllipse(Rectangle rect) {
		rect = toIntegers(rect);
		graphics.drawOval((int)rect.x, (int)rect.y, (int)rect.width, (int)rect.height);
	}

	public void fillEllipse(Rectangle rect) {
		rect = toIntegers(rect);
		graphics.fillOval((int)rect.x, (int)rect.y, (int)rect.width, (int)rect.height);
	}
	
	public void drawPolygon(float[] points) {
		graphics.drawPolygon(toIntegersPolygon(points));
	}

	public void fillPolygon(float[] points) {
		graphics.fillPolygon(toIntegersPolygon(points));
	}

	public void drawPolyline(float[] points) {
		int nPoints = points.length / 2;
		int[] xPoints = new int[nPoints];
		int[] yPoints = new int[nPoints];
		for (int i=0; i<nPoints; i++) {
			Point p = toIntegers(new Point(points[i*2], points[i*2+1]));
			xPoints[i] = (int) p.x;
			yPoints[i] = (int) p.y;
		}
		graphics.drawPolyline(xPoints, yPoints, nPoints);
	}

	public void drawString(String string, Point p) {
		graphics.setFont(convert(font));
		p = toIntegers(p);
		graphics.drawString(string, (int)p.x, (int)p.y);
	}

	public void drawText(String string, Point p) {
		graphics.setFont(convert(font));
		p = toIntegers(p);
		graphics.drawString(string, (int)p.x, (int)p.y);
	}

	public void drawImage(Image image, Rectangle destRect) {
		int w = image.getWidth(null);
		int h = image.getHeight(null);
		drawImage(image, new Rectangle(0,0,w,h), destRect);
	}
	
	public void drawImage(Image image, Rectangle srcRect, Rectangle destRect) {
		destRect = toIntegers(destRect);
		graphics.drawImage(image,
				(int)destRect.x, (int)destRect.y, (int)(destRect.x+destRect.width), (int)(destRect.y+destRect.height),
				(int)srcRect.x, (int)srcRect.y, (int)(srcRect.x+srcRect.width), (int)(srcRect.y+srcRect.height),
				null);
	}

	public void drawMorph(Morph morph) {
		morph.fullDraw(this);
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
		graphics.setColor(convert(convert(graphics.getColor()), alpha));
		graphics.setBackground(convert(convert(graphics.getBackground()), alpha));
	}
	
	public float getAlpha() {
		return alpha;
	}

	public void setColor(Color color) {
		graphics.setColor(convert(color, alpha));
	}
	
	public Color getColor() {
		return convert(graphics.getColor());
	}

	public void setFillColor(Color color) {
		graphics.setPaint(convert(color, alpha));
	}
	
	public Color getFillColor() {
		return convert((java.awt.Color)graphics.getPaint());
	}

	
	public void setFont(Font font) {
		this.font = font;
	}

	public Font getFont() {
		return font;
	}

	public void setTransformation(Transformation transformation) {
		this.transformation = transformation;
		Transformation s = Transformation.scale(width/2.0f, height/2.0f).translatedBy(x+width/2.0f, y+height/2.0f);
		Transformation t = transformation.scaledBy(1.0f/width, 1.0f/height);
		AffineTransform u = new AffineTransform(s.with(t).m);
		graphics.setTransform(u);
		java.awt.Rectangle rect = graphics.getClipBounds();
		if (rect != null) {
			clippingArea = new Rectangle(rect.x-x, rect.y-y, rect.width, rect.height);
		} else {
			clippingArea = null;
		}
	}
	
	public Transformation getTransformation() {
		return transformation;
	}

	public void setClipping(Rectangle rect) {
		clippingArea = toIntegers(rect);
		java.awt.Rectangle awtRect = new java.awt.Rectangle((int)clippingArea.x+x, (int)clippingArea.y+y, (int)clippingArea.width+1, (int)clippingArea.height+1); 
		graphics.setClip(awtRect);
	}
	
	public Rectangle getClipping() {
		return toLocal(clippingArea);
	}

	public boolean isVisible(Rectangle rect) {
		rect = toIntegers(rect);
		return rect.width >= 1 && rect.height >= 1 && rect.intersects(clippingArea);
	}

	public float getScale() {
		Point o = transformation.applyTo(Point.O);
		Point u = transformation.applyTo(new Point(1,1));
		return (float)Math.sqrt(Math.abs((u.x-o.x) * (u.y-o.y)));
	}

	public Rectangle getViewport() {
		float x0 = -1f;
		float y0 = -1f;
		float x1 = x0+2;
		float y1 = y0+2;
		Transformation s = transformation.inverse();
		return Rectangle.encompassing(new Point[] {s.applyTo(x0,y0), s.applyTo(x1,y0), s.applyTo(x0,y1), s.applyTo(x1,y1)});
	}
	
	/* converting */
	
	private Point toIntegers(Point p) {
		return new Point(p.x*width, p.y*height);
	}
	
	private Point toLocal(Point p) {
		return /*transformation.inverse().applyTo(*/new Point(p.x/width, p.y/height);
	}

	private Rectangle toLocal(Rectangle rect) {
		Point p0 = toLocal(rect.topLeft());
		Point p1 = toLocal(rect.topRight());
		Point p2 = toLocal(rect.bottomLeft());
		Point p3 = toLocal(rect.bottomRight());
		return Rectangle.encompassing(new Point[] {p0, p1, p2, p3});
	}

	private Rectangle toIntegers(Rectangle rect) {
		//TODO optimize
		Point p0 = toIntegers(rect.topLeft());
		Point p1 = toIntegers(rect.topRight());
		Point p2 = toIntegers(rect.bottomLeft());
		Point p3 = toIntegers(rect.bottomRight());
		return Rectangle.encompassing(new Point[] {p0, p1, p2, p3});
	}
	
	private Polygon toIntegersPolygon(float[] points) {
		//TODO optimize
		int nPoints = points.length / 2;
		int[] xPoints = new int[nPoints];
		int[] yPoints = new int[nPoints];
		for (int i=0; i<nPoints; i++) {
			Point p = toIntegers(new Point(points[i*2], points[i*2+1]));
			xPoints[i] = (int) p.x;
			yPoints[i] = (int) p.y;
		}
		return new Polygon(xPoints, yPoints, nPoints);
	}
	
	private Color convert(java.awt.Color color) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue());
	}
	
	private java.awt.Color convert(Color color, float alpha) {
		return new java.awt.Color(color.red/255.0f, color.green/255.0f, color.blue/255.0f, alpha);
	}

	private java.awt.Font convert(Font font) {
		float fontSize = font.height * this.height;
		return new java.awt.Font(font.name, font.style, (int) fontSize);
	}
	
	public void dispose() {
	}
}
