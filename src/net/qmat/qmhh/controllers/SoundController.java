package net.qmat.qmhh.controllers;

import net.qmat.qmhh.models.Models;
import net.qmat.qmhh.models.creatures.CreatureBase;
import net.qmat.qmhh.utils.CreaturesInfo;

public class SoundController {

	public SoundController() {}
	
	public void creatureHasGrown(CreatureBase creature) {
		CreaturesInfo ci = new CreaturesInfo();
		Controllers.getOscController().queueSoundEvent(
				"/creatures/grown", 
				creature.stage,
				ci.nrCreatures,
				ci.nrEvs[0],
				ci.nrEvs[1],
				ci.nrEvs[2]);
	}
	
	public void creatureChangedStage(CreatureBase creature) {
		CreaturesInfo ci = new CreaturesInfo();
		Controllers.getOscController().queueSoundEvent(
				"/creatures/stage", 
				creature.stage,
				ci.nrCreatures,
				ci.nrEvs[0],
				ci.nrEvs[1],
				ci.nrEvs[2]);
	}
	
	public void creatureWasBorn() {
		CreaturesInfo ci = new CreaturesInfo();
		Controllers.getOscController().queueSoundEvent(
				"/creatures/new",
				ci.nrCreatures,
				ci.nrEvs[0],
				ci.nrEvs[1],
				ci.nrEvs[2]);
	}
	
	public void plantHasGrown() {
		Controllers.getOscController().queueSoundEvent("/plants/grown");
	}
	
	public void handWasAdded() {
		int nrHands = Models.getHandsModel().nrHands();
		Controllers.getOscController().queueSoundEvent("/hands/added", nrHands);
	}
	
	public void handWasRemoved() {
		int nrHands = Models.getHandsModel().nrHands();
		Controllers.getOscController().queueSoundEvent("/hands/removed", nrHands);
	}
	
	public void beamStarted() {
		sendBeamEvent("/beams/started");
	}
	
	public void beamStopped() {
		sendBeamEvent("/beams/stopped");
	}
	
	public void beamBlocked() {
		sendBeamEvent("/beams/blocked");
	}
	
	public void beamUnblocked() {
		sendBeamEvent("/beams/unblocked");
	}
	
	private void sendBeamEvent(String endPoint) {
		int nrHands = Models.getHandsModel().nrHands();
		int nrUnblockedHands = Models.getHandsModel().nrUnblockedHands();
		Controllers.getOscController().queueSoundEvent(
				endPoint, 
				nrHands-nrUnblockedHands,
				nrUnblockedHands);
	}
	
	public void sporesEmitted() {
		Controllers.getOscController().queueSoundEvent("/spores/emitted");
	}


	

}
