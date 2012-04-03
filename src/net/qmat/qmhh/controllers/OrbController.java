package net.qmat.qmhh.controllers;

import net.qmat.qmhh.Main;
import net.qmat.qmhh.models.Models;

public class OrbController {
	
	Long emitInterval = 10L * 1000000000L;
	Long orbRadiusInterval = 1L * 1000000000L;
	Long emitPauseTime = 4L * 1000000000L;
	Long lastTimestamp = 0L;
	Long lastEmitTimestamp = 0L;
	Long timeToWait;
	int nrHands;
	boolean rippleEmittedP = true;
	float baseRadius = 38.0f;
	float ellipseBaseRadius = 5.0f;
	float extraRadius = 30.0f;
	Double rippleIndex = 1.1;
	
	public OrbController() {
		 timeToWait = emitInterval;
		 lastEmitTimestamp = System.nanoTime();
	}
	
	public void update() {
		Long now = System.nanoTime();
		Long interval = now - lastTimestamp;
		nrHands = Models.getHandsModel().nrUnblockedHands();
		if(nrHands > 0) {
			Long timeToAdvance = (long)((1.0+Math.log((double)nrHands)) * (double)interval);
			timeToWait -= timeToAdvance;
			if(timeToWait <= 0) {
				emit();
			}
		}
		Models.getOrbModel().setChargeIndex((emitInterval-timeToWait)/(float)emitInterval);
		// start ripple in the middle of the orbRadiusInterval
		if(rippleIndex < 1.0) {
			rippleIndex = (now - lastEmitTimestamp) / orbRadiusInterval.doubleValue();
			//float newRadius = Main.sin(rippleIndex.floatValue() * Main.PI) * extraRadius + ellipseBaseRadius;
			//Models.getOrbModel().setRadius(newRadius);
			if(rippleIndex > 0.5 && !rippleEmittedP) {
				Models.getSporesModel().startRipple(17.0f);
				rippleEmittedP = true;
				Controllers.getSoundController().sporesEmitted();
			}
		} else {
			Models.getOrbModel().setParticleRadius(baseRadius);
		}
		lastTimestamp = now;
	}
	
	private void emit() {
		timeToWait = emitInterval+emitPauseTime;
		if(Main.p.random(1.0f) > 0.5f)
			Models.getCreaturesModel().addCreature();
		Models.getOrbModel().setParticleRadius(extraRadius+baseRadius);
		lastEmitTimestamp = System.nanoTime();
		rippleEmittedP = false;
		rippleIndex = 0.0d;
	}

}
