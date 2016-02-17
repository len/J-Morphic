package org.squeak.morphic.kernel;

import java.util.NoSuchElementException;

import org.squeak.morphic.kernel.coordinates.CartesianCoordinateSystem;
import org.squeak.morphic.kernel.events.KeyDownEvent;
import org.squeak.morphic.kernel.events.KeyUpEvent;
import org.squeak.morphic.kernel.events.MouseClickEvent;
import org.squeak.morphic.kernel.events.MouseDownEvent;
import org.squeak.morphic.kernel.events.MouseMoveEvent;
import org.squeak.morphic.kernel.events.MouseUpEvent;
import org.squeak.morphic.kernel.events.MouseWheelEvent;
import org.squeak.morphic.system.HandMorph;
import org.squeak.morphic.system.WorldMorph;

/**
 * <p>A <code>Morph</code> (from the Greek 'shape' or 'form') is an interactive graphical object.</p>
 * 
 * <p>Morphs exist in a tree rooted at the World (a <code>WorldMorph</code>).
 * The morphs <i>owned</i> by a morph are its <i>submorphs</i>. Morphs are drawn recursively:
 * if a morph has no owner it never gets drawn.</p>
 * 
 * <p>Every morph has a local coordinate system to interpret positions.
 * Local coordinates are used in the {@link #draw(Canvas)} method (the Canvas understands
 * positions in the local coordinate system), for the positions of submorphs (for example
 * methods {@link #setPositon(Point)}, {@link #getPosition(Point)}, {@link #align(Point, Point)}),
 * and for positions carried by mouse events.</p>
 *
 * <p>Events are delivered to morphs in Z-order, i.e. if a morph occludes another the
 * event is only delivered to the foremost (just like physical objects). Any morph that
 * wants to receive events should implement the {@link #contains(Point)} method appropriately.
 * Events received by a morph in the <code>handleEvent(Event)</code> methods carry positions
 * in the local coordinate system.</p>
 *
 * <p>Morphs can be translated by an offset, rotated around their center,
 * orbited (rotated around the owner center), and scaled; more generally, they can
 * be applied an arbitrary affine transformation.</p>
 * 
 * <p>Every morph has an associated transformation that defines the inner space where
 * the morph is drawn and where the submorphs live. These transformations don't change anything
 * from the internal point of view of the morph. Mathematically, this is an embedding,
 * i.e. the morph's space is embedded in the owner space by a morphism. In our case the
 * morphisms are affine transformations, and they define Euclidean spaces within Euclidean
 * spaces (in particular, an Euclidean plane within the owner's Euclidean plane).</p>
 * 
 * @see org.squeak.morphic.kernel.CoordinateSystem
 * @see org.squeak.morphic.kernel.Canvas
 *
 * @see org.squeak.morphic.kernel.events.Event
 * 
 * @see org.squeak.morphic.kernel.Transformation
 */
public abstract class Morph {
	private static final Morph[] EMPTY_ARRAY = new Morph[0];

	protected volatile CoordinateSystem coordinateSystem = CartesianCoordinateSystem.CANONICAL;

	protected volatile Transformation transformation = Transformation.IDENTITY;
	
	protected volatile Morph owner;
	protected volatile Morph[] submorphs = EMPTY_ARRAY; // Z-order: last one is front, first one is back

	
	public Morph() { }

	protected final void setCoordinateSystem(CoordinateSystem coordinateSystem) {
		this.coordinateSystem = coordinateSystem;
	}
	
	public String toString() {
		return getClass().getSimpleName()+" "+getPosition();
	}

	public void printFull() {
		System.out.println(this);
		for (int i=0; i<submorphs.length; i++)
			System.out.println("    "+i+" "+submorphs[i]);
		System.out.println();
	}
	
	/* structure */

	/**
	 * Returns the WorldMorph where the receiver lives, or null if it's not in any World at the moment
	 * 
	 * @see org.squeak.morphic.system.WorldMorph
	 * 
	 * @return the WorldMorph where the receiver lives, or null
	 */
	public final WorldMorph getWorld() {
		return owner instanceof WorldMorph ? (WorldMorph) owner : owner == null ? null : owner.getWorld();
	}

	/**
	 * Returns owner of the receiver (i.e. a Morph that it is submorph of), or null if has no owner
	 * 
	 * @return the owner of the receiver, or null
	 */
	public final Morph getOwner() {
		return owner;
	}
	
	private final void setOwner(Morph morph) {
		if (owner != null) {
			changed();
			stopStepping();
			Morph oldOwner = owner;
			owner = null;
			oldOwner.removeMorph(this);
		}
		owner = morph;
		if (owner != null /*&& !(owner instanceof HandMorph)*/) {
			changed();
			startStepping();
		}
	}

	/**
	 * Add the given morph as submorph of the receiver
	 * 
	 * @param morph the morph to be added as the receiver submorph
	 */
	public void addMorph(Morph morph) {
		addMorphFront(morph);
	}

	/**
	 * Add the given morph as submorph of the receiver at the back of all existing submorphs (in z-order).
	 * 
	 * @param morph the morph to be added as the receiver submorph
	 */
	public final void addMorphBack(Morph morph) {
		addMorphAtIndex(morph, 0);
	}

	/**
	 * Add the given morph as submorph of the receiver in front of all existing submorphs (in z-order).
	 * 
	 * @param morph the morph to be added as the receiver submorph
	 */
	public final void addMorphFront(Morph morph) {
		addMorphAtIndex(morph, submorphs.length);
	}

	/**
	 * Add the given morph as submorph of the receiver in front of the other morph (in z-order).
	 * 
	 * @param morph the morph to be added as the receiver submorph
	 * @param other the morph to be kept in front of the newly added morph
	 * @throws NoSuchElementException if the other morph is not a submorph of the receiver
	 */
	public final void addMorphFront(Morph morph, Morph other) {
		for (int i=0; i<submorphs.length; i++) {
			if (submorphs[i] == other) {
				addMorphAtIndex(morph, i);
				return;
			}
		}
		throw new NoSuchElementException("No such submorph: "+other);
	}

	/**
	 * Add the given morph as submorph of the receiver behind the other morph (in z-order).
	 * 
	 * @param morph the morph to be added as the receiver submorph
	 * @param other the morph to be kept at the back of the newly added morph
	 * @throws NoSuchElementException if the other morph is not a submorph of the receiver
	 */
	public final void addMorphBack(Morph morph, Morph other) {
		for (int i=0; i<submorphs.length; i++) {
			if (submorphs[i] == other) {
				addMorphAtIndex(morph, i+1);
				return;
			}
		}
		throw new NoSuchElementException("No such submorph: "+other);
	}

	protected void addMorphAtIndex(Morph morph, int index) {
		if (index < 0 || index > submorphs.length)
			throw new IndexOutOfBoundsException();
		morph.setOwner(this);
		Morph[] newSubmorphs = new Morph[submorphs.length+1];
		for (int i=0; i<index; i++)
			newSubmorphs[i] = submorphs[i];
		newSubmorphs[index] = morph;
		for (int i=index; i<submorphs.length; i++)
			newSubmorphs[i+1] = submorphs[i];
		submorphs = newSubmorphs;
//		fullChanged(); //FIXME why not?
	}
	
	/**
	 * Remove the given morph from the submorphs of the receiver
	 * 
	 * @param morph the morph to be removed from the receiver submorphs
	 * @throws NoSuchElementException if the given morph is not a submorph of the receiver
	 */
	public final void removeMorph(Morph morph) {
		for (int i=0; i<submorphs.length; i++) {
			if (submorphs[i] == morph) {
				removeMorphAtIndex(i);
				return;
			}
		}
		throw new NoSuchElementException("No such submorph: "+morph);
	}
	
	protected void removeMorphAtIndex(int index) {
		Morph[] newSubmorphs = new Morph[submorphs.length-1];
		for (int i=0; i<index; i++)
			newSubmorphs[i] = submorphs[i];
		for (int i=index+1; i<submorphs.length; i++)
			newSubmorphs[i-1] = submorphs[i];
		submorphs[index].setOwner(null);
		submorphs = newSubmorphs;
//		fullChanged(); //FIXME why not?
	}
	
	/**
	 * Make a copy of the receiver including copies of all its submorphs.
	 */
	public Morph clone() {
		try {
			Morph newMorph = getClass().newInstance();
			newMorph.transformation = transformation;
			newMorph.coordinateSystem = coordinateSystem;
			newMorph.owner = null;
			newMorph.submorphs = new Morph[submorphs.length];
			for (int i=0; i<submorphs.length; i++)
				newMorph.submorphs[i] = submorphs[i].clone();
			return newMorph;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Remove the receiver from the owner submorphs list.
	 */
	public void delete() {
		owner.removeMorph(this);
	}
	
	/* drawing */
	
	/**
	 * Draw the receiver on the given canvas. The canvas will understand coordinates in the
	 * local coordinate system.
	 * 
	 * @see org.squeak.morphic.kernel.Canvas
	 * 
	 * @param canvas the canvas where the receiver will be drawn (in local coordinates)
	 */
	protected abstract void draw(Canvas canvas);
	
	/**
	 * Draw each submorph of the receiver on the given canvas, respecting Z-order.
	 * Essentially, the Canvas will call {@link #fullDraw(Canvas)} on each submorph.
	 * 
	 * @see #fullDraw(Canvas)
	 * 
	 * @param canvas the canvas where the submorphs will be drawn
	 */
	protected void drawSubmorphs(Canvas canvas) {
		for (Morph submorph: submorphs) {
			canvas.drawMorph(submorph);
		}
	}
	
	/**
	 * Draw the receiver and all its submorphs recursively on the given canvas.
	 * 
	 * @see org.squeak.morphic.kernel.Canvas
	 * @see #draw(Canvas)
	 * @see #drawSubmorphs(Canvas)
	 * 
	 * @param canvas the canvas where the morphs will be drawn
	 */
	public void fullDraw(Canvas canvas) {
		Transformation outerTransformation = canvas.getTransformation();
		Transformation innerTransformation = outerTransformation.with(transformation);

		canvas.setTransformation(innerTransformation);

		try {
			Rectangle fullBounds = getFullBounds();
			if (fullBounds == null || canvas.isVisible(getFullBounds())) {
				float alpha = canvas.getAlpha();
				Rectangle clippingArea = canvas.getClipping();
				Rectangle bounds = getBounds();
				if (bounds == null || canvas.isVisible(bounds)) {
					try {
						if (bounds != null) canvas.setClipping(bounds);//FIXME this looks ugly, breaks antialiasing on the borders
						draw(canvas);
					} catch (Exception e) {
						e.printStackTrace();
						drawError(canvas);
					}
					canvas.setTransformation(innerTransformation);
					canvas.setAlpha(alpha);
					canvas.setClipping(clippingArea);
				}
	
				drawSubmorphs(canvas);
				
				canvas.setAlpha(alpha);
				canvas.setClipping(clippingArea);
			}
		} finally {
			canvas.setTransformation(outerTransformation);
		}
	}

	private void drawError(Canvas canvas) {
		canvas.setColor(Color.YELLOW);
		canvas.setFillColor(Color.RED);
		Rectangle bounds = getBounds();
		canvas.fillRectangle(bounds);
		canvas.drawRectangle(bounds);
		canvas.drawLine(bounds.topLeft(), bounds.bottomRight());
		canvas.drawLine(bounds.bottomLeft(), bounds.topRight());
	}
	
	/* coordinate system */
	
	/**
	 * Convert a point from canonical coordinates to local coordinates.
	 * 
	 * @param canonical the canonical coordinates of the point to be converted to local coordinates
	 * @return the local coordinates of the point
	 */
	public final Point toLocal(Point canonical) {
		return coordinateSystem.toLocal(canonical);
	}

	/**
	 * Convert a point from local coordinates to canonical coordinates.
	 * 
	 * @param local the local coordinates of the point to be converted to canonical coordinates
	 * @return the local coordinates of the point
	 */
	public final Point toCanonical(Point local) {
		return coordinateSystem.toCanonical(local);
	}

	public final Rectangle toLocal(Rectangle rect) {
		// WARNING: this might be wrong in some coordinate systems (e.g. polar)
		Point p0 = toLocal(rect.topLeft());
		Point p1 = toLocal(rect.topRight());
		Point p2 = toLocal(rect.bottomLeft());
		Point p3 = toLocal(rect.bottomRight());
		return Rectangle.encompassing(new Point[] {p0, p1, p2, p3});
	}

	public final Rectangle toCanonical(Rectangle rect) {
		// WARNING: this might be wrong in some coordinate systems (e.g. polar)
		Point p0 = toCanonical(rect.topLeft());
		Point p1 = toCanonical(rect.topRight());
		Point p2 = toCanonical(rect.bottomLeft());
		Point p3 = toCanonical(rect.bottomRight());
		return Rectangle.encompassing(new Point[] {p0, p1, p2, p3});
	}
	
	/* mapping between inner and outer space */

	/**
	 * Map a point in the owner space to a point in the receiver space applying the inverse of the morph transformation
	 * 
	 * @param outer the point in the owner space
	 * @return a point in the receiver space, the result of applying the inverse of the morph transformation to <code>outer</code>
	 */
	public final Point toInner(Point outer) {
		return transformation.inverse().applyTo(outer);
	}

	public final Point toInner(Point outer, Morph referenceMorph) {
		if (referenceMorph == this) return outer;
		outer = owner == referenceMorph ? outer : owner.toInner(outer, referenceMorph);
		return toInner(outer);
	}

	public final Rectangle toInner(Rectangle rect) {
		Point p0 = toInner(rect.topLeft());
		Point p1 = toInner(rect.topRight());
		Point p2 = toInner(rect.bottomLeft());
		Point p3 = toInner(rect.bottomRight());
		return Rectangle.encompassing(new Point[] {p0, p1, p2, p3});
	}

	/**
	 * Map a point in the receiver space to a point in the owner space applying the morph transformation
	 * 
	 * @param inner the point in the receiver space
	 * @return a point in the owner space, the result of applying the morph transformation to <code>inner</code>
	 */
	public final Point toOuter(Point inner) {
		return transformation.applyTo(inner);
	}

	public final Point toOuter(Point inner, Morph referenceMorph) {
		if (referenceMorph == this) return inner;
		Point outer = toOuter(inner);
		return owner == referenceMorph ? outer : owner.toOuter(outer, referenceMorph);
	}

	public final Rectangle toOuter(Rectangle rect) {
		Point p0 = toOuter(rect.topLeft());
		Point p1 = toOuter(rect.topRight());
		Point p2 = toOuter(rect.bottomLeft());
		Point p3 = toOuter(rect.bottomRight());
		return Rectangle.encompassing(new Point[] {p0, p1, p2, p3});
	}

	/* position in the owner local coordinates */

	/**
	 * Returns the position of the receiver within its owner, in the owner local coordinate system, or null if there is no owner
	 * 
	 * @return The position of the receiver, or null
	 */
	public final Point getPosition() {
		return owner == null ? null : owner.toLocal(toOuter(Point.O));
	}

	/**
	 * Sets the position of the receiver within its owner, in the owner local coordinate system
	 * 
	 * @param position the desired position of the receiver within its owner (in the owner local coordinate system)
	 * 
	 * @throws NullPointerException If the owner is null
	 */
	public final void setPosition(Point position) {
		position = owner.toCanonical(position);
		Point oldPosition = toOuter(Point.O);
		translateBy(position.x - oldPosition.x, position.y - oldPosition.y);
	}

	/* picking in local coordinates */

	/**
	 * Returns true if the shape of this morph contains the given point (in local coordinates).
	 * Note that this is not the same as the morph bounds: the shape of a morph should be
	 * always contained within its bounds, but is not necessarily rectangular.
	 * 
	 * @return true if the shape of this morph contains the given point (in local coordinates)
	 */
	public boolean contains(Point point) {
		return false;
	}

	/**
	 * Finds a submorph immediately under the given position (in local coordinates),
	 * under the given submorph, respecting Z-order.
	 * 
	 * @see org.squeak.morphic.system.HandMorph
	 * 
	 * @param point a position in local coordinates.
	 * @param a submorph to look for (pick morphs under this submorph, usually this is a <code>HandMorph</code>).
	 * @return the foremost submorph that contains the given point, or null if none
	 */
	public Morph pick(Point point, Morph topMorph) {
		Point canonical = toCanonical(point);
		for (int i=0; i<submorphs.length; i++) {
			if (submorphs[i] == topMorph) {
				// enumerate the submorphs in reverse Z-order, first the foremost:
				for (int j=i-1; j>=0; j--) {
					Morph submorph = submorphs[j];
					Point submorphPoint = submorph.toLocal(submorph.toInner(canonical));
					Morph morph = submorph.pick(submorphPoint);
					if (morph != null)
						return morph;
				}
				return null;
			}
		}
		throw new NoSuchElementException("No such submorph: "+topMorph);
	}

	/**
	 * Finds a submorph immediately under the given position (in local coordinates) respecting Z-order.
	 * 
	 * @param point a position in local coordinates.
	 * @return the foremost submorph that contains the given point, or null if none
	 */
	public Morph pick(Point point) {
		Point canonical = toCanonical(point);
		// enumerate the submorphs in reverse Z-order, first the foremost:
		for (int i=submorphs.length-1; i>=0; i--) {
			Morph submorph = submorphs[i];
			Point submorphPoint = submorph.toLocal(submorph.toInner(canonical));
			Morph morph = submorph.pick(submorphPoint);
			if (morph != null)
				return morph;
		}
		if (contains(point))
			return this;
		return null;
	}
	
	/* change management in canonical coordinates */

	/**
	 * Report that the receiver has changed and needs to be redrawn
	 * 
	 * @see #getBounds()
	 * @see #draw(Canvas)
	 */
	protected void changed() {
		changed(getBounds());
	}

	/**
	 * Reports that the receiver submorphs have changed and the area covering submorphs needs to be redrawn
	 * 
	 * @see #getFullBounds()
	 * @see #fullDraw(Canvas)
	 */
	protected void fullChanged() {
		changed(getFullBounds());
	}

	/**
	 * Reports that an area has changed and needs to be redrawn
	 * 
	 * @param rect the area that has changed and needs to be redrawn (in canonical coordinates within the morph space)
	 */
	protected void changed(Rectangle rect) {
		if (owner != null) {
			if (rect != null)
				owner.changed(toOuter(rect));
			else
				owner.changed();
		}
	}

	/* bounds in canonical coordinates */

	/**
	 * Returns the bounds of the receiver in canonical coordinates.
	 * Anything drawn in the {@link #draw(Canvas)} method must be within this area.
	 * 
	 * @see #getFullBounds()
	 * @see #draw(Canvas)
	 * 
	 * @return the bounds of the receiver in canonical coordinates, or null if it is unbound
	 */
	public Rectangle getBounds() {
		return Rectangle.UNIT;
	}

	/**
	 * Return a rectangle including the bounds of the receiver and the bounds of all submorphs, recursively.
	 * This area covers anything that would be drawn calling {@link #fullDraw(Canvas)}.
	 * 
	 * @see #getBounds()
	 * @see #fullDraw(Canvas)
	 * 
	 * @return a rectangle covering the bounds of the receiver and the bounds of all submorphs, recursively. or null if it is unbound
	 */
	public final Rectangle getFullBounds() {
		Rectangle fullBounds = getBounds();
		if (fullBounds == null) return null;
		for (Morph submorph: submorphs) {
			fullBounds = fullBounds.union(submorph.toOuter(submorph.getFullBounds()));
		}
		return fullBounds;
	}

	/* transformations */

	/**
	 * Move the receiver by the given offset
	 * 
	 * @param dx the offset to move the receiver in the x coordinate
	 * @param dy the offset to move the receiver in the y coordinate
	 */
	public final void translateBy(float dx, float dy) {
		setTransformation(transformation.translatedBy(dx, dy));
	}

	/**
	 * Move the receiver by the given offset
	 * 
	 * @param delta the offset to move the receiver
	 */
	public final void translateBy(Point delta) {
		translateBy(delta.x, delta.y);
	}

	/**
	 * Move the receiver by the offset needed to align the
	 * <code>inside</code> position (in the receiver local coordinates)
	 * with the <code>outside</code> position (in the owner local coordinates).
	 * 
	 * @param inside an internal position in the receiver local coordinates
	 * @param outside an external position in the owner local coordinates
	 */
	public final void align(Point inside, Point outside) {
		translateBy(owner.toCanonical(outside).translatedBackBy(toOuter(toCanonical(inside))));
	}

	/**
	 * Scale the receiver by the given factor
	 * 
	 * @param scalar the scalar factor to resize the receiver
	 */
	public final void scaleBy(float scalar) {
		setTransformation(transformation.scaledBy(scalar));
	}

	/**
	 * Rotate around the center
	 * 
	 * @param theta angle of rotation in radians
	 */
	public final void rotateBy(float theta) {
		setTransformation(transformation.rotatedBy(theta));
	}

	/**
	 * Rotate around the center of the owner (orbit around the owner)
	 * 
	 * @param theta angle of rotation in radians
	 */
	public final void orbitBy(float theta) {
		setTransformation(Transformation.rotation(theta).with(transformation));
	}

	/**
	 * Apply a transformation to the receiver, composing it to the right with the current transformation
	 * 
	 * @param transformation the transformation to be applied
	 */
	public final void transformBy(Transformation transformation) {
		setTransformation(this.transformation.with(transformation));
	}

	/**
	 * Apply a transformation to the receiver, composing it to the left (pre-image) with the current transformation
	 * 
	 * @param transformation the transformation to be applied
	 */
	public final void transformLeftBy(Transformation transformation) {
		setTransformation(transformation.with(this.transformation));
	}

	/**
	 * Set the transformation to the receiver, that defines its space within the space of the owner
	 * 
	 * @param transformation the transformation to be set
	 */
	public void setTransformation(Transformation transformation) {
		fullChanged();
		this.transformation = transformation;
		fullChanged();
	}
	
	/**
	 * Get the transformation to the receiver, that defines its space within the space of the owner
	 * 
	 * @return the transformation of the receiver
	 */
	public Transformation getTransformation() {
		return transformation;
	}
	
	/* stepping */

	public boolean wantsSteps() {
		return false;
	}
	
	public float stepTime() {
		return 1.0f; // seconds
	}
	
	public void step(float dt) {
		// subclasses can override
	}
	
	public /*final*/ void startStepping() {
		WorldMorph world = getWorld();
		if (world == null) return;
		for (Morph submorph: submorphs)
			submorph.startStepping();
		world.startStepping(this); //FIXME inefficient
	}

	public /*final*/ void stopStepping() {
		WorldMorph world = getWorld();
		if (world == null) return;
		for (Morph submorph: submorphs)
			submorph.stopStepping();
		world.stopStepping(this); //FIXME inefficient
	}

	/* events (handlers return true if the event was handled) */

	public void handleMouseEnter(HandMorph hand) {
	}

	public void handleMouseLeave(HandMorph hand) {
	}

	public void handleKeyboardEnter(HandMorph hand) {
	}

	public void handleKeyboardLeave(HandMorph hand) {
	}

	public boolean handleEvent(MouseDownEvent e) {
		return false;
	}

	public boolean handleEvent(MouseUpEvent e) {
		return false;
	}

	public boolean handleEvent(MouseClickEvent e) {
		return false;
	}

	public boolean handleEvent(MouseMoveEvent e) {
		return false;
	}

	public boolean handleEvent(MouseWheelEvent e) {
		return false;
	}

	public boolean handleEvent(KeyDownEvent e) {
		return false;
	}

	public boolean handleEvent(KeyUpEvent e) {
		return false;
	}
}
