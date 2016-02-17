package org.squeak.morphic.kernel;

/**
 * <p>A <i>Coordinate System</i> defines a change of coordinates.</p>
 * 
 * <p>Each morph has an associated local coordinate system that is used to interpret positions.</p>
 * 
 * <p>For example, a morph that represents a geographical map would use the geographic coordinate
 * system <code>CartesianCoordinateSystem.GEOGRAPHIC</code>, which ranges from -180 degrees to 180 degrees
 * on the <code>X</code> axis (longitude), and -90 to 90 degrees on the <code>Y</code> axis (latitude).
 * A submorph of the map can be positioned in Buenos Aires just by calling
 * <code>submorph.setPosition(new Point(-58.4833f, -34.5833f))</code>. Also, mouse events handled by the
 * map would automatically carry pointer positions in the geographic coordinate system; so if the user clicks near
 * Buenos Aires, the morph receives a <code>MouseClickEvent</code> with position near (-58,-34).</p>
 * 
 * @see org.squeak.morphic.kernel.Morph
 */
public interface CoordinateSystem {
	Point toCanonical(Point local);
	Point toLocal(Point canonical);
}
