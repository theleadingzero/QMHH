package net.qmat.qmhh.controllers;

import net.qmat.qmhh.models.Models;

public class OrbController {
	
	Long emitInterval = 4L * 1000000000L;
	Long lastTimestamp = 0L;
	Long lastEmitTimestamp = 0L;
	Long timeToWait;
	int nrHands;
	
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
		lastTimestamp = now;
	}
	
	private void emit() {
		// TODO: emit more creatures when the interval was shorter
		timeToWait = emitInterval;
		Models.getSporesModel().startRipple(Models.getOrbModel().getRadius());
		Models.getCreaturesModel().addCreature();
		lastEmitTimestamp = System.nanoTime();
		Controllers.getSoundController().sporesEmitted();
	}

}
