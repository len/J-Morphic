package org.squeak.morphic.system;

import org.squeak.morphic.kernel.Morph;
import org.squeak.morphic.kernel.Transformation;

public class Transition implements Activity {

	private final Morph morph;
	private final Transformation sourceTransformation, targetTransformation;
	private final float duration;
	private final float stepTime;
	private final float accelerationTime, decelerationTime;
	private float t = 0;
	
	public Transition(Morph morph, Transformation targetTransformation, float duration, float stepTime) {
		this(morph, targetTransformation, duration, stepTime, 0, 0);
	}
	
	public Transition(Morph morph, Transformation targetTransformation, float duration, float stepTime, float accelerationTime, float decelerationTime) {
		this.morph = morph;
		this.sourceTransformation = morph.getTransformation();
		this.targetTransformation = targetTransformation;
		this.duration = duration;
		this.stepTime = stepTime;
		this.accelerationTime = accelerationTime;
		this.decelerationTime = decelerationTime;
	}
	
	public boolean wantsSteps() {
		return t < duration;
	}

	public float stepTime() {
		return stepTime;
	}
	
	public void step(float dt) {
		t = t + dt;
		if (t > duration) t = duration;
		morph.setTransformation(sourceTransformation.interpolate(targetTransformation, Math.min(interpolate(), 1f)));
	}
	
	private float interpolate() {
		float x = t/duration;
		float a = accelerationTime/duration;
		float b = decelerationTime/duration;
		float h = 2 / (2-a-b);
		if (duration - t < decelerationTime) {
			return 1 - h*(1-x)*(1-x)/(2*b);
		}
		if (t < accelerationTime) {
			return h * x*x / (2*a);
		}
		return h * (x - a/2);
	}
}
