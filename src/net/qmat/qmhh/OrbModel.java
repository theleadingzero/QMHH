package net.qmat.qmhh;

import processing.core.PVector;

public class OrbModel extends ProcessingObject {

	private float radius, r;
	private float maxRadius, minRadius;

	int numSystems = 45;
	OrbPSystem[] ps;
	float theta, theta2 = 0.0f;
	float amplitude; 

	public OrbModel() {
		ps =  new OrbPSystem[numSystems];
		maxRadius = Settings.getInteger(Settings.PR_ORB_MAX_RADIUS);
		minRadius = Settings.getInteger(Settings.PR_ORB_MIN_RADIUS);
		this.radius = 20.0f;
		this.r = 20.0f;

		float inx = p.width/2.0f;
		float iny = p.height/2.0f;
		float x, y;

		for(int i=0; i<numSystems; i++){
			// dispose PSystems in a circle
			x = r * Main.cos(theta);
			y = r * Main.sin(theta);
			x += inx;
			y += iny;     
			ps[i] = new OrbPSystem(new PVector(x, y), 20, theta, r);
			theta += Main.TWO_PI / numSystems;
		}
		amplitude = r;
	}

	public void draw() {
		p.noStroke();
		p.fill(120, 120, 245, 50);
		p.ellipse(Main.centerX, Main.centerY, radius*3, radius*3);
		p.ellipse(Main.centerX, Main.centerY, radius*2, radius*2);
		p.ellipse(Main.centerX, Main.centerY, radius, radius);
		
		waveR();
		for(int i=0; i<numSystems; i++) {
			ps[i].draw();
		}

	}

	private void waveR()
	{
		theta += 0.05;
		r = theta;
		r = Main.sin(r) * amplitude;
		r += radius;
		for(int i=0; i<numSystems; i++) {
			ps[i].setR(r);
		}
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		if(radius >= minRadius && radius <= maxRadius) {
			this.radius = radius;
			amplitude = radius;
		}
	}

	public void increaseRadius() {
		setRadius(radius+5.0f);
	}

	public void decreaseRadius() {
		setRadius(radius-5.0f);
	}

}
