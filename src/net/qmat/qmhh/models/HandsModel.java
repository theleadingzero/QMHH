package net.qmat.qmhh.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import processing.core.PApplet;
import net.qmat.qmhh.Main;
import net.qmat.qmhh.utils.CPoint2;
import net.qmat.qmhh.utils.PPoint2;
import net.qmat.qmhh.utils.Settings;

import org.jbox2d.common.Vec2;

public class HandsModel extends ProcessingObject {
	
	private ConcurrentHashMap<Long, Hand> hands;
	private int nrSections;
	
	public HandsModel() {
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
		drawHandBackdrops();
		
		// set up the look for the hands here for efficiency
		p.ellipseMode(Main.CENTER);
		
		// draw each hand
		Iterator<Entry<Long, Hand>> entries = hands.entrySet().iterator();
		while(entries.hasNext()) {
			Hand hand = entries.next().getValue();
			hand.draw();
		}
	}
	
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
	
	private void drawHandBackdrops() {
		
		ArrayList<Vec2> averageHandPositions = getHandFocusPoints();

		float angleStep = Main.TWO_PI / nrSections;
		p.strokeWeight(0.5f);
		p.stroke(10, 15, 100);
		
		for(int section=0; section<nrSections; section++) {
			if(averageHandPositions.get(section) == null) 
				continue;
			int nrLines = 7;
			float angle1 = section * angleStep;
			float angle2 = (section+1) * angleStep;
			
			// N.B. use this to test a specific position:
			//PPoint2 handPpos = new PPoint2((Main.outerRingInnerRadius * 0.25f + Main.outerRingOuterRadius * 0.75f),
			//							   (angle1 * 0.25f + angle2 * 0.75f));
			PPoint2 handPpos = new CPoint2(averageHandPositions.get(section)).toPPoint2();
			float angleHand = handPpos.t < 0.0f ? handPpos.t + Main.TWO_PI : handPpos.t;
			float radiusHand = handPpos.r;
			float lineAngleStep;
			float lineAngleStepTemp;
			int line;
			
			p.stroke(0);
			p.strokeWeight(3);
			
			PPoint2 ppos = new PPoint2(200,Main.PI/2.0f);
			p.ellipse(ppos.toCPoint2().x, ppos.toCPoint2().y, 10, 10);
			
			// top, outer right in
			lineAngleStep = (angleHand - angle1) / nrLines / 2.0f;
			lineAngleStepTemp = lineAngleStep;
			line=0;
			for(float angleAdd=0.0f; angle1+angleAdd<angleHand; angleAdd+=lineAngleStepTemp, line++, lineAngleStepTemp*=1.1f) {
				float r = (Main.outerRingOuterRadius-handPpos.r)/nrLines*line+handPpos.r;
				if(r>Main.outerRingOuterRadius) break;
				CPoint2 startPos = new PPoint2(Main.outerRingOuterRadius, angle1+angleAdd).toCPoint2();
				CPoint2 endPos = new PPoint2(r, angleHand).toCPoint2();
				p.line(startPos.x, startPos.y, endPos.x, endPos.y);
			}
			// top, outer left in
			lineAngleStep = (angle2 - angleHand) / nrLines / 2.0f;
			lineAngleStepTemp = lineAngleStep;
			line=0;
			for(float angleAdd=0.0f; angle2-angleAdd>angleHand; angleAdd+=lineAngleStepTemp, line++, lineAngleStepTemp*=1.1f) {
				float r = (Main.outerRingOuterRadius-handPpos.r)/nrLines*line+handPpos.r;
				if(r>Main.outerRingOuterRadius) break;
				CPoint2 startPos = new PPoint2(Main.outerRingOuterRadius, angle2-angleAdd).toCPoint2();
				CPoint2 endPos = new PPoint2(r, angleHand).toCPoint2();
				p.line(startPos.x, startPos.y, endPos.x, endPos.y);
			}
			// top, inner right in
			lineAngleStep = (angleHand - angle1) / nrLines / 2.0f;
			lineAngleStepTemp = lineAngleStep;
			line=0;
			for(float angleAdd=0.0f; angle1+angleAdd<angleHand; angleAdd+=lineAngleStepTemp, line++, lineAngleStepTemp*=1.1f) {
				float r = (Main.outerRingInnerRadius-handPpos.r)/nrLines*line+handPpos.r;
				if(r<Main.outerRingInnerRadius) break;
				CPoint2 startPos = new PPoint2(Main.outerRingInnerRadius, angle1+angleAdd).toCPoint2();
				CPoint2 endPos = new PPoint2(r, angleHand).toCPoint2();
				p.line(startPos.x, startPos.y, endPos.x, endPos.y);
			}
			// top, inner left in
			lineAngleStep = (angle2 - angleHand) / nrLines / 2.0f;
			lineAngleStepTemp = lineAngleStep;
			line=0;
			for(float angleAdd=0.0f; angle2-angleAdd>angleHand; angleAdd+=lineAngleStepTemp, line++, lineAngleStepTemp*=1.1f) {
				float r = (Main.outerRingInnerRadius-handPpos.r)/nrLines*line+handPpos.r;
				if(r<Main.outerRingInnerRadius) break;
				CPoint2 startPos = new PPoint2(Main.outerRingInnerRadius, angle2-angleAdd).toCPoint2();
				CPoint2 endPos = new PPoint2(r, angleHand).toCPoint2();
				p.line(startPos.x, startPos.y, endPos.x, endPos.y);
			}
			// side, inner right in
			for(line=0; line<nrLines; line++) {
				CPoint2 startPos = new PPoint2(radiusHand, angle1+(angleHand-angle1)/nrLines*line).toCPoint2();
				CPoint2 endPos = new PPoint2(radiusHand - (radiusHand - Main.outerRingInnerRadius)/nrLines*line, angle1).toCPoint2();
				p.line(startPos.x, startPos.y, endPos.x, endPos.y);
			}
			// side, outer right in
			for(line=0; line<nrLines; line++) {
				CPoint2 startPos = new PPoint2(radiusHand, angle1+(angleHand-angle1)/nrLines*line).toCPoint2();
				CPoint2 endPos = new PPoint2(radiusHand + (Main.outerRingOuterRadius - radiusHand)/nrLines*line, angle1).toCPoint2();
				p.line(startPos.x, startPos.y, endPos.x, endPos.y);
			}
			// side, inner left in
			for(line=0; line<nrLines; line++) {
				CPoint2 startPos = new PPoint2(radiusHand, angle2-(angle2-angleHand)/nrLines*line).toCPoint2();
				CPoint2 endPos = new PPoint2(radiusHand - ((radiusHand - Main.outerRingInnerRadius)/nrLines)*line, angle2).toCPoint2();
				p.line(startPos.x, startPos.y, endPos.x, endPos.y);
			}
			// side, outer left in
			for(line=0; line<nrLines; line++) {
				CPoint2 startPos = new PPoint2(radiusHand, angle2-(angle2-angleHand)/nrLines*line).toCPoint2();
				CPoint2 endPos = new PPoint2(radiusHand + (Main.outerRingOuterRadius - radiusHand)/nrLines*line, angle2).toCPoint2();
				p.line(startPos.x, startPos.y, endPos.x, endPos.y);
			}
			
		}
		p.strokeWeight(1.0f);
	}
	
	

}
