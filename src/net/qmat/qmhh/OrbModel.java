package net.qmat.qmhh;

public class OrbModel extends ProcessingObject {
	
	private float radius;
	private float maxRadius, minRadius;
	
	public OrbModel() {
		maxRadius = Settings.getInteger(Settings.PR_ORB_MAX_RADIUS);
		minRadius = Settings.getInteger(Settings.PR_ORB_MIN_RADIUS);
		setRadius(minRadius);
	}
	
	public void draw() {
		p.pushMatrix();
		p.translate(Main.centerX, Main.centerY);
		p.fill(0x000077);
		p.noStroke();
		p.ellipse(0, 0, radius, radius);
		p.popMatrix();
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		if(radius >= minRadius && radius <= maxRadius) {
			this.radius = radius;
		}
	}
	
	public void increaseRadius() {
		setRadius(radius+10.0f);
	}
	
	public void decreaseRadius() {
		setRadius(radius-10.0f);
	}

}
