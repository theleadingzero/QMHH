package net.qmat.qmhh.models.creatures;

import net.qmat.qmhh.Main;

import org.jbox2d.common.Vec2;

//import processing.core.PApplet;
//import processing.core.PConstants;

public class SoftBody extends CreatureBase {
	private float centerX=0, centerY=0;
	private float radius1 = 0, rotAngle = -90;
	private float accelX, accelY;
	private float springing = .0009f, damping = .98f;

	//corner nodes
	int nodes = 0;
	private float nodeStartX[];
	private float nodeStartY[];
	private float[]nodeX;
	private float[]nodeY;
	private float[]angle;
	private float[]frequency;
	private float lastX, lastY;

	// soft-body dynamics
	private float organicConstant = 1;  
	

	SoftBody() {
		super();
		if(stage == 0) 
			nodes = 5;
		if(stage == 1)
			nodes = 8;
		if(stage == 2)
			nodes = 11;
		nodeStartX = new float[nodes];
		nodeStartY = new float[nodes];
		nodeX = new float[nodes];
		nodeY = new float[nodes];
		angle = new float[nodes];
		frequency = new float[nodes];
		
		// iniitalize frequencies for corner nodes
		  for (int i=0; i<nodes; i++){
		    frequency[i] = p.random(5, 12);
		  } 
		  
		lastX = Main.centerX;
		lastY = Main.centerY;
	}

	public void draw() {
		radius1=Main.sqrt( w*w+h*h)*0.5f;
		/*
		 * body is part of the CreatureBase class and we can use it to get
		 * the creature's position and angle.
		 */
		Vec2 position = box2d.getBodyPixelCoord(body);
		float angleB = body.getAngle();
		// calculate the angle from the linear velocity
		//Vec2 velocity = body.getLinearVelocity();
		//float angle = Main.atan2(velocity.x, velocity.y);
		//centerY=position.y;
		//centerX=position.x;
		/*
		 * w and h are also part of the CreatureBase class and they are
		 * the width and height of the creature. The field p is a reference to
		 * the Processing applet. This has all the draw functions. So if you'd
		 * want to use a Processing function, prepend it with 'p.'.  
		 */
		p.pushMatrix();
		p.translate(position.x, position.y);
		//p.rotateZ(angleB);
		//p.fill(100);
		p.stroke(255);
		
		/*
		 * SoftBody part
		 * calculate node  starting locations
		 */

		  for (int i=0; i<nodes; i++){
		    nodeStartX[i] = centerX+Main.cos(Main.radians(rotAngle))*radius1;
		    nodeStartY[i] = centerY+Main.sin(Main.radians(rotAngle))*radius1;
		    rotAngle += 360.0f/nodes;
		  }
		  //drawTail();
		  
		  // draw polygon
		  
		  //p.curveTightness(organicConstant);
		  p.fill(255);
		  p.noStroke();
		  p.beginShape();
		  for (int i=0; i<nodes+3; i++){
			  p.curveVertex(nodeX[i%nodeX.length], nodeY[i%nodeY.length]);
		  }/*
		  for (int i=0; i<nodes-1; i++){
			  p.curveVertex(nodeX[i], nodeY[i]);
		  }*/
		  p.endShape(Main.CLOSE); 
		  
		p.popMatrix();
		
		/*
		 * Remember that all the constants and processing stuff is now part of p
		 * or Main. For example, if you'd like to access TWO_PI you can so like 
		 * this: Main.TWO_PI
		 */
		morf(position.x, position.y);
		/*
		 * If you'd like to try out your creature change the 
		 * preferences.properties file in /data/. Look for the PR_CREATURE_CLASS
		 * setting and change it to your class' name. 
		 */
	}
	
	private void morf(float x, float y){
		
		/*
		 * for the continious "morfing"
		 */
		//move center point
		  float deltaX = (lastX-x) * 100f + 20f; //mouseX-centerX;
		  float deltaY = (lastY-y) * 100f + 20f; //mouseY-centerY;
		  lastX = x;
		  lastY = y;

		  // create springing effect
		  deltaX *= springing;
		  deltaY *= springing;
		  accelX += deltaX;
		  accelY += deltaY;

		  // move predator's center
		  //centerX += accelX;
		  //centerY += accelY;

		  // slow down springing
		  accelX *= damping;
		  accelY *= damping;

		  // change curve tightness
		  organicConstant = 1f + (float)(1f-((Main.abs(accelX)+Main.abs(accelY))*.1f));

		  //move nodes
		  for (int i=0; i<nodes; i++){
		    nodeX[i] = nodeStartX[i]+Main.sin(Main.radians(angle[i]))*(accelX*2);
		    nodeY[i] = nodeStartY[i]+Main.sin(Main.radians(angle[i]))*(accelY*2);
		    angle[i]+=frequency[i];
		  }
	}
	

}



