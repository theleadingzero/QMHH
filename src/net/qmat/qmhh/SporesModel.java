package net.qmat.qmhh;

import java.util.ArrayList;
import java.util.Iterator;

import org.jbox2d.common.Vec2;

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
	
	public void update() {
		// check if the spores are outside of the outer ring
		Iterator<Spore> it = spores.iterator();
		while(it.hasNext()) {
			Spore spore = it.next();
			PPoint2 ppos = spore.getPPosition();
			if(ppos.r > Main.outerRingOuterRadius) {
				spore.destroy();
				it.remove();
			}
		}
	}
	
	public void draw() {
		for(Spore spore : spores) {
			spore.draw();
		}
	}
	
}
