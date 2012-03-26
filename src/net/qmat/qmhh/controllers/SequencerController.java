package net.qmat.qmhh.controllers;

import net.qmat.qmhh.Main;
import net.qmat.qmhh.utils.PPoint2;
import net.qmat.qmhh.utils.Settings;
import oscP5.OscMessage;

// TODO: add correct size to outgoing message => need to change tuiocontroller. 

public class SequencerController {

	// cache these settings for speed
	private float innerRadius, outerRadius;

	public SequencerController() {
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
		float distance = (float)(ppos.r - innerRadius) / 
					   	 (float)(outerRadius - innerRadius);
		float size = 100;
		Controllers.getOscController()
			.queueSequencerEvent(endPoint, Main.table, id, ppos.t, distance, size);
	}

	public void removeHand(Long id) {
		Controllers.getOscController()
			.queueSequencerEvent("/seq/removeHand", Main.table, id);
	}

}
