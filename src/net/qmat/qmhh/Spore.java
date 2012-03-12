package net.qmat.qmhh;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public class Spore extends ProcessingObject {
	
	float w, h;
	float angle;
	Body body;
	
	
	public Spore() {
		w = 2.0f;
		h = 2.0f;
		
		angle = p.random(0, Main.TWO_PI);
		
		// Define a polygon (this is what we use for a rectangle)
	    CircleShape sd = new CircleShape();
	    sd.m_radius = box2d.scalarPixelsToWorld(w/2.0f);

	    // Define a fixture
	    FixtureDef fd = new FixtureDef();
	    fd.shape = sd;
	    // Parameters that affect physics
	    fd.density = 0.1f;
	    fd.friction = 0.3f;
	    fd.restitution = 0.3f;
	    fd.filter.groupIndex = Settings.getInteger(Settings.PR_SPORE_COLLISION_GROUP);

	    // Define the body and make it from the shape
	    BodyDef bd = new BodyDef();
	    bd.type = BodyType.DYNAMIC;
	    bd.position.set(box2d.coordPixelsToWorld(new Vec2(Main.centerX, 
	    												  Main.centerY)));

	    body = box2d.createBody(bd);
	    body.createFixture(fd);
	    Vec2 direction = new Vec2(p.random(-0.5f, 0.5f),
								  p.random(-0.5f, 0.5f));
	    direction.normalize();
	    body.setLinearVelocity(direction);
	    body.setAngularVelocity(0.0f);
	}
	
	public void draw() {
		Vec2 pos = box2d.getBodyPixelCoord(body);
		p.pushMatrix();
		p.translate(pos.x, pos.y);
		p.ellipseMode(Main.CENTER);
		p.stroke(p.color(230, 230, 255));
		p.fill(p.color(230, 230, 255));
		p.ellipse(0, 0, w, h);
		p.popMatrix();
	}

}
