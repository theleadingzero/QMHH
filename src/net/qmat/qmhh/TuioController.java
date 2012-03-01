package net.qmat.qmhh;

import TUIO.*;

public class TuioController implements TuioListener {
	TuioClient client = null;
	
	public TuioController() {
		client = new TuioClient();
		client.addTuioListener(this);
	    client.connect();
	}

	@Override
	public void addTuioCursor(TuioCursor arg0) {
		printTuioCursor(arg0);
	}

	@Override
	public void addTuioObject(TuioObject arg0) {
		printTuioObject(arg0);
	}

	@Override
	public void refresh(TuioTime arg0) {
		System.out.println("#<TuioTime ms: " + arg0.getTotalMilliseconds() + ">");
	}

	@Override
	public void removeTuioCursor(TuioCursor arg0) {
		printTuioCursor(arg0);
	}

	@Override
	public void removeTuioObject(TuioObject arg0) {
		printTuioObject(arg0);
	}

	@Override
	public void updateTuioCursor(TuioCursor arg0) {
		printTuioCursor(arg0);
	}

	@Override
	public void updateTuioObject(TuioObject arg0) {
		printTuioObject(arg0);
	}
	
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
