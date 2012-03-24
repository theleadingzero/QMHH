package net.qmat.qmhh.models.trees;

import java.util.ArrayList;

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
	protected Branch parent;

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
			int levels, 
			float x, 
			float y, 
			float angle) {
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

		makeBody(x, y, endX, endY, angle);

		if(this.withinEcosystemP()) {
			if(levels > 0) {
				Branch b1 = new Branch(tree,
						this, 
						0.8f * length, 
						levels-1,
						endX,
						endY,
						angle - tree.internalAngle);
				if(b1.withinEcosystemP())
					branches.add(b1);
				Branch b2 = new Branch(tree, 
						this, 
						0.8f * length, 
						levels-1,
						endX,
						endY,
						angle + tree.internalAngle);
				if(b1.withinEcosystemP())
					branches.add(b2);
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
			float angle) {
		// Define a polygon (this is what we use for a rectangle)
		PolygonShape sd = new PolygonShape();
		sd.setAsBox(box2d.scalarPixelsToWorld(length/2.0f), 
				box2d.scalarPixelsToWorld(1.0f));

		// Define a fixture
		FixtureDef fd = new FixtureDef();
		fd.shape = sd;
		// Parameters that affect physics
		fd.density = 0.01f;
		fd.friction = 0.2f;
		fd.restitution = 0.3f;
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
}