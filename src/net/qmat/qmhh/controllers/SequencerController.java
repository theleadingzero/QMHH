package net.qmat.qmhh.controllers;

import oscP5.*;
import net.qmat.qmhh.Main;
import net.qmat.qmhh.models.Models;
import net.qmat.qmhh.utils.PPoint2;
import net.qmat.qmhh.utils.Settings;
import netP5.*;

// TODO: add correct size to outgoing message => need to change tuiocontroller. 

public class SequencerController {

	OscP5 oscP5;
	NetAddress soundMachine;

	// cache these settings for speed
	private float innerRadius, outerRadius;

	public SequencerController() {
		oscP5 = new OscP5(this, Settings.getInteger(Settings.OSC_SEQUENCER_PORT));
		soundMachine = new NetAddress(Settings.getString(Settings.OSC_SEQUENCER_IP),
				Settings.getInteger(Settings.OSC_SEQUENCER_PORT));
		innerRadius = Settings.getInteger(Settings.PR_RING_INNER_RADIUS);
		outerRadius = Settings.getInteger(Settings.PR_RING_OUTER_RADIUS);
	}

	public void addHand(Long id, PPoint2 ppos) {
		sendUpdate("/seq/addHand", id, ppos);
	}

	public void updateHand(Long id, PPoint2 ppos) {
		sendUpdate("/seq/updateHand", id, ppos);
	}

	private void sendUpdate(String endPoint, Long id, PPoint2 ppos) {
		OscMessage oscMessage = new OscMessage(endPoint);
		oscMessage.add(Main.table);
		oscMessage.add(id);
		oscMessage.add(ppos.t);
		/* 
		 * prepare the right distance, is position within the outer ring 
		 * expressed as a number betwen 0.0 and 1.0.
		 */
		oscMessage.add((float)(ppos.r - innerRadius) / 
					   (float)(outerRadius - innerRadius));
		oscMessage.add(100);
		oscP5.send(oscMessage, soundMachine);
	}

	public void removeHand(Long id) {
		OscMessage oscMessage = new OscMessage("/seq/removeHand");
		oscMessage.add(Main.table);
		oscMessage.add(id);
		oscP5.send(oscMessage, soundMachine);
	}

	void oscEvent(OscMessage theOscMessage) {
		
		if(theOscMessage.checkAddrPattern("/seq/setAngle")==true) {
			if(theOscMessage.checkTypetag("f")) {
				float angle = theOscMessage.get(0).floatValue();
				Models.getPlayheadModel().setAngle(angle);
			}  
		} else if(theOscMessage.checkAddrPattern("/seq/setDuration")==true) {
			if(theOscMessage.checkTypetag("f")) {
				float duration = theOscMessage.get(0).floatValue();
				Models.getPlayheadModel().setDuration(duration);
			}  
		} else if(theOscMessage.checkAddrPattern("/seq/start")==true) {
			if(theOscMessage.checkTypetag("")) {
				Models.getPlayheadModel().start();
			}  
		} else if(theOscMessage.checkAddrPattern("/seq/stop")==true) {
			if(theOscMessage.checkTypetag("")) {
				Models.getPlayheadModel().stop();
			}  
		}
		
	}

}
