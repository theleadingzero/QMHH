package net.qmat.qmhh;

public class Hand extends ProcessingObject {
	
	private float x = 0.0f;
	private float y = 0.0f;
	private float radius = 10.0f;
	
	public Hand(float x, float y) {
		updatePosition(x, y);
	}
	
	public void updatePosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void draw() {
		p.noStroke();
		p.fill(p.color(0, 155, 0));
		p.ellipse(x, y, radius, radius);
		p.stroke(p.color(200, 200, 0));
		p.line(x, y, p.centerX, p.centerY);
	}
	
}
