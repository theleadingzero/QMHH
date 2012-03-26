
package net.qmat.qmhh.models.creatures;

import net.qmat.qmhh.Main;

import org.jbox2d.common.Vec2;

public class Moth extends CreatureBase {


	int maxStage = 2;

	float flyMin = -w / 9; //how wings can move
	float flyMax = w / 13; //how wings can move
	float flyPos = 1; //temp position for wings animation
	//float angleSpace = Main.TWO_PI / 12;

	int scale_state = 0; //is it scaling?
	int scale; //for scaling up and down when flying
	int alpha;
	
	Moth() {
		super();
		scale = 100;
		setAlpha(scale);
	}
	
	private void setAlpha(int a) {
		alpha = a > 100 ? 100 : a;
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
		// the amount of body segments to draw
		int tempCount = 1;  
		if(stage == 0) 
			tempCount = 1;
		if(stage == 1)
			tempCount = 2;
		if(stage == 2)
			tempCount = 3;

		p.stroke(255, 255, 255, alpha);
		
		/*
		 * w and h are also part of the CreatureBase class and they are
		 * the width and height of the creature. The field p is a reference to
		 * the Processing applet. This has all the draw functions. So if you'd
		 * want to use a Processing function, prepend it with 'p.'.  
		 */
		p.pushMatrix();
		p.translate(position.x, position.y);
		p.rotateZ(angle);
		p.scale(scale / 100.0f);
		drawWings(tempCount);
		p.rotate(-Main.PI);
		
		//p.rectMode(p.CENTER);
		//p.fill(100);
		p.strokeWeight(1);
		p.stroke(255);
		//p.rect(0, 0, w, h);
		p.translate(-w * tempCount / 2.5f/2, -w / 3 * tempCount / 1.5f/2.3f);
		//body parts
				for(int i=tempCount; i > 0; i--) {
					//for the eyes in the first iteration
					if(i == tempCount) drawEyes(i, alpha);
					// main body part
					drawBodyPart(i, alpha);
					//for the tail arcs in the last iteration
					if(i == 1) {
						drawTail();
					}
					// get ready to draw the next bit
					p.translate(0, h * i / 6);
				}
		p.popMatrix();
		
		/*
		 * Remember that all the constants and processing stuff is now part of p
		 * or Main. For example, if you'd like to access TWO_PI you can so like 
		 * this: Main.TWO_PI
		 */
		
		/*
		 * If you'd like to try out your creature change the 
		 * preferences.properties file in /data/. Look for the PR_CREATURE_CLASS
		 * setting and change it to your class' name. 
		 */
		update();
	}
	
	
	
	private void drawBodyPart(int i, float a) {
		//p.fill(255,255,255,29); 
		p.noFill();
		float eW = h/4 * i/5;
		float eH = h/3.5f * i/7;
		float[] offsets = {0, 3, 4, 5, 9};
		float a2 = a;
		//p.stroke(255, 255, 255, a2);
		//p.ellipse(0, 0, eW/9 , eH); 
		for(int j=0; j<offsets.length; j++) {
			a2 /= 1.7;
			p.stroke(255, 255, 255, a2);
			p.ellipse(0, 0, eW + offsets[j], eH + offsets[j]); 
		}
	}

	private void drawEyes(int i, float a) {
		float[] offsets = {0, 2, 5, 9};
		float eW = h / 2 * i / 28;
		float eH = h * i / 38;
		float eY = -h * i / 6;
		float eX = -h / 2 * i / 8;
		float a2 = a;
		p.stroke(255, 255, 255, a2);
		p.fill(255);
		p.ellipse(eX, eY, eW , eH ); 
		p.ellipse(-eX, eY, eW , eH);
		/*for(int j=0; j<offsets.length; j++) {
			a2 /= 1.7;
			p.stroke(255, 255, 255, a2);
			p.noFill();
			p.ellipse(eX, eY, eW + offsets[j], eH + offsets[j]); 
			p.ellipse(-eX, eY, eW + offsets[j], eH + offsets[j]); 
		}*/
	}

	private void drawTail() {
		p.stroke(255, 255, 255, alpha);
		p.strokeWeight(1);
		p.translate(0, h / 3);
		p.noFill();
		p.arc(0, 0,        h / 3,   h / 4,  Main.PI - Main.PI / 10, Main.TWO_PI + Main.PI / 10);
		p.arc(0, h / 16,   h / 2.2f, h / 4,  Main.PI,         Main.TWO_PI);
	}

	private void drawWings(int tempCount) {
		//for the wings
		p.rotate(Main.PI);
		p.strokeWeight(1);
		//p.noFill();
		p.fill(255, 255, 255, alpha/11);
		float hDiff = (h * tempCount / 3.0f - w / 3.0f * tempCount / 1.5f) / 2.0f;

		p.ellipse(0, hDiff, w * tempCount / 2.5f, w / 3 * tempCount / 1.5f); 
		p.ellipse(0, flyPos, w * tempCount / 2.5f + flyPos * 2, w / 3 * tempCount/1.5f); 

	}

	//on and off wings
	public void update() {
		if(flyPos >= 0) {
			if(flyPos < flyMax)
				flyPos = flyPos + 0.7f;
			else
				flyPos = -1;
		} else if(flyPos < 0) {
			if(flyPos > flyMin)
				flyPos = flyPos - 0.3f;
			else
				flyPos = 0.0f;
		}
	}
	
	
}


