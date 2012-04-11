package net.qmat.qmhh.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.qmat.qmhh.Main;
import net.qmat.qmhh.utils.PPoint2;
import net.qmat.qmhh.utils.Settings;

import org.jbox2d.common.Vec2;

public class HandsModel extends ProcessingObject {
	
	public static Float chargeTime;
	public static Double chargeTimeNano;
	
	private ConcurrentHashMap<Long, Hand> hands;
	private int nrSections;
	
	public HandsModel() {
		chargeTime = Settings.getFloat(Settings.PR_HAND_CHARGE_TIME);
		chargeTimeNano = chargeTime.doubleValue() * 1000000000;
		hands = new ConcurrentHashMap<Long, Hand>();
		nrSections = Settings.getInteger(Settings.PR_SEQUENCER_SECTIONS);;
	}
	
	public void addHand(Long id, float x, float y) {
		Hand hand = hands.get(id);
		if(hand != null) {
			hand.updatePosition(x, y);
		} else {
			hands.put(id, new Hand(x, y));
		}
	}
	
	public void updateHand(Long id, float x, float y) {
		Hand hand = hands.get(id);
		if(hand == null) {
			addHand(id, x, y);
		} else {
			hands.get(id).updatePosition(x, y);
		}
	}
	
	public Hand getHand(Long id) {
		return hands.get(id);
	}
	
	public void removeHand(Long id) {
		// TODO: If the hand isn't there, just ignore the call.
		// TODO: Don't remove immediately, but mark the hand for removal.
		Hand hand = hands.get(id);
		if(hand != null) hand.markForRemoval();
	}
	
	public boolean containsHandAlreadyP(Long id) {
		return hands.get(id) != null;
	}
	
	public void update() {
		Iterator<Entry<Long, Hand>> entries = hands.entrySet().iterator();
		while(entries.hasNext()) {
			Entry<Long, Hand> entry = entries.next();
			Hand hand = entry.getValue();
			if(hand.isMarkedForRemoval()) {
				hand.destroy();
				entries.remove();
			}
		}
	}
	
	public void draw() {
		// draw the fancy patterns underneath the hands
		//drawHandBackdrops();
		
		// set up the look for the hands here for efficiency
		p.ellipseMode(Main.CENTER);
		
		// draw each hand
		Iterator<Entry<Long, Hand>> entries = hands.entrySet().iterator();
		while(entries.hasNext()) {
			Hand hand = entries.next().getValue();
			hand.draw();
		}
	}
	
	@SuppressWarnings("unused")
	private ArrayList<Vec2> getHandFocusPoints() {
		ArrayList<Vec2> result = new ArrayList<Vec2>();
		ArrayList<ArrayList<Vec2>> intermediate = new ArrayList<ArrayList<Vec2>>();
		for(int i=0; i<nrSections; i++) {
			intermediate.add(new ArrayList<Vec2>());
		}
		Iterator<Entry<Long, Hand>> entries = hands.entrySet().iterator();
		while(entries.hasNext()) {
			Hand hand = entries.next().getValue();
			PPoint2 ppos = hand.getCPosition().toPPoint2();
			float handAngle = ppos.t < 0.0f ? ppos.t + Main.TWO_PI : ppos.t;
			ArrayList<Vec2> addTo = intermediate.get((int)(handAngle / (Main.TWO_PI/nrSections)));
			addTo.add(hand.getCPosition().toVec2());
		}
		for(int i=0; i<intermediate.size(); i++) {
			ArrayList<Vec2> sectionVectors = intermediate.get(i);
			if(sectionVectors.size()==0) {
				result.add(null);
				continue;
			}
			Vec2 vTotal = new Vec2(0,0);
			for(Vec2 vAdd : sectionVectors) {
				vTotal = vTotal.add(vAdd);
			}
			vTotal = vTotal.mulLocal(1.0f/sectionVectors.size());
			result.add(vTotal);
		}
		return result;
	}
	
	public int nrUnblockedHands() {
		synchronized(hands) {
			int nrHands = 0;
			Iterator<Entry<Long, Hand>> entries = hands.entrySet().iterator();
			while(entries.hasNext()) {
				Hand hand = entries.next().getValue();
				if(hand.nrBeamCreatures() <= 0 && hand.hasReachedCenterP()) 
					nrHands++;
			}
			return nrHands;
		}
	}
	
	public int nrHands() {
		return hands.size();
	}

}
