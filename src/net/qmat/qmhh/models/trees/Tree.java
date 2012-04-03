/*
 * N.B. only change the draw function in the Branch class
 */

package net.qmat.qmhh.models.trees;

import net.qmat.qmhh.Main;
import net.qmat.qmhh.models.ProcessingObject;
import net.qmat.qmhh.utils.CPoint2;
import net.qmat.qmhh.utils.PPoint2;
import net.qmat.qmhh.utils.Settings;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

public class Tree extends ProcessingObject {
	
	public static int MAX_BRANCH_LEVELS = 5;
	public static float BRANCH_START_LENGTH = 80.0f;
	public static float BRANCH_NEXT_LENGTH_MP = 0.95f;
	public static Long BRANCH_GROW_TIME = 6L * 1000000000L; // 4 seconds in nanoseconds
	
	public float internalAngle;
	private Branch root;
	
	public int branchGroup;
	
	public Class<? extends BranchDrawerBase> branchDrawerClass;
	public BranchDrawerBase branchDrawer;
	
	@SuppressWarnings("unchecked")
	public Tree(float startAngle, float maxInternalAngle, Body centerBody) {
		branchGroup = Settings.getInteger(Settings.PR_BRANCH_COLLISION_GROUP);
		this.internalAngle = p.random(1.0f/16.0f * Main.TWO_PI, maxInternalAngle);
		String clName = "net.qmat.qmhh.models.trees." + Settings.getString(Settings.PR_BRANCH_DRAWER);
		try {
			branchDrawerClass = (Class<? extends BranchDrawerBase>) Class.forName(clName);
			branchDrawer = branchDrawerClass.newInstance();
		} catch (Exception e) {
			System.err.println("Something went wrong while loading the branch drawer class: " + clName);
			e.printStackTrace();
		}
		buildTree(BRANCH_START_LENGTH, startAngle, centerBody);
	}
	
	private void buildTree(float startLength, 
						   float startAngle, 
						   Body centerBody) {
		CPoint2 cpos = new PPoint2(10.0f, -startAngle).toCPoint2();
		try {
			root = new Branch(this,
							  null, 
							  startLength,
							  BRANCH_NEXT_LENGTH_MP,
							  0, 
							  cpos.x,  
							  cpos.y,  
							  startAngle);
		} catch(Exception e) {
			System.err.println("You've probably specified some weird parameters for the tree, couldn't create the root branch.");
		}
		root.activate();

		RevoluteJointDef rjd = new RevoluteJointDef();
        rjd.bodyA = centerBody;
        rjd.bodyB = root.body;
        rjd.collideConnected = false;
        Vec2 anchor = box2d.coordPixelsToWorld(new Vec2(Main.centerX, Main.centerY));
        rjd.localAnchorA = centerBody.getLocalPoint(anchor);
        rjd.localAnchorB = root.body.getLocalPoint(anchor);
        rjd.lowerAngle = -startAngle - 1.9f * internalAngle;
        rjd.upperAngle = -startAngle + 1.9f * internalAngle;
        rjd.enableLimit = true;
        box2d.createJoint(rjd);
	}
	
	public void draw() {
		p.stroke(0, 255, 0);
		p.fill(0, 55, 0);
		p.ellipseMode(Main.CENTER);
		p.ellipse(Main.centerX, Main.centerY, 20.0f, 20.0f);
		root.draw();
	}
	
	private float normalizeAngle(float angle) {
		float na = angle;
		while(na < 0.0f) na += Main.TWO_PI;
		return na % Main.TWO_PI;
	}
		
}
