package net.qmat.qmhh;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

public class Background extends ProcessingObject {
	
	private static int NR_SECTIONS = 16;
	
	private float centerX, centerY;
	private float innerDiameter, outerDiameter;
	private float innerRadius, outerRadius;
	private Body body;
	Vec2[] vertices;
	
	public Background() {
		centerX = Settings.getInteger(Settings.PR_CENTER_X);
		centerY = Settings.getInteger(Settings.PR_CENTER_Y);
		innerDiameter = Settings.getInteger(Settings.PR_RING_INNER_RADIUS) * 2.0f;
		outerDiameter = Settings.getInteger(Settings.PR_RING_OUTER_RADIUS) * 2.0f;
		innerRadius = innerDiameter / 2.0f;
		outerRadius = outerDiameter / 2.0f;
		
		createRingBox2D();
	}
	
	@SuppressWarnings("static-access")
	public void draw() {
		p.background(0);
		p.pushMatrix();
		p.translate(centerX, centerY);
		p.ellipseMode(p.CENTER);
		p.noStroke();
		p.fill(90, 40, 190);
		p.ellipse(0, 0, outerDiameter, outerDiameter);
		p.fill(0);
		p.ellipse(0, 0, innerDiameter, innerDiameter);
		p.popMatrix();
		
		float angleStep = Main.TWO_PI / 16.0f;
		for(int i=0; i<NR_SECTIONS; i++) {
			CPoint2 cpos1 = new PPoint2(innerRadius, i*angleStep).toCPoint2();
			CPoint2 cpos2 = new PPoint2(outerRadius, i*angleStep).toCPoint2();
			CPoint2 cpos3 = new PPoint2(innerRadius, (i+1)*angleStep).toCPoint2();
			CPoint2 cpos4 = new PPoint2(outerRadius, (i+1)*angleStep).toCPoint2();
			
			p.noFill();
			p.stroke(0);
			p.line(cpos1.x, cpos1.y, cpos2.x, cpos2.y);
			p.line(cpos3.x, cpos3.y, cpos4.x, cpos4.y);
		}
		
		/* Debug the ring boundary.
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
		// N.B. link the only static body to the background.
		body.setUserData(this);
		for(int i=0; i<steps; i++) {
			float angle = stepSize * i;
			vertices[i] = calculateChainPosition(angle);
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(0.1f, 5.0f, vertices[i], angle);
			body.createFixture(shape, 0.0f);
		}
	}

}
