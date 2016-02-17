package org.squeak.morphic.support;

import java.awt.Image;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Color;
import org.squeak.morphic.kernel.Font;
import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.kernel.Point;
import org.squeak.morphic.kernel.Rectangle;
import org.squeak.morphic.kernel.Transformation;

public class DelegatingCanvas implements Canvas {

	private final Canvas canvas;
	
	public DelegatingCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	public void drawPoint(Point p) {
		canvas.drawPoint(convert(p));
	}

	public void drawLine(Point p1, Point p2) {
		canvas.drawLine(convert(p1), convert(p2));
	}

	public void drawRectangle(Rectangle rect) {
		canvas.drawRectangle(convert(rect));
	}

	public void fillRectangle(Rectangle rect) {
		canvas.fillRectangle(convert(rect));
	}

	public void drawEllipse(Rectangle rect) {
		canvas.drawEllipse(convert(rect));
	}

	public void fillEllipse(Rectangle rect) {
		canvas.fillEllipse(convert(rect));
	}

	public void drawPolygon(float[] points) {
		canvas.drawPolygon(convert(points));
	}

	public void fillPolygon(float[] points) {
		canvas.fillPolygon(convert(points));
	}

	public void drawPolyline(float[] points) {
		canvas.drawPolyline(convert(points));
	}

	public void drawText(String text, Point p) {
		canvas.drawText(text, convert(p));
	}
	
	public void drawImage(Image image, Rectangle destRect) {
		canvas.drawImage(image, convert(destRect));
	}
	
	public void drawImage(Image image, Rectangle srcRect, Rectangle destRect) {
		canvas.drawImage(image, srcRect, convert(destRect));
	}

	public void drawMorph(Morph morph) {
		morph.fullDraw(this);
	}

	public void setAlpha(float alpha) {
		canvas.setAlpha(alpha);
	}
	
	public float getAlpha() {
		return canvas.getAlpha();
	}

	public void setColor(Color color) {
		canvas.setColor(color);
	}
	
	public Color getColor() {
		return canvas.getFillColor();
	}

	public void setFillColor(Color color) {
		canvas.setFillColor(color);
	}
	
	public Color getFillColor() {
		return canvas.getFillColor();
	}

	public void setFont(Font font) {
		canvas.setFont(font);
	}

	public Font getFont() {
		return canvas.getFont();
	}

	public void setTransformation(Transformation transformation) {
		canvas.setTransformation(transformation);
	}
	
	public Transformation getTransformation() {
		return canvas.getTransformation();
	}
	
	public void setClipping(Rectangle rect) {
		canvas.setClipping(convert(rect));
	}
	
	public Rectangle getClipping() {
		return convertBack(canvas.getClipping());
	}

	public boolean isVisible(Rectangle rect) {
		return canvas.isVisible(convert(rect));
	}

	public float getScale() {
		return canvas.getScale();
	}

	public Rectangle getViewport() {
		return convertBack(canvas.getViewport());
	}

	protected Rectangle convert(Rectangle rect) {
		return rect;
	}

	protected Rectangle convertBack(Rectangle rect) {
		return rect;
	}

	protected float[] convert(float[] points) {
		return points;
	}
	
	protected Point convert(Point point) {
		return point;
	}
}
