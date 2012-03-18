package net.qmat.qmhh.models;

import net.qmat.qmhh.Main;
import net.qmat.qmhh.utils.Settings;

public class PlayheadModel extends ProcessingObject {
	
	private float angle;
	private float duration;
	private boolean playingP;
	private Long lastTime;
	
	// cache these settings for speed
	private float innerRadius, outerRadius;
	
	public PlayheadModel() {
		angle = 0.0f;
		playingP = false;
		innerRadius = Settings.getInteger(Settings.PR_RING_INNER_RADIUS);
		outerRadius = Settings.getInteger(Settings.PR_RING_OUTER_RADIUS);
	}
	
	public void update() {
		// only update the angle if we're playing
		if(playingP) {
			Long newTime = System.nanoTime();
			float interval = (newTime - lastTime) / 1000000000.0f;
			angle += (interval / duration) * Main.TWO_PI;
			lastTime = newTime;
		}
	}
	
	public void start() {
		lastTime = System.nanoTime();
		playingP = true;
	}
	
	public void stop() {
		playingP = false;
	}
	
	public void draw() {
		p.pushMatrix();
		p.translate(Main.centerX, Main.centerY);
		p.rotate(angle);
		p.stroke(p.color(255, 0, 0));
		p.line(innerRadius, 0, outerRadius, 0);
		p.popMatrix();
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}
	
	public void setDuration(float duration) {
		this.duration = duration;
	}
	

}
