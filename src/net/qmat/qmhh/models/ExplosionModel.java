package net.qmat.qmhh.models;

import net.qmat.qmhh.Main;

public class ExplosionModel extends ProcessingObject {

	private boolean explodingP = false;
	private boolean resetModelsP = false;
	private int explosionDuration = 2000; // in m.s.
	private int cooldownDuration = 4000; 
	private int startingTimestamp;

	public void ExplosionModel() {

	}

	public void startExplosion() {
		startingTimestamp = p.millis();
		explodingP = true;
		resetModelsP = false;
	}

	public void draw() {
		if(!explodingP) return;
		 
		int interval = p.millis() - startingTimestamp;
		int totalDuration = explosionDuration + cooldownDuration;
		float index = (float)interval/(float)totalDuration;
		float switchoverPoint = (float)explosionDuration / (float)totalDuration;
		
		if(index > 1f) {
			explodingP =  false;
			resetModelsP = false;
			return;
		}
		
		p.pushMatrix();
		p.translate(Main.centerX, Main.centerY);
		p.noStroke();
		// exploding
		if(index < switchoverPoint) {
			p.fill(255, 255, 255, (index/switchoverPoint)*255f);
			p.ellipseMode(Main.CENTER);
			float size = 2f*Main.pow((float)interval/(float)explosionDuration, 4)*Main.outerRingOuterRadius;
			p.ellipse(0, 0, size, size);
		// cooling down
		} else {
			if(!resetModelsP) {
				Models.reset();
				resetModelsP = true;
			}
			p.fill(255, 255, 255, (1f-((index-switchoverPoint)/(1f-switchoverPoint)))*255f);
			p.rectMode(Main.CENTER);
			float size = 2f*Main.outerRingOuterRadius;
			p.rect(0, 0, size, size);
		}
		p.popMatrix();
	}

}
