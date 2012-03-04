/*
 * The TuioController takes care of receiving the Tuio messages and passing them
 * on to the rest of the application. It currently takes care of talking to the
 * HandModel, but later we should perhaps add a HandController if game logic 
 * becomes more complex.
 * 
 * N.B. this is not proper MVC design, but should the application become too
 * complex to manage we should be able to switch relatively easily if we keep
 * functionality separated like this.
 * 
 * A TuioCursor is a blob (i.e. a finger or hand).
 * A TuioObject is a fiducial.
 *  
 */

package net.qmat.qmhh;

import TUIO.*;

/* 
 * TODO: when CCV crashes or is restarted while the processing app is running 
 * 	     the cursor cache could retain blobs forever. 
 */

public class TuioController implements TuioListener {
	
	TuioClient client;
	
	public TuioController() {
		client = new TuioClient();
		client.addTuioListener(this);
	    client.connect();
	}

	@Override
	public void addTuioCursor(TuioCursor tc) {
		//.put(tc.getSessionID(), tc);
		TuioPoint p = tc.getPosition();
		Controllers.getHandsController().addHand(tc.getSessionID(), p.getX(), p.getY());
	}

	@Override
	public void addTuioObject(TuioObject arg0) {
		//printTuioObject(arg0);
	}

	@Override
	public void refresh(TuioTime arg0) {
		//System.out.println("#<TuioTime ms: " + arg0.getTotalMilliseconds() + ">");
	}

	@Override
	public void removeTuioCursor(TuioCursor tc) {
		//printTuioCursor(arg0);
		Controllers.getHandsController().removeHand(tc.getSessionID());
	}

	@Override
	public void removeTuioObject(TuioObject arg0) {
		//printTuioObject(arg0);
	}

	@Override
	public void updateTuioCursor(TuioCursor tc) {
		//printTuioCursor(arg0);
		TuioPoint p = tc.getPosition();
		Controllers.getHandsController().updateHand(tc.getSessionID(), p.getX(), p.getY());
	}

	@Override
	public void updateTuioObject(TuioObject arg0) {
		//printTuioObject(arg0);
	}
	
	@SuppressWarnings("unused")
	private void printTuioCursor(TuioCursor o) {
		System.out.println("#<TuioCursor"
							+ " cursorId: " + o.getCursorID()
							+ " motionAccel: " + o.getMotionAccel()
							+ " motionSpeed: " + o.getMotionSpeed()
							+ " pathLength: " + o.getPath().size()
							+ " position: " + tuioPointToString(o.getPosition())
							+ " sessionId: " + o.getSessionID()
							+ " tuioState: " + o.getTuioState()
							+ ">");
	}
	
	@SuppressWarnings("unused")
	private void printTuioObject(TuioObject o) {
		System.out.println("#<TuioObject"
							+ " angleDegrees: " + o.getAngleDegrees() 
							+ " motionAccel: " + o.getMotionAccel()
							+ " motionSpeed: " + o.getMotionSpeed()
							+ " pathLength: " + o.getPath().size()
							+ " position: " + tuioPointToString(o.getPosition())
							+ " rotationAccel: " + o.getRotationAccel()
							+ " rotationSpeed: " + o.getRotationSpeed()
							+ " sessionId: " + o.getSessionID()
							+ " symbolId: " + o.getSymbolID()
							+ " tuioState: " + o.getTuioState()
							+ ">");
	}
	
	private String tuioPointToString(TuioPoint p) {
		return "[" + p.getX() + "," + p.getY() + "]";
	}
		
}
