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
	
	private int targetColorIndex = 0;
	private int targetColors[] = 
		{ p.color(150),
		  p.color(150, 0, 0, 200),
		  p.color(0, 150, 0, 200)
		  //p.color(0, 0, 150, 100)
		};
	
	@SuppressWarnings("unchecked")
	public CreaturesModel() {
		creatures = new ArrayList<CreatureBase>();
		String clName = "net.qmat.qmhh.models.creatures."+Settings.getString(Settings.PR_CREATURE_CLASS);
		try {
			creatureClass = (Class<? extends CreatureBase>) Class.forName(clName);
			initCreatures();
		} catch (ClassNotFoundException e) {
			System.err.println("Can't find the creature class defined in the preferences file.");
			e.printStackTrace();
		}
		
	}
	
	public int getTargetColor() {
		targetColorIndex++;
		return targetColors[targetColorIndex % targetColors.length];
	}
	
	private void initCreatures() {
		CreatureBase cb;
		// add a creature in stage 2, stage 1, and stage 0;
		cb = instantiateCreature();
		cb.setStage(2);
		cb = instantiateCreature();
		cb.setStage(2);
		// stage 1
		cb = instantiateCreature();
		cb.setStage(1);
		cb = instantiateCreature();
		cb.setStage(1);
		// stage 0
		instantiateCreature();
		instantiateCreature();
	}
	
	public CreatureBase addCreature() {
		Controllers.getSoundController().creatureWasBorn();
		return instantiateCreature();
	}
	
	public CreatureBase instantiateCreature() {
		try {
			CreatureBase cb = creatureClass.newInstance();
			creatures.add(cb);
			return cb;
		} catch (Exception e) {
			System.err.println("Something went wrong with instantiating a creature.");
			e.printStackTrace();
			return null;
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
			//creature.drawTargetFeedback();
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
				if(PPoint2.calculateAngularDistance(angle, c1p.t) < PPoint2.calculateAngularDistance(angle, c2p.t))
					return -1;
				else if(PPoint2.calculateAngularDistance(angle, c2p.t) > PPoint2.calculateAngularDistance(angle, c1p.t))
					return 1;
				else
					return 0;
			}
		});
		return orderedCreatures;
	}
}
