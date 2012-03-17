package net.qmat.qmhh.models.creatures;

import net.qmat.qmhh.Main;

import org.jbox2d.common.Vec2;

public class Jellyfish extends CreatureBase {

	private int numSegments = 4;
	private int maxSegments = 12;
	private float Ra = 150.0f;  // aperture / radius
	private float Ha = 100.0f;  // height
	private float angleSpace = Main.TWO_PI / numSegments;
	private float offsetX, offsetY;
	private float rot = 0.0f;
	private float[][] points = new float[maxSegments][2];
	private float velocitySum = 0.0f;

	Jellyfish() {
		super();
	}

	public void draw() {

		//drawDebugShape();
		
		Vec2 velocity = body.getLinearVelocity(); 
		velocitySum += velocity.length() * 0.05f + 0.05f;
		
		Ha = ((0.9f + 0.1f * Main.sin(velocitySum)))*h;
		Ra = ((0.9f + 0.1f * Main.cos(velocitySum)))*w/2;

		for (int i = 0; i < numSegments; i++) {
			float x = Main.cos(i * angleSpace) * Ra;
			float y = Main.sin(i * angleSpace) * Ra;
			points[i][0] = x;
			points[i][1] = y;
		}

		p.pushMatrix();
		// align the umbrella in the center
		Vec2 v = box2d.getBodyPixelCoord(body);
		p.translate(v.x, v.y);
		//p.ellipse(0, 0, 6, 6);
		// rotate the umbrella 90 degrees, so it stands normally
		//p.rotateX(body.getAngle());
		p.rotateZ(Main.atan2(velocity.x, velocity.y));
		// perform rotation just to appreciate the geometry
		p.rotateX(Main.PI * 0.5f + rot);

		// draw the umbrella
		for (int i = 0; i < numSegments - 1; i++) {
			umbrellaSegment(points[i][0], points[i][1], points[i + 1][0], points[i + 1][1], Ha);
		}
		// draw last segment of the umbrella
		umbrellaSegment(points[numSegments - 1][0], points[numSegments - 1][1], points[0][0], points[0][1], Ha);

		p.popMatrix();
		rot += p.random(0.0f, 0.006f);
		
	}
	
	public void grow() {
		super.grow();
		if(numSegments<maxSegments) {
			numSegments++;
			angleSpace = Main.TWO_PI / numSegments;
		}
	}

	void umbrellaSegment(float x1, float y1, float x2, float y2, float h) {

		p.stroke(252, 231, 214, 50);
		p.noFill();

		for (int i = 0; i <= 6; i++) {
			p.bezier(x1, y1, 0, x1, y1, h * 0.5f, x1, y1, h * 0.4f, 0, 0, h );
		}

		p.fill(255, 255, 255, 50);
		p.stroke(255, 255, 255, 100);
		p.beginShape(Main.TRIANGLE_STRIP);

		int steps = 10;

		for (int i=0; i<=steps; i++) {
			float t = i / (float)steps;
			float x = p.bezierPoint(x1, x1, x1, 0, t);
			float y = p.bezierPoint(y1, y1, y1, 0, t);
			float z = p.bezierPoint(0, h * 0.5f, h*0.4f, h, t);
			float bx = p.bezierPoint(x2, x2, x2, 0, t);
			float by = p.bezierPoint(y2, y2, y2, 0, t);
			float bz = p.bezierPoint(0, h * 0.5f, h * 0.4f, h, t);
			p.vertex(x, y, z);
			p.vertex(bx, by, bz);
		} 
		p.endShape(Main.CLOSE);
	}
}


