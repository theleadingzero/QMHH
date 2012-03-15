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
		return hands.get(id) != null;
	}
	
	public void draw() {
		// draw the fancy patterns underneath the hands
		
		float angleStep = Main.TWO_PI / 16.0f;
		float lineSpace = 5.0f;
		p.strokeWeight(0.5f);
		p.stroke(0);
		
		// TODO: replace 16 by setting
		System.out.println("------");
		for(int section=0; section<16; section++) {
			int nrLines = 7;
			float angle1 = section * angleStep;
			float angle2 = (section+1) * angleStep;
			
			PPoint2 handPpos = new PPoint2((Main.outerRingInnerRadius + Main.outerRingOuterRadius) / 2.0f,
					  				  (angle1+angle2)/2.0f);
			float angleHand = handPpos.t;
			float lineAngleStep = (angleHand - angle1) / nrLines / 2.0f;
			float lineAngleStepTemp;
			int line;
			
			PPoint2 ppos = new PPoint2(200,Main.PI/2.0f);
			p.ellipse(ppos.toCPoint2().x, ppos.toCPoint2().y, 10, 10);
			
			lineAngleStepTemp = lineAngleStep;
			line=0;
			for(float angleAdd=0.0f; angle1+angleAdd<angleHand; angleAdd+=lineAngleStepTemp, line++, lineAngleStepTemp*=1.1f) {
				CPoint2 startPos = new PPoint2(Main.outerRingOuterRadius, angle1+angleAdd).toCPoint2();
				CPoint2 endPos = new PPoint2((Main.outerRingOuterRadius-handPpos.r)/nrLines*line+handPpos.r, angleHand).toCPoint2();
				p.line(startPos.x, startPos.y, endPos.x, endPos.y);
				if(section==0) {
					p.fill(255,0,0);
					p.ellipse(startPos.x, startPos.y, 3, 3);
					p.fill(255,0,255);
					p.ellipse(endPos.x, endPos.y, 3, 3);
				}
			}
		}
		p.strokeWeight(1.0f);
		
		// set up the look for the hands here for efficiency
		p.ellipseMode(Main.CENTER);
		
		// draw each hand
		Iterator<Entry<Long, Hand>> entries = hands.entrySet().iterator();
		while(entries.hasNext()) {
			Hand hand = entries.next().getValue();
			hand.draw();
		}
	}
	
	

}
