package org.squeak.morphic.support.awt;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.List;

import org.squeak.morphic.kernel.Point;
import org.squeak.morphic.system.EyeMorph;
import org.squeak.morphic.system.HandMorph;
import org.squeak.morphic.system.WorldMorph;
import org.squeak.morphic.system.hands.EditingHandMorph;
import org.squeak.morphic.system.hands.NavigationHandMorph;

public class MorphicWindow extends Frame {

	private static final long serialVersionUID = 7855680956684295847L;

	public static int FPS = 16;
	
	private volatile WorldMorph world;
	private volatile EyeMorph eye;
	private volatile HandMorph hand;
	
	private volatile Image buffer = null;

	private Thread redrawingThread;
	
	public MorphicWindow() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				System.exit(0);
			}
		});
		
		MouseAdapter mouseListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				org.squeak.morphic.kernel.events.MouseClickEvent morphicEvent = new org.squeak.morphic.kernel.events.MouseClickEvent();
				morphicEvent.count = event.getClickCount();
				dispatchMorphicEvent(morphicEvent, event);
			}
			@Override
			public void mouseMoved(MouseEvent event) {
				dispatchMorphicEvent(new org.squeak.morphic.kernel.events.MouseMoveEvent(), event);
			}
			@Override
			public void mouseDragged(MouseEvent event) {
				dispatchMorphicEvent(new org.squeak.morphic.kernel.events.MouseMoveEvent(), event);
			}
			@Override
			public void mousePressed(MouseEvent event) {
				dispatchMorphicEvent(new org.squeak.morphic.kernel.events.MouseDownEvent(), event);
			}
			@Override
			public void mouseReleased(MouseEvent event) {
				dispatchMorphicEvent(new org.squeak.morphic.kernel.events.MouseUpEvent(), event);
			}
			@Override
			public void mouseWheelMoved(MouseWheelEvent event) {
				org.squeak.morphic.kernel.events.MouseWheelEvent morphicEvent = new org.squeak.morphic.kernel.events.MouseWheelEvent();
				morphicEvent.count = event.getWheelRotation();
				dispatchMorphicEvent(morphicEvent, event);
			}
			
			private void dispatchMorphicEvent(org.squeak.morphic.kernel.events.MouseEvent morphicEvent, MouseEvent event) {
				morphicEvent.button = event.getButton();
				morphicEvent.stateMask = event.getModifiers();
				morphicEvent.position = toWorld(event.getX(), event.getY());
//				System.out.println("event position "+event.getX()+","+event.getY()+"; to world "+morphicEvent.position.x+","+morphicEvent.position.y);
//				System.out.println("button "+morphicEvent.button);
//				System.out.println("stateMask "+morphicEvent.stateMask);
				try {
					hand.dispatchEvent(morphicEvent);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				processChangedArea();
			}
		};
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		addMouseWheelListener(mouseListener);
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				dispatchMorphicEvent(new org.squeak.morphic.kernel.events.KeyDownEvent(), event);
			}
			public void keyReleased(KeyEvent event) {
				if (event.isAltDown()) {
					if (event.getKeyChar() == 'h')
						switchHand();
					else if (event.getKeyChar() == 'e')
						switchEye();
					else if (event.getKeyChar() == 's')
						switchStepping();
				}
				dispatchMorphicEvent(new org.squeak.morphic.kernel.events.KeyUpEvent(), event);
			}
			private void dispatchMorphicEvent(org.squeak.morphic.kernel.events.KeyEvent morphicEvent, KeyEvent event) {
				morphicEvent.stateMask = event.getModifiers();
				morphicEvent.character = event.getKeyChar();
				morphicEvent.keyCode = event.getKeyCode();
				try {
					hand.dispatchEvent(morphicEvent);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				processChangedArea();
			}
		});
	}
	
	public void setWorld(WorldMorph newWorld) {
		if (world != null)
			world.stopStepping();
		if (redrawingThread != null)
			redrawingThread.interrupt();
		
		world = newWorld;
		eye = new EyeMorph();
		hand = new EditingHandMorph();
		world.addMorph(hand);
		hand = new NavigationHandMorph(eye);
		world.addMorph(hand);
		world.addMorph(eye);
		
/*		Morph sticky = new RectangleMorph();
		eye.addMorph(sticky);
		sticky.scaleBy(0.25f);
		sticky.align(new Point(-1,-1), new Point(-1,-1));
*/
		world.startStepping();		

		redrawingThread = new Thread(new Runnable() {
			public void run() {
				while (true) { //isValid()) {
					processChangedArea();
					try {
						Thread.sleep(1000 / FPS);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			}
		});
		redrawingThread.start();
}

	private void processChangedArea() {
		synchronized(world) {
			org.squeak.morphic.kernel.Rectangle changedArea = eye.getChangedArea();
			if (changedArea != null) {
				repaintBuffer(); // TODO: clip
				float screenExtent = Math.max(getWidth(),getHeight());
				repaint((int)((changedArea.x+1.0f)/2.0f*screenExtent/*+rect.x*/), (int)((changedArea.y+1.0f)/2.0f*screenExtent/*+rect.y*/), (int)(changedArea.width*screenExtent), (int)(changedArea.height*screenExtent));
			}
		}
	}

	@Override
	public void dispose() {
		if (world != null)
			world.stopStepping();
		super.dispose();
	}
	
	private Point toWorld(int x, int y) {
		float screenExtent = Math.max(getWidth(),getHeight());
		float centerX = screenExtent/2.0f;
		float centerY = screenExtent/2.0f;
		float eyeX = (x - centerX) / screenExtent * 2.0f;
		float eyeY = (y - centerY) / screenExtent * 2.0f;
		//return world.toLocal(eye.toOuter(new Point(eyeX, eyeY)));
		return eye.toOuter(new Point(eyeX, eyeY)); //XXX assumes eye is submorph of the world
	}

	private void switchHand() {
		List<HandMorph> hands = world.getHands();
		Iterator<HandMorph> iterator = hands.iterator();
		while (iterator.hasNext() && iterator.next() != hand) ;
		Point position = hand.getPosition();
		if (iterator.hasNext()) {
			hand = iterator.next();
		} else {
			hand = hands.get(0);
		}
		hand.setPosition(position);
		System.out.println("set hand: "+hand);
	}

	private void switchEye() {
		List<EyeMorph> eyes = world.getEyes();
		Iterator<EyeMorph> iterator = eyes.iterator();
		while (iterator.hasNext() && iterator.next() != eye) ;
		if (iterator.hasNext()) {
			eye = iterator.next();
		} else {
			eye = eyes.get(0);
		}
		System.out.println("set eye: "+eye);
		eye.invalidate(eye.getBounds());
	}
	
	private void switchStepping() {
		if (world.isStepping()) {
			world.stopStepping();
		} else {
			world.startStepping();
		}
	}

	@Override
	public void paint(Graphics g) {
		update(g);
	}

	@Override
	public void update(Graphics g) {
		if (buffer == null || buffer.getWidth(null) != getWidth() || buffer.getHeight(null) != getHeight()) {
			buffer = createImage(getWidth(), getHeight());
			repaintBuffer();
		}
		g.drawImage(buffer, 0, 0, this);
	}

	private void repaintBuffer() {
		if (buffer == null) return;
		int w = (int) (Math.max(getWidth(),getHeight()));
		AWTCanvas canvas = new AWTCanvas((Graphics2D) buffer.getGraphics(), 0, 0, w, w);
		canvas.setAlpha(1.0f);
		synchronized(world) {
			eye.drawWorld(canvas);
		}
		canvas.dispose();
	}
	
	/****************************************************/
	
	public static void main(String args[]) {
		System.setProperty("sun.awt.noerasebackground", "true");
		MorphicWindow w = new MorphicWindow();
		w.setTitle("Morphic");
		w.setSize(800,600);
		w.setWorld(WorldMorph.example1());
		w.setVisible(true);
	}
}
