package net.qmat.qmhh;

public class Background extends ProcessingObject {
	
	private int centerX, centerY;
	private int innerDiameter, outerDiameter;
	
	public Background() {
		centerX = Settings.getInteger(Settings.PR_CENTER_X);
		centerY = Settings.getInteger(Settings.PR_CENTER_Y);
		innerDiameter = Settings.getInteger(Settings.PR_RING_INNER_DIAMETER);
		outerDiameter = Settings.getInteger(Settings.PR_RING_OUTER_DIAMETER);
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
