/*
 * The HandsController takes care of checking whether the hands are 
 * out of bounds and should be removed or even instantiated in the first place.
 */

package net.qmat.qmhh;

import java.util.ArrayList;
import java.util.HashMap;

public class HandsController {
	
	// cache for speed
	private float ringInnerRadius, ringOuterRadius;
	private HashMap<Long, ArrayList<Creature>> handCreatures;
	
	public HandsController() {
		ringInnerRadius = Settings.getInteger(Settings.PR_RING_INNER_RADIUS) / 1.0f;
		ringOuterRadius = Settings.getInteger(Settings.PR_RING_OUTER_RADIUS) / 1.0f;
		handCreatures = new HashMap<Long, ArrayList<Creature>>();
	}
	
	// N.B. takes relative positions [0.0 ... 1.0]
	public void addHand(Long id, float x, float y) {
		float pixelX = Main.relativeToPixelsX(x);
		float pixelY = Main.relativeToPixelsY(y);
		if(handInBoundsP(pixelX, pixelY)) {
			addHandHelper(id, pixelX, pixelY);
		}
	}
	
	private void addHandHelper(Long id, float pixelX, float pixelY) {
		Models.getOrbModel().increaseRadius();
		Models.getHandsModel().addHand(id, pixelX, pixelY);
		PPoint2 ppos = new CPoint2(pixelX, pixelY).toPPoint2();
		Controllers.getSequencerController().addHand(id, ppos);
		// Let's get some bad ass creatures to eat that energy
		float angle = ppos.t;
		ArrayList<Creature> creatures = Models.getCreaturesModel().getTargeters(angle);
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
		float pixelX = Main.relativeToPixelsX(x);
		float pixelY = Main.relativeToPixelsY(y);
		if(handInBoundsP(pixelX, pixelY)) {
			if(Models.getHandsModel().containsHandAlreadyP(id)) {
				Models.getHandsModel().updateHand(id, pixelX, pixelY);
				PPoint2 ppos = new CPoint2(pixelX, pixelY).toPPoint2();
				Controllers.getSequencerController().updateHand(id, ppos);
			} else {
				addHandHelper(id, pixelX, pixelY);
			}
		} else {
			removeHand(id);
		}
	}
	
	// N.B. takes relative positions [0.0 ... 1.0]
	public void removeHand(Long id) {
		// TODO: Think about what happens if a player's hand is removed.
		removeCreaturesFromHand(id);
		Models.getOrbModel().decreaseRadius();
		Models.getHandsModel().removeHand(id);
		Controllers.getSequencerController().removeHand(id);
	}
	
	// N.B. takes actual pixel values, not relative ones (like the one from Tuio)
	private boolean handInBoundsP(float x, float y) {
		PPoint2 ppoint = Main.c2p(x, y);
		return (ppoint.r > ringInnerRadius && ppoint.r < ringOuterRadius);
	}
	
	private void addCreatureToHand(Long handID, Creature creature) {
		ArrayList<Creature> al = handCreatures.get(handID);
		if(al == null) {
			al = new ArrayList<Creature>();
			handCreatures.put(handID, al);
		}
		if(!al.contains(creature)) {
			al.add(creature);
			System.out.println("Following: " + handID);
			creature.setTarget(Models.getHandsModel().getHand(handID));
		}
	}
	
	private void removeCreaturesFromHand(Long handID) {
		ArrayList<Creature> al = handCreatures.get(handID);
		if(al != null) {
			for(Creature creature : al) {
				creature.removeTarget();
			}
		}
		handCreatures.put(handID, new ArrayList<Creature>());
	}
	

}
