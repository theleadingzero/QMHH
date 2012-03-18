package net.qmat.qmhh.models;

import java.util.ArrayList;

import net.qmat.qmhh.Main;
import net.qmat.qmhh.controllers.Controllers;
import net.qmat.qmhh.utils.CPoint2;
import net.qmat.qmhh.utils.PPoint2;
import net.qmat.qmhh.utils.Settings;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

public class Tree extends ProcessingObject {
	
	private static int BRANCH_LEVELS = 5;
	private static float BRANCH_START_LENGTH = 100.0f;
	private static Long BRANCH_GROW_TIME = 6L * 1000000000L; // 4 seconds in nanoseconds
	
	private float internalAngle;
	private Branch root;
	
	private int branchGroup;
	
	
	public Tree(float startAngle, float maxInternalAngle, Body centerBody) {
		branchGroup = Settings.getInteger(Settings.PR_BRANCH_COLLISION_GROUP);
		this.internalAngle = p.random(1.0f/16.0f * Main.TWO_PI, maxInternalAngle);
		buildTree(BRANCH_START_LENGTH, BRANCH_LEVELS, startAngle, centerBody);
	}
	
	
	
	private void buildTree(float startLength, 
						   int levels, 
						   float startAngle, 
						   Body centerBody) {
		CPoint2 cpos = new PPoint2(10.0f, -startAngle).toCPoint2();
		root = new Branch(null, 
						  startLength, 
						  levels, 
						  cpos.x, //Main.centerX, 
						  cpos.y, //Main.centerY, 
						  startAngle);
		root.activate();

		RevoluteJointDef rjd = new RevoluteJointDef();
        rjd.bodyA = centerBody;
        rjd.bodyB = root.body;
        rjd.collideConnected = false;
        Vec2 anchor = box2d.coordPixelsToWorld(new Vec2(Main.centerX, Main.centerY));
        rjd.localAnchorA = centerBody.getLocalPoint(anchor);
        rjd.localAnchorB = root.body.getLocalPoint(anchor);
        rjd.lowerAngle = -startAngle - 0.9f * internalAngle;
        rjd.upperAngle = -startAngle + 0.9f * internalAngle;
        rjd.enableLimit = true;
        box2d.createJoint(rjd);
	}
	
	public void draw() {
		p.stroke(0, 255, 0);
		p.noFill();
		p.ellipse(Main.centerX, Main.centerY, 20.0f, 20.0f);
		root.draw();
	}
	
	private float normalizeAngle(float angle) {
		float na = angle;
		while(na < 0.0f) na += Main.TWO_PI;
		return na % Main.TWO_PI;
	}
	
	public class Branch {
		
		private float length;
		private ArrayList<Branch> branches = new ArrayList<Branch>();
		private Body body;
		private Branch parent;
		
		private float startX, startY;
		private float endX, endY;
		
		private Long startGrowTimestamp;
		private boolean activeP;
		private boolean stoppedGrowing;
		private Long startSproutTimestamp;
		
		public Branch(Branch parent, 
					  float length, 
					  int levels, 
					  float x, 
					  float y, 
					  float angle) {
			this.parent = parent;
			this.length = p.random(0.5f * length, 1.2f * length);
			
			activeP = false;
			stoppedGrowing = false;
			startSproutTimestamp = 0L;
			
			endX = x + this.length * Main.cos(angle);
			endY = y + this.length * Main.sin(angle);
			startX = x;
			startY = y;
			
			makeBody(x, y, endX, endY, angle);

			if(levels > 0) {
				Branch b1 = new Branch(this, 
										0.6f * length, 
										levels-1,
										endX,
										endY,
										angle - internalAngle);
				if(b1.withinEcosystemP())
					branches.add(b1);
				Branch b2 = new Branch(this, 
						0.6f * length, 
						levels-1,
						endX,
						endY,
						angle + internalAngle);
				if(b1.withinEcosystemP())
					branches.add(b2);
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
			return System.nanoTime() - startGrowTimestamp < BRANCH_GROW_TIME;
		}
		
		public void sprout() {
			// pick one of the sub branches and activate it
			Long now = System.nanoTime();
			if((now - startSproutTimestamp) > BRANCH_GROW_TIME) {
				startSproutTimestamp = now;
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
		    fd.density = 0.1f;
		    fd.friction = 0.2f;
		    fd.restitution = 0.03f;
		    fd.filter.groupIndex = branchGroup;

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
        		rjd.lowerAngle = -internalAngle;
		        rjd.upperAngle = internalAngle;
		        rjd.enableLimit = true;
		        box2d.createJoint(rjd);
		    }
		}
		
		public void draw() {
			if(activeP) {
				float alpha = 255.0f;
				if(!stoppedGrowing) {
					alpha = 255.0f * (float)((double)(System.nanoTime() - startGrowTimestamp) / (double)BRANCH_GROW_TIME);
					if(alpha >= 255.0f)
						stoppedGrowing = true;
				}
				p.stroke(0, 100, 0, alpha);
				p.fill(0, 255, 0, alpha);
				Transform transform = body.getTransform();
				p.beginShape();
				Vec2 pos;
				for(Fixture f=body.getFixtureList(); f!=null; f=f.getNext()) {
					PolygonShape shape = (PolygonShape) f.getShape();
					for(int i=0; i<shape.getVertexCount(); i++) {
						pos = box2d.coordWorldToPixels(Transform.mul(transform, shape.getVertex(i)));
						p.vertex(pos.x, pos.y);
					}
				}
				p.endShape(Main.CLOSE);
				
				for(Branch branch : branches) {
					branch.draw();
				}
			}
		}
	}
		
}
