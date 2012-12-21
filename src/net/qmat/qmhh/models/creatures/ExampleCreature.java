package net.qmat.qmhh.models.creatures;

import org.jbox2d.common.Vec2;

public class ExampleCreature extends CreatureBase {

	ExampleCreature() {
		super();
	}

	public void draw() {

		/*
		 * body is part of the CreatureBase class and we can use it to get
		 * the creature's position and angle.
		 */
		Vec2 position = box2d.getBodyPixelCoord(body);
		float angle = body.getAngle();
		
		/*
		 * w and h are also part of the CreatureBase class and they are
		 * the width and height of the creature. The field p is a reference to
		 * the Processing applet. This has all the draw functions. So if you'd
		 * want to use a Processing function, prepend it with 'p.'.  
		 */
		p.pushMatrix();
		p.translate(position.x, position.y);
		p.rotateZ(angle);
		p.rectMode(p.CENTER);
		p.fill(100);
		p.stroke(255);
		p.rect(0, 0, w, h);
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
	}
	
	
}


