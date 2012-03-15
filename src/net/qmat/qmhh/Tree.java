package net.qmat.qmhh;

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

public class Tree extends ProcessingObject {
	
	private static int BRANCH_LEVELS = 5;
	private static float BRANCH_START_LENGTH = 100.0f;
	
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
		root = new Branch(null, 
						  startLength, 
						  levels, 
						  Main.centerX, 
						  Main.centerY, 
						  startAngle);

		RevoluteJointDef rjd = new RevoluteJointDef();
        rjd.bodyA = centerBody;
        rjd.bodyB = root.body;
        rjd.collideConnected = false;
        Vec2 anchor = box2d.coordPixelsToWorld(new Vec2(Main.centerX, Main.centerY));
        rjd.localAnchorA = centerBody.getLocalPoint(anchor);
        rjd.localAnchorB = root.body.getLocalPoint(anchor);
        rjd.lowerAngle =  Main.PI / 2.0f - startAngle - 0.25f * internalAngle;
        rjd.upperAngle =  Main.PI / 2.0f - startAngle + 0.25f * internalAngle;
        rjd.enableLimit = true;
        box2d.createJoint(rjd);
	}
	
	public void draw() {
		p.fill(0, 255, 0);
		root.draw();
	}
	
	private class Branch {
		
		float length;
		ArrayList<Branch> branches = new ArrayList<Branch>();
		Body body;
		Branch parent;
		
		public Branch(Branch parent, 
					  float length, 
					  int levels, 
					  float x, 
					  float y, 
					  float angle) {
			this.parent = parent;
			this.length = p.random(0.5f * length, 1.2f * length);
			
			float endX, endY;
			endX = x + this.length * Main.cos(angle);
			endY = y + this.length * Main.sin(angle);
			makeBody(x, y, endX, endY, angle);

			if(levels > 0) {
					branches.add(new Branch(this, 
											0.6f * length, 
											levels-1,
											endX,
											endY,
											angle - internalAngle));
					branches.add(new Branch(this, 
								            0.6f * length, 
								            levels-1,
								            endX,
								            endY,
								            angle + internalAngle));
			}
		}
		
		private void makeBody(float x, 
							  float y, 
							  float endX,
							  float endY,
							  float angle) {
		    // Define a polygon (this is what we use for a rectangle)
		    PolygonShape sd = new PolygonShape();
		    sd.setAsBox(box2d.scalarPixelsToWorld(1.0f), 
		    			box2d.scalarPixelsToWorld(length/2.0f));

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
		    bd.angle = 0.5f * Main.PI - angle;

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
		        rjd.lowerAngle = -1.0f * internalAngle;
		        rjd.upperAngle =  1.0f * internalAngle;
		        rjd.enableLimit = true;
		        box2d.createJoint(rjd);
		    }
		}
		
		public void draw() {
			
			p.pushMatrix();
			Vec2 bodyPos = box2d.getBodyPixelCoord(body);
			p.fill(0, 0, 255);
			//p.translate(bodyPos.x, bodyPos.y);
			//p.rotate(-body.getAngle());
			p.beginShape();
			for(Fixture f=body.getFixtureList(); f!=null; f=f.getNext()) {
				PolygonShape shape = (PolygonShape) f.getShape();
				Transform transform = body.getTransform();
				for(int i=0; i<shape.getVertexCount(); i++) {
					Vec2 pos = shape.getVertex(i);
					Vec2 v2 = box2d.coordWorldToPixels(transform.mul(transform, pos));
					p.vertex(v2.x, v2.y);
				}
			}
			p.endShape();
			p.popMatrix();
			
			for(Branch branch : branches) {
				branch.draw();
			}
		}
	}

	
}
