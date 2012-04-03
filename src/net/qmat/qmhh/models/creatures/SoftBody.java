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
	int nodes = 8;
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
		Vec2 velocity = body.getLinearVelocity();
		float angle = Main.atan2(velocity.x, velocity.y);
		/*
		 * w and h are also part of the CreatureBase class and they are
		 * the width and height of the creature. The field p is a reference to
		 * the Processing applet. This has all the draw functions. So if you'd
		 * want to use a Processing function, prepend it with 'p.'.  
		 */

		

		/*
		 * SoftBody part
		 * calculate node  starting locations
		 */
		int temp=4;
		if(stage == 0){ 
			temp = 8;
			p.curveTightness(0.0f);
		}
		if(stage == 1){
			temp = 4;
			p.curveTightness(3.9f);
		}
		if(stage == 2){
			temp = 6;
			p.curveTightness(3.9f);
		}
		  for (int i=0; i<temp; i++){
		    nodeStartX[i] = centerX+Main.cos(Main.radians(rotAngle))*radius1;
		    nodeStartY[i] = centerY+Main.sin(Main.radians(rotAngle))*radius1;
		    rotAngle += 360.0f/temp;
		  }
		  //drawTail();
		  
		  // draw poly as shadow
		  p.noStroke();
		  p.pushMatrix();
		  p.translate(Main.centerX,Main.centerY);
		  p.scale(1.3f);
		  p.translate(position.x-Main.centerX, position.y-Main.centerY);
		  p.scale(1.5f);
		  //p.translate(-(Main.centerX-Main.centerX/1.3f),-(Main.centerY-Main.centerY/1.3f));
		  p.rotateZ(angle);
		  p.fill(150, 180, 120, 50);
		  drawPoly(temp, false);
		  p.popMatrix();
		  
		  
		  p.pushMatrix();
		  p.translate(position.x, position.y);
		  p.rotateZ(angle);
		  // draw real poly 
		  //p.curveTightness(organicConstant);
		  p.fill(255,190,230,90);
		  //first layer
		  //p.noStroke();
		  p.strokeWeight(0.1f);
		  p.stroke(255,0,255,110);
		  drawPoly(temp,true);
		  //star
		  p.strokeWeight(0.08f);
		  p.stroke(255,60,180,70);
		  //p.noStroke();
		  p.fill(255,255,255,140);
		  p.ellipse(0, 0, radius1*1.3f/2, radius1*1.3f/9);
		  p.ellipse(0, 0, radius1*1.3f/9, radius1*1.3f/2);
		  p.noStroke();
		  p.fill(255,255,255,220);
		  p.ellipse(-radius1/22, -radius1/22, radius1/11, radius1/11);	  
		  p.popMatrix();
		
		/*
		 * Remember that all the constants and processing stuff is now part of p
		 * or Main. For example, if you'd like to access TWO_PI you can so like 
		 * this: Main.TWO_PI
		 */
		morf(position.x, position.y, temp);
		/*
		 * If you'd like to try out your creature change the 
		 * preferences.properties file in /data/. Look for the PR_CREATURE_CLASS
		 * setting and change it to your class' name. 
		 */
	}
	private void drawPoly(int temp, boolean m){
		 p.beginShape();
		  for (int i=0; i<temp+3; i++){
			  p.curveVertex(nodeX[i%temp], nodeY[i%temp]);
		  }/*
		  for (int i=0; i<nodes-1; i++){
			  p.curveVertex(nodeX[i], nodeY[i]);
		  }*/
		  p.endShape(Main.CLOSE); 
		  //second layer
		  p.beginShape();
		  if(m){
		  p.strokeWeight(0.2f);
		  p.stroke(255,190,230,70);
		  } else p.noStroke();
		  for (int i=0; i<temp+3; i++){
			  p.curveVertex(nodeX[i%temp]*2/3, nodeY[i%temp]*2/3);
		  }/*
		  for (int i=0; i<nodes-1; i++){
			  p.curveVertex(nodeX[i], nodeY[i]);
		  }*/
		  p.endShape(Main.CLOSE); 
		  //third layer
		  if(m){
		  p.strokeWeight(0.1f);
		  p.stroke(190, 90, 205,110);
		  } else p.noStroke();
		  p.beginShape();
		   for (int i=0; i<temp+3; i++){
			  p.curveVertex(nodeX[i%temp]*1.3f/3, nodeY[i%temp]*1.1f/3);
		  }
		   /*for (int i=0; i<nodes-1; i++){
			  p.curveVertex(nodeX[i], nodeY[i]);
		  }*/
 
		  p.endShape(Main.CLOSE); 
	}
	private void morf(float x, float y, int temp){
		
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
		  for (int i=0; i<temp; i++){
		    nodeX[i] = nodeStartX[i]+Main.sin(Main.radians(angle[i]))*(accelX*2);
		    nodeY[i] = nodeStartY[i]+Main.sin(Main.radians(angle[i]))*(accelY*2);
		    angle[i]+=frequency[i];
		  }
	}
	

}



