package net.qmat.qmhh;

import java.util.ArrayList;

public class SporesModel extends ProcessingObject {
	
	ArrayList<Spore> spores;
	
	public SporesModel() {
		spores = new ArrayList<Spore>();
	}
	
	public void startRipple() {
		for(int i=0; i<40; i++) {
			spores.add(new Spore());
		}
	}
	
	public void removeSpore(Spore spore) {
		spore.destroy();
		spores.remove(spore);
	}
	
	public void draw() {
		for(Spore spore : spores) {
			spore.draw();
		}
	}
	

}
