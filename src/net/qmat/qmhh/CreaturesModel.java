package net.qmat.qmhh;

import java.util.ArrayList;

public class CreaturesModel extends ProcessingObject {
	
	private ArrayList<Creature> creatures;
	
	public CreaturesModel() {
		creatures = new ArrayList<Creature>();
	}
	
	public void addCreature() {
		creatures.add(new Creature());
	}
	
	public void update() {
		for(Creature creature : creatures) {
			creature.update(creatures);
		}
	}
	
	@SuppressWarnings("static-access")
	public void draw() {
		p.rectMode(p.CENTER);
		p.noFill();
		p.stroke(200);
		for(Creature creature : creatures) {
			creature.draw();
		}
	}
	
	

}
