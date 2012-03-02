package net.qmat.qmhh;

import TUIO.*;
import processing.core.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

public class TuioController extends ProcessingObject implements TuioListener {
	
	TuioClient client;
	ConcurrentHashMap<Long, TuioCursor> cursors;
	
	public TuioController(PApplet p) {
		super(p);
		cursors = new ConcurrentHashMap<Long, TuioCursor>();
		client = new TuioClient();
		client.addTuioListener(this);
	    client.connect();
	}

	@Override
	public void addTuioCursor(TuioCursor tc) {
		cursors.put(tc.getSessionID(), tc);
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
		//drawTuioCursor(arg0);
		//printTuioCursor(arg0);
		cursors.remove(tc.getSessionID());
	}

	@Override
	public void removeTuioObject(TuioObject arg0) {
		//printTuioObject(arg0);
	}

	@Override
	public void updateTuioCursor(TuioCursor tc) {
		//drawTuioCursor(arg0);
		//printTuioCursor(arg0);
		cursors.put(tc.getSessionID(), tc);
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
	
	private void drawTuioCursor(TuioCursor o) {
		if(o != null)
		{
			p.pushMatrix();
			p.translate(o.getPosition().getX() * p.width, o.getPosition().getY() * p.height);
			p.stroke(255);
			p.fill(255);
			p.ellipse(0, 0, 10, 10);
			p.text("" + o.getSessionID());
			p.popMatrix();
		}
	}
	
	public void draw() {
		Iterator<Entry<Long, TuioCursor>> entries = cursors.entrySet().iterator();
		while(entries.hasNext()) {
			TuioCursor tc = entries.next().getValue();
			//p.println(""+tc.getPosition().getX()+","+tc.getPosition().getY());
			drawTuioCursor(tc);
		}
	}
		
}
