package net.qmat.qmhh;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

public class Background extends ProcessingObject {
	
	private float centerX, centerY;
	private float innerDiameter, outerDiameter;
	private Body body;
	Vec2[] vertices;
	
	public Background() {
		centerX = Settings.getInteger(Settings.PR_CENTER_X);
		centerY = Settings.getInteger(Settings.PR_CENTER_Y);
		innerDiameter = Settings.getInteger(Settings.PR_RING_INNER_RADIUS) * 2.0f;
		outerDiameter = Settings.getInteger(Settings.PR_RING_OUTER_RADIUS) * 2.0f;
		
		createRingBox2D();
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
		
		/* Debug the ring boundary. N.B. a = -a for box2d.
		int steps = 60;
		float stepSize = Main.TWO_PI / steps;
		p.rectMode(p.CENTER);
		float w = box2d.scalarWorldToPixels(0.1f);
		float h = box2d.scalarWorldToPixels(2.0f);
		for(int i=0; i<vertices.length; i++) {
			p.pushMatrix();
			Vec2 pos = box2d.coordWorldToPixels(vertices[i]);
			p.translate(pos.x, pos.y);
			float angle = stepSize * i;
			p.rotate(angle);
			p.rect(0, 0, w, h);
			p.popMatrix();
		}
		*/
	}
	
	public void destroy() {
		box2d.destroyBody(body);
	}
	
	private Vec2 calculateChainPosition(float angle) {
		PPoint2 pos1 = new PPoint2(innerDiameter / 2.0f, angle);
		Vec2 pos2 = pos1.toVec2();
		Vec2 pos3 = new Vec2(pos2.x, pos2.y);
		Vec2 pos4 = box2d.coordPixelsToWorld(pos3);
		return pos4;
	}
	
	private void createRingBox2D() {
		
		int steps = 60;
		float stepSize = Main.TWO_PI / steps;
		vertices = new Vec2[steps];
		body = box2d.getGroundBody();
		for(int i=0; i<steps; i++) {
			float angle = stepSize * i;
			vertices[i] = calculateChainPosition(angle);
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(0.1f, 2.0f, vertices[i], -angle);
			body.createFixture(shape, 0.0f);
		}
	}

}
