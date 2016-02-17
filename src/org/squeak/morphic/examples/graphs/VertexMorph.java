package org.squeak.morphic.examples.graphs;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Color;
import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.kernel.Point;

public class VertexMorph extends Morph {

	public VertexMorph() {
		translateBy(Point.random());
		scaleBy(0.05f);
	}
	
	@Override
	protected void draw(Canvas canvas) {
//		canvas.setAlpha(Math.min(canvas.getScale()+0.5f, 1));
		canvas.setFillColor(Color.GREEN_YELLOW);
		canvas.fillEllipse(getBounds());

		canvas.setAlpha(1);
		canvas.setColor(Color.BLACK);
		canvas.drawEllipse(getBounds());
		
//		canvas.drawString(""+canvas.getScale(), Point.O);
		
		canvas.setColor(Color.RED);
//		canvas.drawLine(Point.O, velocity.scaledBy(100));
	}

	@Override
	public boolean contains(Point point) {
		return point.radius() <= 1;
	}
	
	@Override
	protected void changed() {
		//HACK: handle change in the owner, GraphMorph.step()
	}

	@Override
	protected void fullChanged() {
		//HACK: handle change in the owner, GraphMorph.step()
	}
}
