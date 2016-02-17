package org.squeak.morphic.system;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import javax.imageio.ImageIO;

import org.squeak.morphic.examples.BallMorph;
import org.squeak.morphic.examples.ClockMorph;
import org.squeak.morphic.examples.EllipseMorph;
import org.squeak.morphic.examples.FlasherMorph;
import org.squeak.morphic.examples.FractalMorph;
import org.squeak.morphic.examples.ImageMorph;
import org.squeak.morphic.examples.RadialTreeMorph;
import org.squeak.morphic.examples.RectangleMorph;
import org.squeak.morphic.examples.ScreenMorph;
import org.squeak.morphic.examples.XRayEyeMorph;
import org.squeak.morphic.examples.graphs.GraphMorph;
import org.squeak.morphic.kernel.Canvas;
import org.squeak.morphic.kernel.Color;
import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.kernel.Point;
import org.squeak.morphic.kernel.Rectangle;
import org.squeak.morphic.kernel.events.MouseClickEvent;
import org.squeak.morphic.physics.SimulationMorph;
import org.squeak.morphic.physics.forces.DragForce;
import org.squeak.morphic.physics.forces.GravityForce;
import org.squeak.morphic.physics.forces.NBodyForce;
import org.squeak.morphic.physics.forces.NSpringForce;

/**
 * Worlds are the root of a Morph tree; they usually have no owner.
 * They are responsible for the passage of time, stepping the morphs that live in them.
 * 
 * <p>Every World should have at least a <i>hand</i> and an <i>eye</i>.</p>
 *
 * @see org.squeak.morphic.system.HandMorph
 * @see org.squeak.morphic.system.EyeMorph
 */
public class WorldMorph extends Morph {

	private PriorityQueue<ActivitySchedule> activities = new PriorityQueue<ActivitySchedule>();
	private transient Thread steppingThread;
	
	class ActivitySchedule implements Comparable<ActivitySchedule> {
		Activity activity;
		long time;
		ActivitySchedule(Activity activity) {
			this.activity = activity;
			this.time = System.currentTimeMillis();
		}
		public int compareTo(ActivitySchedule o) {
			return time < o.time ? -1 : time > o.time ? 1 : 0;
		}
	}
	
	class MorphSteppingActivity implements Activity {
		Morph morph;
		public MorphSteppingActivity(Morph morph) { this.morph = morph; }
		public boolean wantsSteps() { return morph.wantsSteps(); }
		public float stepTime() { return morph.stepTime(); }
		public void step(float dt) { morph.step(dt); }
	}

	@Override
	protected void draw(Canvas canvas) {
		canvas.setColor(Color.LAVENDER);
		canvas.fillRectangle(canvas.getViewport().insetBy(-0.1f)); // expand a bit to avoid aliasing artifacts in the border of the window
	}

	@Override
	protected void changed(Rectangle rect) {
		//FIXME slow
		for (Morph m: submorphs) {
			if (m instanceof EyeMorph) {
				((EyeMorph)m).invalidate(rect);
			}
		}
	}

	@Override
	public Rectangle getBounds() {
		return null;
	}

	@Override
	public boolean contains(Point point) {
		return true;
	}

	public void startActivity(Activity activity) {
		if (activity.wantsSteps()) {
			for (ActivitySchedule schedule: activities) {
				if (schedule.activity == activity)
					return;
			}
			activities.add(new ActivitySchedule(activity));
		}
		
	}
	
	public void stopActivity(Activity activity) {
		for (ActivitySchedule schedule: activities) {
			if (schedule.activity == activity) {
				activities.remove(schedule);
				return;
			}
		}
	}

	public void startStepping(Morph morph) {
		if (morph.wantsSteps()) {
			for (ActivitySchedule schedule: activities) {
				if (schedule.activity instanceof MorphSteppingActivity)
					if (((MorphSteppingActivity) schedule.activity).morph == morph)
						return;
			}
			activities.add(new ActivitySchedule(new MorphSteppingActivity(morph)));
		}
	}
	
	public void stopStepping(Morph morph) {
		for (ActivitySchedule schedule: activities) {
			if (schedule.activity instanceof MorphSteppingActivity)
				if (((MorphSteppingActivity) schedule.activity).morph == morph) {
					activities.remove(schedule);
					return;
				}
		}
	}

	@Override
	public boolean wantsSteps() {
		return true;
	}

	@Override
	public float stepTime() {
		ActivitySchedule schedule = activities.peek();
		long currentTime = System.currentTimeMillis();
		return (schedule == null ? 500 : schedule.time <= currentTime ? 0 : Math.min((int)(schedule.time - currentTime), 500)) / 1000.0f;
	}
	
	public void step() { //FIXME use step(float dt)
		ActivitySchedule schedule = activities.peek();
		long currentTime = System.currentTimeMillis();
		while (schedule != null && schedule.time <= currentTime) {
			activities.poll();
			try {
				schedule.activity.step(schedule.activity.stepTime() + (currentTime - schedule.time) / 1000.0f);
				if (schedule.activity.wantsSteps()) {
					schedule.time = currentTime + Math.max((int)(schedule.activity.stepTime()*1000), 1);
					activities.add(schedule);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error an Activity produced an exception, the offending activity was stopped");
			}
			schedule = activities.peek();
		}
	}

	@Override
	public void startStepping() {
		stopStepping();
		
		steppingThread = new Thread(new Runnable() {
			public void run() {
				try {
					while (wantsSteps()) {
						Thread.sleep((int)(stepTime()*1000));
						synchronized(WorldMorph.this) {
							step();
						}
						if (Thread.currentThread().isInterrupted())
							return;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
			}
		});
		
		steppingThread.start();
	}

	@Override
	public void stopStepping() {
		if (steppingThread != null) {
			steppingThread.interrupt();
			steppingThread = null;
		}
	}

	public boolean isStepping() {
		return steppingThread != null;
	}
	
	public List<HandMorph> getHands() {
		List<HandMorph> hands = new ArrayList<HandMorph>();
		for (Morph m: submorphs) {
			if (m instanceof HandMorph)
				hands.add((HandMorph)m);
		}
		return Collections.unmodifiableList(hands);
	}

	public List<EyeMorph> getEyes() {
		List<EyeMorph> eyes = new ArrayList<EyeMorph>();
		for (Morph m: submorphs) {
			if (m instanceof EyeMorph)
				eyes.add((EyeMorph)m);
		}
		return Collections.unmodifiableList(eyes);
	}

	public static WorldMorph example1() {
		WorldMorph world = new WorldMorph();

		Morph morph = new RectangleMorph();
		world.addMorph(morph);
		morph.setPosition(new Point(-0.1f, -0.1f));
		morph.scaleBy(0.5f);

		Morph submorph = new FlasherMorph();
		world.addMorph(submorph);
		submorph.setPosition(new Point(-0.2f, -0.2f));
		submorph.scaleBy(0.5f);

/*		morph = new PolarPlotMorph();
		world.addMorph(morph);
		morph.setPosition(new Point(-0.5f, -0.5f));
		morph.scaleBy(0.25f);
*/
		
/*		Morph morph = GraphMorph.example();
		morph.scaleBy(0.5f);
		world.addMorph(morph);
*/	
/*		morph = new XRayEyeMorph();
		world.addMorph(morph);
		morph.setPosition(new Point(0.5f, -0.5f));
		morph.scaleBy(0.3f);
*/
		morph = new RectangleMorph() {
			public void step(float dt) {
				rotateBy(0.1f * dt);
			}
			public boolean wantsSteps() {
				return true;
			}
			public float stepTime() {
				return 0.1f;
			}
		};
		world.addMorph(morph);
		morph.setPosition(new Point(0.5f, 0.5f));
		morph.scaleBy(0.25f);

/*		morph = new StringMorph();
		world.addMorph(morph);
		morph.scaleBy(0.5f);
		morph.translateBy(0.1f, 0.1f);
*/
		morph = new FlasherMorph();
		world.addMorph(morph);
		morph.scaleBy(0.05f);
		morph.translateBy(0.05f, 0.05f);

		morph = new ClockMorph();
		world.addMorph(morph);
		morph.scaleBy(1/16.0f);

		try {
			morph = new ImageMorph(ImageIO.read(new File("4_no_ice_clouds_mts_8k.jpg")));
			world.addMorph(morph);
			morph.scaleBy(1/5.0f);
		} catch (IOException e) {
			e.printStackTrace();
		}

/*		morph = new RadialTreeMorph();
		morph.scaleBy(0.5f);
		for (int i=0; i<3; i++) {
			submorph = new RadialTreeMorph();
			submorph.scaleBy(0.1f);
			morph.addMorph(submorph);
		}
		world.addMorph(morph);
*/		
		return world;
	}

	public static WorldMorph exampleRectangles() {
		WorldMorph world = new WorldMorph();
		
		RectangleMorph morph = new RectangleMorph();
		world.addMorph(morph);
//		morph.setPosition(new Point(1, 1));
		morph.scaleBy(0.5f);

		morph = new RectangleMorph();
		morph.setColor(Color.RED);
		world.addMorph(morph);
		morph.scaleBy(0.25f);
		morph.setPosition(new Point(-0.5f, -0.5f));

		morph = new RectangleMorph();
		morph.setColor(Color.BLUE);
		world.addMorph(morph);
		morph.scaleBy(0.1f);
		morph.setPosition(new Point(0.5f, -0.5f));

		return world;
	}

	public static WorldMorph exampleRotations() {
		WorldMorph world = new WorldMorph();

		Morph morph = new RectangleMorph() {
			public void step(float dt) {
				rotateBy(0.1f * dt);
			}
			public boolean wantsSteps() {
				return true;
			}
			public float stepTime() {
				return 1.0f/10;
			}
		};
		world.addMorph(morph);
		morph.setPosition(new Point(-0.5f, -0.5f));
		morph.scaleBy(0.25f);

		morph = new RectangleMorph();
		world.addMorph(morph);
		morph.setPosition(new Point(0.5f, 0.5f));
		morph.scaleBy(0.25f);
		morph.rotateBy((float)Math.PI/4);

		morph = new RadialTreeMorph();
		morph.scaleBy(0.5f);
		for (int i=0; i<3; i++) {
			Morph submorph = new RectangleMorph();
			submorph.scaleBy(0.1f);
			morph.addMorph(submorph);
		}
		world.addMorph(morph);
		
		return world;
	}
	
	public static WorldMorph exampleGraph() {
		WorldMorph world = new WorldMorph();

		Morph morph = GraphMorph.example();
		morph.scaleBy(0.5f);
		world.addMorph(morph);
	
		return world;
	}

	public static WorldMorph exampleBalls() {
		WorldMorph world = new WorldMorph();

		for (int i=0; i<1000; i++) {
			Morph morph = new BallMorph();
			morph.scaleBy(0.01f);
			world.addMorph(morph);
		}
	
		return world;
	}

	public static WorldMorph exampleSmallRectangle() {
		WorldMorph world = new WorldMorph();

		Morph morph = new RectangleMorph() {
			public void step(float dt) {
				rotateBy(0.1f * dt);
			}
			public boolean wantsSteps() {
				return true;
			}
			public float stepTime() {
				return 1.0f/10;
			}
		};
		morph.scaleBy(0.05f);
		world.addMorph(morph);
	
		return world;
	}

	public static WorldMorph exampleTransition() {
		final WorldMorph world = new WorldMorph();

		Morph morph = new RectangleMorph() {
			public boolean handleEvent(MouseClickEvent e) {
				world.startActivity(new Transition(this, getTransformation().translatedBy(0.5f, 0.5f), 1, 1f/20, 0.1f, 0.3f));
				return true;
			}
		};
		morph.scaleBy(0.1f);
		world.addMorph(morph);
	
		return world;
	}

	public static WorldMorph exampleSimulationBalls() {
		WorldMorph world = new WorldMorph();

		SimulationMorph simulation = new SimulationMorph();
		world.addMorph(simulation);
		simulation.enforceBounds = true;
		simulation.addForce(new NBodyForce(-0.2f, 1));
		simulation.addForce(new NSpringForce(1e-5f, 0.2f));
//		simulation.addForce(new NSpringForce(, 0.2));
		simulation.addForce(new DragForce());
		simulation.addForce(new GravityForce());
		
		for (int i=0; i<5; i++) {
			Morph morph = new EllipseMorph();
			morph.scaleBy(0.02f);
			simulation.addParticle(morph);
			morph.setPosition(Point.random());
		}

		return world;
	}

	public static WorldMorph exampleClock() {
		WorldMorph world = new WorldMorph();
		
		Morph morph = new ClockMorph();
		world.addMorph(morph);
		morph.scaleBy(1/4.0f);

		return world;
	}
	
	public static WorldMorph exampleEyes() {
		WorldMorph world = new WorldMorph();

		Morph morph = new RectangleMorph();
		world.addMorph(morph);
		morph.setPosition(new Point(-0.1f, -0.1f));
		morph.scaleBy(0.5f);

		Morph submorph = new FlasherMorph();
		world.addMorph(submorph);
		submorph.setPosition(new Point(-0.2f, -0.2f));
		submorph.scaleBy(0.5f);

		morph = new XRayEyeMorph();
		world.addMorph(morph);
		morph.setPosition(new Point(-0.5f, -0.5f));
		morph.scaleBy(0.2f);

		EyeMorph eye = new EyeMorph();
		world.addMorph(eye);
		eye.setPosition(new Point(0.5f, -0.5f));
		eye.scaleBy(0.3f);

		morph = new ScreenMorph(eye);
		world.addMorph(morph);
		morph.setPosition(new Point(0.5f, 0.5f));
		morph.scaleBy(0.3f);

		return world;
	}
	
	public static WorldMorph exampleFractal() {
		WorldMorph world = new WorldMorph();
		
		Morph morph = new FractalMorph();
		world.addMorph(morph);
		morph.scaleBy(1/4.0f);

		return world;
	}
}
