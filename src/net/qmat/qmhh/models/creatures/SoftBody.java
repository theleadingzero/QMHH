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
	private float nodeStartX[] = new float[nodes];
	private float nodeStartY[] = new float[nodes];
	private float[]nodeX = new float[nodes];
	private float[]nodeY = new float[nodes];
	private float[]angle = new float[nodes];
	private float[]frequency = new float[nodes];

	// soft-body dynamics
	private float organicConstant = 1;  
	

	SoftBody() {
		super();
		//radius1=PApplet.pow( w*w+h*h,0.5f);	
		radius1=15;	
		if(stage == 0) 
			nodes = 4;
		if(stage == 1)
			nodes = 6;
		if(stage == 2)
			nodes = 8;
		// iniitalize frequencies for corner nodes
		  for (int i=0; i<nodes; i++){
		    frequency[i] = p.random(5, 12);
		  } 
	}

	public void draw() {

		/*
		 * body is part of the CreatureBase class and we can use it to get
		 * the creature's position and angle.
		 */
		Vec2 position = box2d.getBodyPixelCoord(body);
		//float rotationalAngle = body.getAngle();
		// calculate the angle from the linear velocity
		Vec2 velocity = body.getLinearVelocity();
		float angle = Main.atan2(velocity.x, velocity.y);
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
		p.rotateZ(angle);
		p.rectMode(Main.CENTER);
		p.fill(100);
		p.stroke(255);
		
		/*
		 * SoftBody part
		 * calculate node  starting locations
		 */

		  for (int i=0; i<nodes; i++){
		    nodeStartX[i] = centerX+Main.cos(Main.radians(rotAngle))*radius1;
		    nodeStartY[i] = centerY+Main.sin(Main.radians(rotAngle))*radius1;
		    rotAngle += 360.0/nodes;
		  }
		  drawTail();
		  
		  // draw polygon
		  /*
		  p.curveTightness(organicConstant);
		  p.fill(255);
		  p.beginShape();
		  for (int i=0; i<nodes; i++){
			  p.curveVertex(nodeX[i], nodeY[i]);
		  }
		  for (int i=0; i<nodes-1; i++){
			  p.curveVertex(nodeX[i], nodeY[i]);
		  }
		  p.endShape(PConstants.CLOSE); 
		  */
		  p.ellipse(0, 0, 20 , 20 );
		p.popMatrix();
		
		/*
		 * Remember that all the constants and processing stuff is now part of p
		 * or Main. For example, if you'd like to access TWO_PI you can so like 
		 * this: Main.TWO_PI
		 */
		//morf();
		/*
		 * If you'd like to try out your creature change the 
		 * preferences.properties file in /data/. Look for the PR_CREATURE_CLASS
		 * setting and change it to your class' name. 
		 */
	}
	
	private void morf(){
		
		/*
		 * for the continious "morfing"
		 */
		//move center point
		  float deltaX = centerX; //mouseX-centerX;
		  float deltaY = centerY; //mouseY-centerY;

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
		  organicConstant = (float) (1-((p.abs(accelX)+Main.abs(accelY))*.1));

		  //move nodes
		  for (int i=0; i<nodes; i++){
		    nodeX[i] = nodeStartX[i]+Main.sin(Main.radians(angle[i]))*(accelX*2);
		    nodeY[i] = nodeStartY[i]+Main.sin(Main.radians(angle[i]))*(accelY*2);
		    angle[i]+=frequency[i];
		  }
	}
	
	
	private void drawTail() {
		p.stroke(255, 255, 255, 255);
		p.strokeWeight(2);
		p.translate(0, h / 3);
		p.noFill();
		p.arc(0, 0,        h / 3,   h / 4,  Main.PI - Main.PI / 10, Main.TWO_PI + Main.PI / 10);
		p.arc(0, h / 16,   h / 2.2f, h / 4,  Main.PI,         Main.TWO_PI);
	}
}



