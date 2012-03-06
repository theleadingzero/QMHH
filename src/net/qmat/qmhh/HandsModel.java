package net.qmat.qmhh;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import processing.core.PApplet;

public class HandsModel extends ProcessingObject {
	
	private ConcurrentHashMap<Long, Hand> hands;
	
	public HandsModel() {
		hands = new ConcurrentHashMap<Long, Hand>();
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
		hands.remove(id);
	}
	
	public boolean containsHandAlreadyP(Long id) {
		return hands.contains(id);
	}
	
	public void draw() {
		
		// set up the look for the hands here for efficiency
		p.ellipseMode(PApplet.CENTER);
		
		// draw each hand
		Iterator<Entry<Long, Hand>> entries = hands.entrySet().iterator();
		while(entries.hasNext()) {
			Hand hand = entries.next().getValue();
			hand.draw();
		}
	}
	
	

}
