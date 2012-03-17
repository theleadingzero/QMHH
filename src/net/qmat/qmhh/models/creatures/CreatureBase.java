/*
 * Based on Daniel Shiffman's Flocking example <http://www.shiffman.net>.
 */

package net.qmat.qmhh.models.creatures;

import java.util.ArrayList;

import net.qmat.qmhh.Main;
import net.qmat.qmhh.models.Hand;
import net.qmat.qmhh.models.ProcessingObject;
import net.qmat.qmhh.utils.CPoint2;
import net.qmat.qmhh.utils.PPoint2;
import net.qmat.qmhh.utils.Settings;

import org.jbox2d.dynamics.*;
import org.jbox2d.common.*;
import org.jbox2d.collision.shapes.*;

public class CreatureBase extends ProcessingObject {

	public int stage = 0;
	protected int maxStage = 2;

	protected int subStage = 0;
	protected int maxSubStage = 2;

	protected float w = 10.0f;
	protected float h = 10.0f;
	private float maxForce = 3.0f;
	private float maxSpeed = 10.0f;
	private Hand target = null;
	protected Body body;
	private int followDebugColor = 0;
	// these timestamps/durations are in seconds
	private Long lastGrowthTimestamp = 0L;
	private float minimalGrowthInterval; 

	private boolean rebuildShapeP = false;

	private static float DESIRED_SEPARATION = 60.0f;
	private static float NEIGHBOR_DISTANCE  = 20.0f;



	public CreatureBase() {
		float angle = p.random(0, Main.TWO_PI);
		CPoint2 cpos = new PPoint2(20.0f, angle).toCPoint2();
		init(cpos, angle);
	}

	public CreatureBase(CPoint2 cpos, float angle) {
		init(cpos, angle);
	}

	private void init(CPoint2 cpos, float angle) {
		stage = 0;
		minimalGrowthInterval = Settings.getFloat(Settings.PR_MINIMAL_GROWTH_INTERVAL);
		makeBody(cpos, angle);
	}

	private void makeBody(CPoint2 cpos, float angle) {

		FixtureDef fd = createFixture();

		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position.set(box2d.coordPixelsToWorld(new Vec2(cpos.x, cpos.y)));

		body = box2d.createBody(bd);
		body.createFixture(fd);
		body.setLinearVelocity(new Vec2(p.random(-0.5f, 0.5f),
				p.random(-0.5f, 0.5f)));
		body.setAngularVelocity(0.0f);
		body.setUserData(this);
	}

	public void destroy() {
		box2d.destroyBody(body);
	}

	public void draw() {

		if(target != null) {
			p.fill(followDebugColor);
		} else {
			p.noFill();
		}

		// TODO: delete the next line when debugging targeting
		p.fill(followDebugColor);

		p.pushMatrix();
		Vec2 loc = box2d.getBodyPixelCoord(body);
		p.translate(loc.x, loc.y);
		p.rotate(body.getAngle());
		p.rect(0, 0, w, h);
		p.popMatrix();

		if(target != null) {
			p.pushMatrix();
			p.ellipseMode(Main.CENTER);
			CPoint2 targetPos = target.getCPosition();
			p.translate(targetPos.x + p.random(0, 10), targetPos.y + p.random(0, 10));
			p.fill(followDebugColor);
			p.ellipse(0, 0, w, h);
			p.popMatrix();
		}
		p.noFill();
	}

	// TODO: DRY this out!
	public void update(ArrayList<CreatureBase> creatures) {
		// recreate the shapes if we have to
		if(rebuildShapeP) {
			rebuildShape();
		}
		
		// flock, but keep within the zone.
		float zoneWidth = Main.outerRingInnerRadius / 3.0f;
		float force = 1.0f;
		Vec2 loc = body.getWorldCenter();
		PPoint2 ppos = new CPoint2(box2d.getBodyPixelCoord(body)).toPPoint2();
		// if in stage 1 
		if(stage == 1) {
			// too far outwards?
			if(ppos.r > zoneWidth * 2.0f) {
				followDebugColor = p.color(155, 0 ,0);
				body.setLinearVelocity(body.getLinearVelocity().mulLocal(0.98f));
				Vec2 towards = seek(box2d.coordPixelsToWorld(
						new Vec2(Main.centerX, Main.centerY)),
						force);
				body.applyForce(towards, loc);
				// too far inwards?
			} else if(ppos.r < zoneWidth) {
				followDebugColor = p.color(0, 155 ,0);
				Vec2 towards = seek(box2d.coordPixelsToWorld(
						new Vec2(Main.centerX, Main.centerY)),
						force);
				towards.negateLocal();
				body.applyForce(towards, loc);
				// in the zone? booyah!
			} else {
				followDebugColor = p.color(0);
				flock(creatures);
			}
		} else if(stage == 0) {
			// too far outwards?
			if(ppos.r > zoneWidth) {
				followDebugColor = p.color(55, 0 ,0);
				// slow down
				body.setLinearVelocity(body.getLinearVelocity().mulLocal(0.98f));
				Vec2 towards = seek(box2d.coordPixelsToWorld(
						new Vec2(Main.centerX, Main.centerY)),
						force);
				body.applyForce(towards, loc);
				// in the zone
			} else {
				followDebugColor = p.color(0);
				flock(creatures);
			}
			// in stage 2, so target, get back into the third zone, or flock
		} else {
			// if the creature has a target, ignore the flocking behavior
			if(target != null) {
				target();
			} else if(ppos.r < zoneWidth * 2.0f) {
				followDebugColor = p.color(0, 255 ,0);
				Vec2 towards = seek(box2d.coordPixelsToWorld(
						new Vec2(Main.centerX, Main.centerY)),
						force);
				towards.negateLocal();
				body.applyForce(towards, loc);
				// in the zone
			} else {
				followDebugColor = p.color(0);
				flock(creatures);
			}

		}
	}

	public void setTarget(Hand target) {
		if(target != null) {
			followDebugColor = p.color(p.random(100, 255), p.random(100, 255), p.random(100, 255));
			this.target = target;
			body.setLinearVelocity(body.getLinearVelocity().mulLocal(0.0f));
		}

	}

	public void removeTarget() {
		this.target = null;
	}

	private void flock(ArrayList<CreatureBase> creatures) {
		Vec2 sep = separate(creatures);
		Vec2 ali = align(creatures);
		Vec2 coh = cohesion(creatures);
		Vec2 loc = body.getWorldCenter();
		body.applyForce(sep,loc);
		body.applyForce(ali,loc);
		body.applyForce(coh,loc);
	}

	// N.B. t is in box2d location!
	private Vec2 seek(Vec2 t, float force) {
		Vec2 loc = body.getWorldCenter();
		Vec2 desired = t.sub(loc);
		if (desired.length() == 0) 
			return new Vec2(0, 0);
		desired.normalize();
		desired.mulLocal(maxSpeed);
		Vec2 vel = body.getLinearVelocity();
		Vec2 steer = desired.sub(vel);
		if(steer.length() > force) {
			steer.normalize();
			steer.mulLocal(force);
		}
		return steer;
	}

	private Vec2 seek(Vec2 t) {
		return seek(t, maxForce);
	}

	private void target() {
		if(target != null) {
			body.applyForce(seek(box2d.coordPixelsToWorld(target.getCPosition().toVec2()).mulLocal(5.0f), maxForce * 2.0f), 
					body.getWorldCenter());
		}
	}

	// Separation - method checks for nearby boids and steers away
	@SuppressWarnings("static-access")
	private Vec2 separate(ArrayList<CreatureBase> creatures) {
		float desiredSeparation = box2d.scalarPixelsToWorld(DESIRED_SEPARATION);

		Vec2 steer = new Vec2(0,0);
		int count = 0;

		// For every boid in the system, check if it's too close
		Vec2 locA = body.getWorldCenter();
		for (CreatureBase other : creatures) {
			Vec2 locB = other.body.getWorldCenter();

			float d = p.dist(locA.x, locA.y, locB.x, locB.y);
			// If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
			if ((d > 0.0f) && (d < desiredSeparation)) {
				// Calculate vector pointing away from neighbor
				Vec2 diff = locA.sub(locB);
				diff.normalize();
				diff.mulLocal(1.0f/d); // Weight by distance
				steer.addLocal(diff);
				count++;               // Keep track of how many
			}
		}
		// Average -- divide by how many
		if (count > 0) {
			steer.mulLocal(1.0f/count);
		}

		// As long as the vector is greater than 0
		if (steer.length() > 0.0f) {
			// Implement Reynolds: Steering = Desired - Velocity
			steer.normalize();
			steer.mulLocal(maxSpeed);
			Vec2 vel = body.getLinearVelocity();
			steer.subLocal(vel);
			if (steer.length() > maxForce) {
				steer.normalize();
				steer.mulLocal(maxForce);
			}
		}
		return steer;
	}

	// Alignment - for every nearby boid in the system, calculate the average velocity
	@SuppressWarnings("static-access")
	Vec2 align (ArrayList<CreatureBase> creatures) {
		float neighborDist = box2d.scalarPixelsToWorld(NEIGHBOR_DISTANCE);
		Vec2 steer = new Vec2(0,0);
		int count = 0;
		Vec2 locA = body.getWorldCenter();
		for (CreatureBase other : creatures) {
			Vec2 locB = other.body.getWorldCenter();

			float d = p.dist(locA.x,locA.y,locB.x,locB.y);
			if ((d > 0) && (d < neighborDist)) {
				Vec2 vel = other.body.getLinearVelocity();
				steer.addLocal(vel);
				count++;
			}
		}
		if (count > 0) {
			steer.mulLocal(1.0f/count);
		}

		// As long as the vector is greater than 0
		if (steer.length() > 0) {
			// Implement Reynolds: Steering = Desired - Velocity
			steer.normalize();
			steer.mulLocal(maxSpeed);
			Vec2 vel = body.getLinearVelocity();
			steer.subLocal(vel);
			if (steer.length() > maxForce) {
				steer.normalize();
				steer.mulLocal(maxForce);
			}
		}
		return steer;
	}

	// Cohesion - for the average location (i.e. center) of all nearby 
	// creatures, calculate steering vector towards that location
	@SuppressWarnings("static-access")
	Vec2 cohesion (ArrayList<CreatureBase> creatures) {
		float neighborDist = box2d.scalarPixelsToWorld(NEIGHBOR_DISTANCE);
		Vec2 sum = new Vec2(0,0);   // Start with empty vector to accumulate all locations
		int count = 0;
		Vec2 locA = body.getWorldCenter();
		for (CreatureBase other : creatures) {
			Vec2 locB = other.body.getWorldCenter();

			float d = p.dist(locA.x,locA.y,locB.x,locB.y);
			if ((d > 0) && (d < neighborDist)) {
				sum.addLocal(locB); // Add location
				count++;
			}
		}
		if (count > 0) {
			sum.mulLocal(1.0f/count);
			return seek(sum);  // Steer towards the location
		}
		return sum;
	}

	public PPoint2 getPPosition() {
		return getCPosition().toPPoint2();
	}

	public CPoint2 getCPosition() {
		Vec2 pos = box2d.getBodyPixelCoord(body);
		return new CPoint2(pos);
	}

	public boolean hasTargetP() {
		return target != null;
	}

	public void grow() {
		Long newTimestamp = System.nanoTime();
		if(stage < maxStage && newTimestamp - lastGrowthTimestamp > (double)minimalGrowthInterval * 1000000000L) {
			lastGrowthTimestamp = newTimestamp;
			if(subStage < maxSubStage) {
				subStage += 1;
				w *= 1.15f;
				h *= 1.15f;
			} else {
				subStage = 0;
				stage += 1;
				w *= 1.3f;
				h *= 1.3f;
			}
			rebuildShapeP = true;
		}
	}

	private FixtureDef createFixture() {
		CircleShape sd = new CircleShape();
		sd.m_radius = box2d.scalarPixelsToWorld(w/2.0f);

		FixtureDef fd = new FixtureDef();
		fd.shape = sd;
		fd.density = 0.5f / w;
		fd.friction = 1.2f;
		fd.restitution = 0.5f;
		return fd;
	}

	protected void drawDebugShape() {
		p.fill(0, 0, 255);
		Vec2 v = box2d.getBodyPixelCoord(body);
		for(Fixture f=body.getFixtureList(); f!=null; f=f.getNext()) {
			CircleShape shape = (CircleShape) f.getShape();
			float cRadius = box2d.scalarWorldToPixels(shape.m_radius);
			p.ellipse(v.x, v.y, cRadius*2, cRadius*2);
		}
	}

	private void rebuildShape() {
		// update size of the body
		Fixture f = body.getFixtureList();
		while(f != null) {
			body.destroyFixture(f);
			f = f.getNext();
		}
		body.createFixture(createFixture());
		rebuildShapeP = false;
	}

	public float getRadius() {
		return w * 0.5f;
	}
}
