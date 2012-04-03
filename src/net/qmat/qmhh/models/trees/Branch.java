package net.qmat.qmhh.models.trees;

import java.util.ArrayList;
import java.util.Vector;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import net.qmat.qmhh.Main;
import net.qmat.qmhh.controllers.Controllers;
import net.qmat.qmhh.models.ProcessingObject;
import net.qmat.qmhh.utils.CPoint2;
import net.qmat.qmhh.utils.PPoint2;

public class Branch extends ProcessingObject {

	protected float length;
	protected ArrayList<Branch> branches = new ArrayList<Branch>();
	public Body body;
	public Branch parent;

	protected float startX, startY;
	protected float endX, endY;

	protected Long startGrowTimestamp;
	protected boolean activeP;
	protected boolean stoppedGrowing;
	protected Long startSproutTimestamp;
	protected Tree tree;

	public Branch(Tree tree,
			Branch parent, 
			float length, 
			float lengthMp,
			int level, 
			float x, 
			float y, 
			float angle) throws Exception {
		this.tree = tree;
		this.parent = parent;
		this.length = p.random(0.8f * length, 1.2f * length);

		activeP = false;
		stoppedGrowing = false;
		startSproutTimestamp = 0L;

		endX = x + this.length * Main.cos(angle);
		endY = y + this.length * Main.sin(angle);
		startX = x;
		startY = y;

		if(!this.withinEcosystemP()) {
			throw new Exception("Branch is not within ecosystem. Abort! Abort!");
		}

		makeBody(x, y, endX, endY, angle, level);


		if(level < Tree.MAX_BRANCH_LEVELS) {
			try {
				Branch b1 = new Branch(tree,
						this, 
						lengthMp * length,
						lengthMp,
						level+1,
						endX,
						endY,
						angle - tree.internalAngle);
				branches.add(b1);
			} catch(Exception e) {
				// ignore, not within ecosystem (probably)
				// TODO: use own exception type
			}
			try {
				Branch b2 = new Branch(tree, 
						this, 
						lengthMp * length,
						lengthMp,
						level+1,
						endX,
						endY,
						angle + tree.internalAngle);
				branches.add(b2);
			} catch(Exception e) {
				// ignore, not within ecosystem (probably)
				// TODO: use own exception type
			}
		}
	}

	public boolean withinEcosystemP() {
		PPoint2 ppos1 = new CPoint2(endX, endY).toPPoint2();
		PPoint2 ppos2 = new CPoint2(startX, startY).toPPoint2();
		return ppos1.r < Main.outerRingInnerRadius - 10.f &&
				ppos2.r < Main.outerRingInnerRadius - 10.f;
	}

	public void activate() {
		activeP = true;
		startGrowTimestamp = System.nanoTime();
		Controllers.getSoundController().plantHasGrown();
	}

	public boolean isActiveP() {
		return activeP;
	}

	public boolean branchesCompleteP() {
		if(branches.size() == 0)
			return true;
		// if any of the subbranches is not active, return false
		for(Branch branch : branches) {
			if(!branch.activeP) {
				return false;
			}
		}
		return true;
	}

	public boolean isStillGrowingP() {
		return System.nanoTime() - startGrowTimestamp < tree.BRANCH_GROW_TIME;
	}

	public boolean isStillSproutingP() {
		return (System.nanoTime() - startSproutTimestamp) < tree.BRANCH_GROW_TIME;
	}

	public void sprout() {
		// pick one of the sub branches and activate it
		startSproutTimestamp = System.nanoTime();
		synchronized(branches) {
			int offsetI = (int)p.random(branches.size());
			for(int i=0; i<branches.size(); i++) {
				Branch branch = branches.get((i+offsetI) % branches.size());
				if(!branch.isActiveP()) {
					branch.activate();
					return;
				}
			}
		}
	}

	private void makeBody(float x, 
			float y, 
			float endX,
			float endY,
			float angle,
			int level) {
		// Define a polygon (this is what we use for a rectangle)
		PolygonShape sd = new PolygonShape();
		sd.setAsBox(box2d.scalarPixelsToWorld(length/2.0f), 
				box2d.scalarPixelsToWorld(1.0f));

		// Define a fixture
		FixtureDef fd = new FixtureDef();
		fd.shape = sd;
		// Parameters that affect physics
		//float density = 1f - 0.35f*level;
		float density = 1f - 0.35f*level;
		fd.density = density < 0.001f ? 0.001f : density;
		//fd.density =  1f-0.5f*level < 0.01f ? 0.01f : 1.f-0.5f*level;
		fd.friction = 0.2f;
		//fd.restitution = 0.15f*level > 1 ? 1 : 0.15f*level;
		fd.restitution = 0.15f*(level-1)*(level-1) > 1 ? 1 : 0.15f*(level-1)*(level-1);
		fd.filter.groupIndex = tree.branchGroup;

		// Define the body and make it from the shape
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		// set the right position and angle
		bd.position.set(box2d.coordPixelsToWorld(new Vec2((x+endX)/2.0f, (y+endY)/2.0f)));
		/*float correctAngle = Main.PI * 0.5f - angle;
		    bd.angle = normalizeAngle(correctAngle);*/
		bd.angle = -angle;

		body = box2d.createBody(bd);
		body.createFixture(fd);
		body.setUserData(this);

		if(parent != null) {
			RevoluteJointDef rjd = new RevoluteJointDef();
			rjd.bodyA = parent.body;
			rjd.bodyB = body;
			rjd.collideConnected = false;
			Vec2 anchor = box2d.coordPixelsToWorld(new Vec2(x, y));
			rjd.localAnchorA = parent.body.getLocalPoint(anchor);
			rjd.localAnchorB = body.getLocalPoint(anchor);
			/*float a1 = normalizeAngle(correctAngle - internalAngle);
        		float a2 = normalizeAngle(correctAngle + internalAngle);
		        rjd.lowerAngle = a1 < a2 ? a1 : a2;
		        rjd.upperAngle = a1 > a2 ? a1 : a2;*/
			rjd.lowerAngle = -tree.internalAngle;
			rjd.upperAngle = tree.internalAngle;
			rjd.enableLimit = true;
			box2d.createJoint(rjd);
		}
	}

	protected CPoint2 getLeafPosition() {
		Transform transform = body.getTransform();
		for(Fixture f=body.getFixtureList(); f!=null; f=f.getNext()) {
			PolygonShape shape = (PolygonShape) f.getShape();
			Vec2 vpos = box2d.coordWorldToPixels(Transform.mul(transform, shape.getVertex(1)));
			return new CPoint2(vpos);
		}
		return new CPoint2(0, 0);
	}

	protected CPoint2 getMiddleStartPosition() {
		return getMiddlePosition(0, 3);
	}

	protected CPoint2 getMiddleEndPosition() {
		return getMiddlePosition(2, 1);
	}

	protected CPoint2 getMiddlePosition(int a, int b) {
		Transform transform = body.getTransform();
		for(Fixture f=body.getFixtureList(); f!=null; f=f.getNext()) {
			PolygonShape shape = (PolygonShape) f.getShape();
			Vec2 vpos1 = box2d.coordWorldToPixels(Transform.mul(transform, shape.getVertex(a)));
			Vec2 vpos2 = box2d.coordWorldToPixels(Transform.mul(transform, shape.getVertex(b)));
			vpos1.addLocal(vpos2);
			vpos1.mulLocal(0.5f);
			return new CPoint2(vpos1);
		}
		return new CPoint2(0, 0);
	}

	public void draw() {
		// only draw the branch if it is marked as active
		if(activeP) {
			tree.branchDrawer.drawBranch(this);
			drawChildren();
		}
	}

	public void drawChildren() {
		for(Branch branch : branches) {
			branch.draw();
		}
	}

	public Vector<CPoint2> getParentPoints() {
		Vector<CPoint2> parentPoints;
		if(parent != null) {
			parentPoints = parent.getParentPoints();
		} else {
			parentPoints = new Vector<CPoint2>();
		}
		parentPoints.add(getMiddleStartPosition());
		return parentPoints;
	}
}
