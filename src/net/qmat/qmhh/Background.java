package net.qmat.qmhh;

public class Background extends ProcessingObject {
	
	private float centerX, centerY;
	private float innerDiameter, outerDiameter;
	
	public Background() {
		centerX = Settings.getInteger(Settings.PR_CENTER_X);
		centerY = Settings.getInteger(Settings.PR_CENTER_Y);
		innerDiameter = Settings.getInteger(Settings.PR_RING_INNER_RADIUS) * 2.0f;
		outerDiameter = Settings.getInteger(Settings.PR_RING_OUTER_RADIUS) * 2.0f;
	}
	
	@SuppressWarnings("static-access")
	public void draw() {
		p.background(0);
		p.pushMatrix();
		p.translate(centerX, centerY);
		p.ellipseMode(p.CENTER);
		p.stroke(255);
		p.noFill();
		p.ellipse(0, 0, outerDiameter, outerDiameter);
		p.fill(0x000033);
		p.ellipse(0, 0, innerDiameter, innerDiameter);
		p.popMatrix();
	}

}
