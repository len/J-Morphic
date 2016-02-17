package org.squeak.morphic.examples.graphs;

import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Color;
import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.kernel.Point;
import org.squeak.morphic.physics.Force;
import org.squeak.morphic.physics.SimulationMorph;
import org.squeak.morphic.physics.forces.DragForce;
import org.squeak.morphic.physics.forces.NBodyForce;
import org.squeak.morphic.physics.forces.SpringForce;

public class GraphMorph extends SimulationMorph {

	public GraphMorph() {
		addForce(new NBodyForce());
		addForce(new DragForce(0.01f));
	}
	
	@Override
	protected void draw(Canvas canvas) {
		// draw edges, the nodes are submorphs and they draw themselves
		for (Force f: simulation.forces) {
			if (f instanceof SpringForce) {
				SpringForce s = (SpringForce) f;
				canvas.setColor(Color.BLACK);
				canvas.drawLine(s.p1.morph.getPosition(), s.p2.morph.getPosition());
			}
		}
	}

	public void addVertex(Morph m) {
		addParticle(m);
	}
	
	public void addEdge(Morph m1, Morph m2) {
		simulation.addSpring(m1, m2);
	}
	
	public static void addConnections(GraphMorph g, Morph v, int count) {
		for (int i=0; i<count; i++) {
			VertexMorph u = new VertexMorph();
			g.addVertex(u);
			g.addEdge(v, u);
		}
	}

	Morph getRandomVertex() {
		int i = (int)(Math.random()*simulation.particles.size());
		return simulation.particles.get(i).morph;
	}

	
	public static GraphMorph example() {
		GraphMorph graph = new GraphMorph();
		VertexMorph v1 = new VertexMorph();
		VertexMorph v2 = new VertexMorph();
		VertexMorph v3 = new VertexMorph();

		graph.addVertex(v1);
		v1.setPosition(Point.O);
		graph.addVertex(v2);
		graph.addVertex(v3);
		
		graph.addEdge(v1, v2);
		graph.addEdge(v1, v3);
		graph.addEdge(v2, v3);

		addConnections(graph, v1, 3);
		addConnections(graph, v2, 5);
		addConnections(graph, v3, 3);

		addConnections(graph, graph.getRandomVertex(), 10);
		addConnections(graph, graph.getRandomVertex(), 15);
		addConnections(graph, graph.getRandomVertex(), 50);
		addConnections(graph, graph.getRandomVertex(), 3);
		addConnections(graph, graph.getRandomVertex(), 2);
		addConnections(graph, graph.getRandomVertex(), 1);
		addConnections(graph, graph.getRandomVertex(), 50);
		addConnections(graph, graph.getRandomVertex(), 50);
		addConnections(graph, graph.getRandomVertex(), 50);
		addConnections(graph, graph.getRandomVertex(), 100);
		addConnections(graph, graph.getRandomVertex(), 100);
		addConnections(graph, graph.getRandomVertex(), 10);
		addConnections(graph, graph.getRandomVertex(), 15);
		addConnections(graph, graph.getRandomVertex(), 25);
		
		return graph;
	}
}
