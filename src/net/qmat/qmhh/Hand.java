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
		p.ellipse(x, y, radius, radius);
	}
	
}
