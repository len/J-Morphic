package org.squeak.morphic.system;

public interface Activity {
	boolean wantsSteps();
	float stepTime();
	void step(float dt);
}
