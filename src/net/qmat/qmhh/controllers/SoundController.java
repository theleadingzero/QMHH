package net.qmat.qmhh.controllers;

public class SoundController {

	public SoundController() {
		
	}
	
	public void creatureHasGrown() {}
	
	public void creatureChangedStage() {}
	
	public void creatureWasBorn() {}
	
	public void plantHasGrown() {}
	
	public void handWasAdded() {}
	
	public void handWasRemoved() {}
	
	public void beamStarted() {}
	
	public void beamStopped() {}
	
	public void beamBlocked() {}
	
	public void beamUnblocked() {}
	
	public void sporesEmitted() {
		Controllers.getOscController().queueSoundEvent("/spores/emitted");
	}


	

}
