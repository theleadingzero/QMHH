package net.qmat.qmhh.models;

import java.util.ArrayList;
import java.util.Iterator;

import net.qmat.qmhh.Main;
import net.qmat.qmhh.utils.PPoint2;

import org.jbox2d.common.Vec2;

public class SporesModel extends ProcessingObject {
	
	ArrayList<Spore> spores;
	
	public SporesModel() {
		spores = new ArrayList<Spore>();
	}
	
	public void startRipple(float startRadius) {
		for(int i=0; i<40; i++) {
			spores.add(new Spore(startRadius));
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
			if(ppos.r > Main.outerRingOuterRadius ||
			   spore.getAbsoluteVelocity() < 1.0f) {
				spore.destroy();
				it.remove();
			}
		}
	}
	
	public void draw() {
		p.ellipseMode(Main.CENTER);
		p.stroke(150, 150, 255);
		p.strokeWeight(2.0f);
		for(Spore spore : spores) {
			spore.draw();
		}
		p.strokeWeight(1.0f);
	}
	
}
