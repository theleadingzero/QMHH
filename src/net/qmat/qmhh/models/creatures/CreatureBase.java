/*
 * Based on Daniel Shiffman's Flocking example <http://www.shiffman.net>.
 */

package net.qmat.qmhh.models.creatures;

import java.util.ArrayList;

import net.qmat.qmhh.Main;
import net.qmat.qmhh.controllers.Controllers;
import net.qmat.qmhh.controllers.SoundController;
import net.qmat.qmhh.models.Hand;
import net.qmat.qmhh.models.Models;
import net.qmat.qmhh.models.ProcessingObject;
import net.qmat.qmhh.utils.CPoint2;
import net.qmat.qmhh.utils.PPoint2;
import net.qmat.qmhh.utils.Settings;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

public class CreatureBase extends ProcessingObject {

	public int stage = 0;
	protected int maxStage = 2;

	protected int subStage = 0;
	protected int maxSubStage = 5;

	protected float w = 10.0f;
	protected float h = 10.0f;
	private float tmpW, tmpH;
	private float goalW;
	private float goalH;
	private float mulW = 1.0f;
	private float mulH = 1.0f;
	private float maxForce = 3.0f;
	private float maxSpeed = 10.0f;
	private Hand target = null;
	protected Body body;
	private int targetColor;
	// these timestamps/durations are in seconds
	private Long lastGrowthTimestamp = 0L;
	private Float minimalGrowthInterval; 
	private Double growthFeedbackTime = 1.0;

	private static float DESIRED_SEPARATION = 80.0f;
	private static float NEIGHBOR_DISTANCE  = 40.0f;

	private float subStageGrowthFactor = 1.05f;
	private float stageGrowthFactor = 1.2f;


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
		goalW = w;
		goalH = h;
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
		body.setFixedRotation(true);
	}

	public void destroy() {
		box2d.destroyBody(body);
	}

	public void draw() {

		p.noFill();
		p.ellipseMode(Main.CENTER);
		p.pushMatrix();
		Vec2 loc = box2d.getBodyPixelCoord(body);
		p.translate(loc.x, loc.y);
		p.rotate(body.getAngle());
		p.ellipse(0, 0, w, h);
		p.popMatrix();
	}
	
	public void drawTargetFeedback() {
		if(stage > 0 && target != null) {
			float handWidth = 135f;
			p.ellipseMode(Main.CENTER);
			p.fill(targetColor);
			p.noStroke();
			
			Vec2 position = box2d.getBodyPixelCoord(body);
			p.ellipse(position.x, position.y, w+5, h+5);
			
			CPoint2 targetPos = target.getCPosition();
			p.ellipse(targetPos.x, targetPos.y, handWidth, handWidth);
			
			float angleOffset;
			PPoint2 crP  = new CPoint2(position).toPPoint2();
			angleOffset = Main.atan((w/2f)/crP.r);
			PPoint2 crP1 = new PPoint2(crP.r, crP.t-angleOffset);
			PPoint2 crP2 = new PPoint2(crP.r, crP.t+angleOffset);
			
			PPoint2 handP  = targetPos.toPPoint2();
			angleOffset = Main.atan((handWidth/2f)/handP.r);
			PPoint2 hP1 = new PPoint2(handP.r, handP.t-angleOffset);
			PPoint2 hP2 = new PPoint2(handP.r, handP.t+angleOffset);
			
			float middleOffsetR = 0;
			float middleOffsetT = -0.06f;
			PPoint2 middleP1 = new PPoint2((handP.r+crP.r)/2f, PPoint2.averageAngles(handP.t, crP.t));
			PPoint2 middleP2 = new PPoint2(middleP1.r+middleOffsetR, middleP1.t+middleOffsetT);
			middleP1.r -= middleOffsetR;
			middleP1.t -= middleOffsetT;
			
			CPoint2 crCP1 = crP1.toCPoint2();
			CPoint2 crCP2 = crP2.toCPoint2();
			CPoint2 hCP1 = hP1.toCPoint2();
			CPoint2 hCP2 = hP2.toCPoint2();
			CPoint2 middleCP1 = middleP1.toCPoint2();
			CPoint2 middleCP2 = middleP2.toCPoint2();
			
			float reverse = PPoint2.calculateAngularDistance(handP.t, crP.t) < 0 ? 1 : -1;
			// 
			CPoint2 guide1a = new PPoint2((middleP1.r*0.3f+crP1.r*0.7f), PPoint2.averageAngles(crP.t, middleP1.t) - reverse * 2 * middleOffsetT).toCPoint2();
			CPoint2 guide1b = new PPoint2((middleP1.r*0.7f+crP1.r*0.3f)/2, PPoint2.averageAngles(crP.t, middleP1.t) - reverse * 1 * middleOffsetT).toCPoint2();
			CPoint2 guide2a = new CPoint2(middleCP1.x-(guide1b.x-middleCP1.x), middleCP1.y-(guide1b.y-middleCP1.y));
			CPoint2 guide2a2 = new CPoint2(middleCP2.x-(guide1b.x-middleCP2.x), middleCP2.y-(guide1b.y-middleCP2.y));
			CPoint2 guide2b = new PPoint2(middleP1.r, handP.t + reverse * 1 * middleOffsetT).toCPoint2();
			
			p.text("g1a", guide1a.x, guide1a.y);
			p.text("g1b", guide1b.x, guide1b.y);
			p.text("g2a", guide2a.x, guide2a.y);
			p.text("g2a2", guide2a2.x, guide2a2.y);
			p.text("g2b", guide2b.x, guide2b.y);
			/*
			p.stroke(targetColor);
			p.noFill();
			p.beginShape();
			// first control point
			p.curveVertex(crCPguide.x, crCPguide.y);
			p.curveVertex(crCP1.x, crCP1.y);
			p.curveVertex(middleCP1.x, middleCP1.y);
			p.curveVertex(hCP1.x, hCP1.y);
			p.curveVertex(hCPguide.x, hCPguide.y);
			p.curveVertex(hCP2.x, hCP2.y);
			p.curveVertex(middleCP2.x, middleCP2.y);
			p.curveVertex(crCP2.x, crCP2.y);
			// last control point
			p.curveVertex(crCPguide.x, crCPguide.y);
			p.endShape(Main.CLOSE);
			*/
			
			//p.fill(targetColor);
			//p.noStroke();
			p.noFill();
			p.stroke(targetColor);
			p.beginShape();
			p.vertex(crCP1.x, crCP1.y);
			p.bezierVertex(guide1a.x, guide1a.y, guide1b.x, guide1b.y, middleCP1.x, middleCP1.y);
			p.bezierVertex(guide2a.x, guide2a.y, guide2b.x, guide2b.y, hCP1.x, hCP1.y);
			p.bezierVertex(hCP2.x, hCP2.y, hCP2.x, hCP2.y, hCP2.x, hCP2.y);
			p.bezierVertex(guide2b.x, guide2b.y, guide2a2.x, guide2a2.y, middleCP2.x, middleCP2.y);
			p.bezierVertex(guide1b.x, guide1b.y, guide1a.x, guide1a.y, crCP2.x, crCP2.y);
			p.endShape();

			//p.bezier(crCP1.x, crCP1.y, middleCP1.x, middleCP1.y, middleCP2.x, middleCP2.y, hCP1.x, hCP1.y);
			//p.bezier(crCP2.x, crCP2.y, middleCP1.x, middleCP1.y, middleCP2.x, middleCP2.y, hCP2.x, hCP2.y);
		}
	}

	// TODO: DRY this out!
	public void update(ArrayList<CreatureBase> creatures) {
		// adjust the size of the creature
		rebuildShape();
		
		// flock, but keep within the zone.
		float zoneWidth = Main.outerRingInnerRadius / 3.0f;
		float force = 1.0f;
		Vec2 loc = body.getWorldCenter();
		PPoint2 ppos = new CPoint2(box2d.getBodyPixelCoord(body)).toPPoint2();
		// add some randomness to the center
		Vec2 vCenter = new Vec2(Main.centerX + p.random(-100, 100), Main.centerY + p.random(-100, 100));
		// if in stage 1 
		
		// if the creature has a target, ignore the flocking behavior
		if(target != null && stage > 0) {
			target();
		} else if(stage == 1) {
			// too far outwards?
			if(ppos.r > zoneWidth * 2.0f) {
				body.setLinearVelocity(body.getLinearVelocity().mulLocal(0.98f));
				Vec2 towards = seek(box2d.coordPixelsToWorld(vCenter), force);
				body.applyForce(towards, loc);
			// too far inwards?
			} else if(ppos.r < zoneWidth) {
				Vec2 towards = seek(box2d.coordPixelsToWorld(vCenter), force);
				towards.negateLocal();
				body.applyForce(towards, loc);
			// in the zone? booyah!
			} else {
				flock(creatures);
			}
		} else if(stage == 0) {
			// too far outwards?
			if(ppos.r > zoneWidth) {
				// slow down
				body.setLinearVelocity(body.getLinearVelocity().mulLocal(0.98f));
				Vec2 towards = seek(box2d.coordPixelsToWorld(vCenter), force);
				body.applyForce(towards, loc);
				// in the zone
			} else if(ppos.r < zoneWidth / 2.0f) {
				Vec2 towards = seek(box2d.coordPixelsToWorld(vCenter), force);
				towards.negateLocal();
				body.applyForce(towards, loc);
			} else {
				flock(creatures);
			}
			// in stage 2, get back into the third zone, or flock
		} else {
			if(ppos.r < zoneWidth * 2.0f) {
				Vec2 towards = seek(box2d.coordPixelsToWorld(vCenter), force);
				towards.negateLocal();
				body.applyForce(towards, loc);
				// in the zone
			} else {
				flock(creatures);
			}

		}
	}

	public void setTarget(Hand target) {
		if(target != null) {
			targetColor = Models.getCreaturesModel().getTargetColor();
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
	
	// TODO: find the right path, not through the plants!
	private void target() {
		if(target != null) {
			PPoint2 targetPPos = target.getCPosition().toPPoint2();
			targetPPos.r = Main.outerRingInnerRadius - w/2.0f;
			Vec2 targetVPos = targetPPos.toVec2();
			body.applyForce(seek(box2d.coordPixelsToWorld(targetVPos), maxForce * 35.0f), 
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
		Long interval = newTimestamp - lastGrowthTimestamp;
		if(stage < maxStage && interval > (double)minimalGrowthInterval * 1000000000L) {
			lastGrowthTimestamp = newTimestamp;
			growWithoutTimeCheck(true);
		}
	}
	
	public void growWithoutTimeCheck(boolean notifySound) {
		SoundController sc = Controllers.getSoundController();
		boolean ns = notifySound && sc != null && Models.getInstance() != null;
		if(subStage < maxSubStage) {
			subStage += 1;
			goalW = goalW * subStageGrowthFactor;
			goalH = goalH * subStageGrowthFactor;
			if(ns) {
				sc.creatureHasGrown(this);
			}
		} else {
			subStage = 0;
			stage += 1;
			goalW = goalW * stageGrowthFactor;
			goalH = goalH * stageGrowthFactor;
			if(ns) {
				sc.creatureChangedStage(this);
			}
		}
	}
	
	// N.B. this will only work when starting from stage and substage 0
	public void setStage(int stage) {
		for(int i=0; i<stage*maxSubStage - 1; i++)
			growWithoutTimeCheck(false);
		grow();
	}

	private FixtureDef createFixture() {
		CircleShape sd = new CircleShape();
		sd.m_radius = box2d.scalarPixelsToWorld(w/2.0f);

		FixtureDef fd = new FixtureDef();
		fd.shape = sd;
		fd.density = 0.09f;
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
		// update size of the body, grow to the new size slowly
		Long newTimestamp = System.nanoTime();
		Double growthIndex = (newTimestamp - lastGrowthTimestamp) / (growthFeedbackTime*1000000000.0);
		// only update mulW and mulH when we are growing
		if(growthIndex < 1.0) {
			mulH = mulW = (1.0f + Main.sin(growthIndex.floatValue() * Main.TWO_PI) / 2.0f) * 2.0f;
		} else {
			mulH = mulW = 1.0f;
		}
		tmpW += (goalW - tmpW) * 0.1f;
		tmpH += (goalH - tmpH) * 0.1f;
		w = tmpW * mulW;
		h = tmpH * mulH;
		Fixture f = body.getFixtureList();
		while(f != null) {
			f.m_shape.m_radius = box2d.scalarPixelsToWorld(w/2.0f);
			f = f.getNext();
		}
	}

	public float getRadius() {
		return w * 0.5f;
	}
}
