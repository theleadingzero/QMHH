/*
 * The HandsController takes care of checking whether the hands are 
 * out of bounds and should be removed or even instantiated in the first place.
 */

package net.qmat.qmhh.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import TUIO.TuioPoint;

import net.qmat.qmhh.Main;
import net.qmat.qmhh.models.Models;
import net.qmat.qmhh.models.creatures.CreatureBase;
import net.qmat.qmhh.utils.CPoint2;
import net.qmat.qmhh.utils.PPoint2;
import net.qmat.qmhh.utils.Settings;

public class HandsController {
	
	private float minX = 1.0f;
	private float minY = 1.0f;
	private float maxX = 0.0f;
	private float maxY = 0.0f;
	
	// cache for speed
	private float ringInnerRadius, ringOuterRadius;
	private HashMap<Long, ArrayList<CreatureBase>> handCreatures;
	
	public HandsController() {
		ringInnerRadius = Settings.getInteger(Settings.PR_RING_INNER_RADIUS) / 1.0f;
		ringOuterRadius = Settings.getInteger(Settings.PR_RING_OUTER_RADIUS) / 1.0f;
		handCreatures = new HashMap<Long, ArrayList<CreatureBase>>();
	}
	
	private float mapX(float x) {
		if(x > maxX)
			maxX = x;
		if(x < minX)
			minX = x;
		return Main.map(x, minX, maxX, 0.0f, 1.0f);
	}
	
	private float mapY(float y) {
		if(y > maxY)
			maxY = y;
		if(y < minY)
			minY = y;
		return Main.map(y, minY, maxY, 0.0f, 1.0f);
	}
	
	private CPoint2 mapPoint(TuioPoint p) {
		return new CPoint2(
				Main.relativeToPixelsX(Main.map(p.getX(), minX, maxX, 0.0f, 1.0f)), 
				Main.relativeToPixelsY(Main.map(p.getY(), minY, maxY, 0.0f, 1.0f)));
	}
	
	@SuppressWarnings("unused")
	private Vector<CPoint2> mapPoints(Vector<TuioPoint> path) {
		Vector<CPoint2> v = new Vector<CPoint2>();
		for(TuioPoint p : path) {
			v.add(mapPoint(p));
		}
		return v;
	}
	
	// N.B. takes relative positions [0.0 ... 1.0]
	public void addHand(Long id, float x, float y) {
		float pixelX = Main.relativeToPixelsX(mapX(x));
		float pixelY = Main.relativeToPixelsY(mapY(y));
		if(handInBoundsP(pixelX, pixelY)) {
			addHandHelper(id, pixelX, pixelY);
		}
	}
	
	private void addHandHelper(Long id, float pixelX, float pixelY) {
		//Models.getOrbModel().increaseRadius();
		Models.getHandsModel().addHand(id, pixelX, pixelY);
		PPoint2 ppos = new CPoint2(pixelX, pixelY).toPPoint2();
		//Controllers.getSequencerController().addHand(id, ppos);
		Controllers.getSoundController().handWasAdded();
		Controllers.getSoundController().beamStarted();
		// Let's get some bad ass creatures to eat that energy
		float angle = ppos.t;
		ArrayList<CreatureBase> creatures = Models.getCreaturesModel().getTargeters(angle);
		for(int i=0; i<creatures.size() && i<2; i++) {
			// TODO: remove this later:
			if(!creatures.get(i).hasTargetP()) {
				this.addCreatureToHand(id, creatures.get(i));
			}
		}
	}

	// N.B. takes relative positions [0.0 ... 1.0]
	public void updateHand(Long id, float x, float y)
	{
		float pixelX = Main.relativeToPixelsX(mapX(x));
		float pixelY = Main.relativeToPixelsY(mapY(y));
		if(handInBoundsP(pixelX, pixelY)) {
			if(Models.getHandsModel().containsHandAlreadyP(id)) {
				Models.getHandsModel().updateHand(id, pixelX, pixelY);
				PPoint2 ppos = new CPoint2(pixelX, pixelY).toPPoint2();
				//Controllers.getSequencerController().updateHand(id, ppos);
			}
		// remove if it is present, but not in bounds
		} else if(Models.getHandsModel().containsHandAlreadyP(id)) {
			removeHand(id);
		}
	}
	
	// N.B. takes relative positions [0.0 ... 1.0]
	public void removeHand(Long id) {
		if(Models.getHandsModel().containsHandAlreadyP(id)) {
			// TODO: Think about what happens if a player's hand is removed.
			removeCreaturesFromHand(id);
			//Models.getOrbModel().decreaseRadius();
			Models.getHandsModel().removeHand(id);
			//Controllers.getSequencerController().removeHand(id);
			Controllers.getSoundController().handWasRemoved();
			Controllers.getSoundController().beamStopped();
		}
	}
	
	// N.B. takes actual pixel values, not relative ones (like the one from Tuio)
	private boolean handInBoundsP(float x, float y) {
		return true;
		/*
		PPoint2 ppoint = new CPoint2(x, y).toPPoint2();
		//return (ppoint.r > ringInnerRadius && ppoint.r < ringOuterRadius);
		return (ppoint.r > ringInnerRadius);
		*/
	}
	
	private void addCreatureToHand(Long handID, CreatureBase creature) {
		ArrayList<CreatureBase> al = handCreatures.get(handID);
		if(al == null) {
			al = new ArrayList<CreatureBase>();
			handCreatures.put(handID, al);
		}
		if(!al.contains(creature)) {
			al.add(creature);
			creature.setTarget(Models.getHandsModel().getHand(handID));
		}
	}
	
	private void removeCreaturesFromHand(Long handID) {
		ArrayList<CreatureBase> al = handCreatures.get(handID);
		if(al != null) {
			for(CreatureBase creature : al) {
				creature.removeTarget();
			}
		}
		handCreatures.put(handID, new ArrayList<CreatureBase>());
	}
	

}
