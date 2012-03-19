package net.qmat.qmhh.models.creatures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.qmat.qmhh.Main;
import net.qmat.qmhh.controllers.Controllers;
import net.qmat.qmhh.models.ProcessingObject;
import net.qmat.qmhh.utils.PPoint2;
import net.qmat.qmhh.utils.Settings;

public class CreaturesModel extends ProcessingObject {
	
	public ArrayList<CreatureBase> creatures;
	private Class<? extends CreatureBase> creatureClass;
	
	@SuppressWarnings("unchecked")
	public CreaturesModel() {
		creatures = new ArrayList<CreatureBase>();
		String clName = "net.qmat.qmhh.models.creatures."+Settings.getString(Settings.PR_CREATURE_CLASS);
		try {
			creatureClass = (Class<? extends CreatureBase>) Class.forName(clName);
		} catch (ClassNotFoundException e) {
			System.err.println("Can't find the creature class defined in the preferences file.");
			e.printStackTrace();
		}
	}
	
	public void addCreature() {
		try {
			creatures.add(creatureClass.newInstance());
			Controllers.getSoundController().creatureWasBorn();
		} catch (Exception e) {
			System.err.println("Something went wrong with loading the creature class.");
			e.printStackTrace();
		}
	}
	
	public void update() {
		for(CreatureBase creature : creatures) {
			creature.update(creatures);
		}
	}
	
	public void draw() {
		p.rectMode(Main.CENTER);
		p.noFill();
		p.stroke(200);
		for(CreatureBase creature : creatures) {
			creature.draw();
		}
	}
	
	public ArrayList<CreatureBase> getTargeters(final float angle) {
		ArrayList<CreatureBase> orderedCreatures = new ArrayList<CreatureBase>(creatures);
		Collections.sort(orderedCreatures, new Comparator<CreatureBase>() {
			public int compare(CreatureBase c1, CreatureBase c2) {
				PPoint2 c1p = c1.getPPosition();
				PPoint2 c2p = c2.getPPosition();
				// if one of the creatures has a target and the other doesn't, no need to check angles
				if(c1.hasTargetP() && !c2.hasTargetP())
					return 1;
				if(c2.hasTargetP() && !c1.hasTargetP())
					return -1;
				// so they both don't have targets, or they both do, check angles
				// TODO: check whether the ones with targets have companion hunters
				if(calculateAngularDistance(angle, c1p.t) < calculateAngularDistance(angle, c2p.t))
					return -1;
				else if(calculateAngularDistance(angle, c2p.t) > calculateAngularDistance(angle, c1p.t))
					return 1;
				else
					return 0;
			}
		});
		return orderedCreatures;
	}
	
	private float calculateAngularDistance(float a1, float a2) {
		float d = a1 - a2;
		if(d < -Main.PI) return d + Main.PI;
		if(d > Main.PI) return d - Main.PI;
		return d;
	}
	
}