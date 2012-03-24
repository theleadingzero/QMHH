package net.qmat.qmhh.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;

import net.qmat.qmhh.Main;
import net.qmat.qmhh.utils.CPoint2;
import net.qmat.qmhh.utils.PPoint2;
import org.jbox2d.common.Vec2;
import com.sun.opengl.util.BufferUtil;
import java.nio.FloatBuffer;
import javax.media.opengl.GL;
import processing.opengl.PGraphicsOpenGL;

public class FeedbackPointsModel extends ProcessingObject {
	
	ArrayList<FeedbackPoint> points;
	
	public FeedbackPointsModel() {
		points = new ArrayList<FeedbackPoint>();
	}
	
	public void addFeedbackPoint(float x, float y) {
		addFeedbackPoint(new CPoint2(x, y));
	}
	
	public void addFeedbackPoint(CPoint2 cpos) {
		points.add(new FeedbackPoint(cpos));
	}
	
	public void draw() {
		synchronized(points) {
			Iterator<FeedbackPoint> it = points.iterator();
			while(it.hasNext()) {
				FeedbackPoint point = it.next();
				if(point.isMarkedForRemovalP())
					it.remove();
				else
					point.draw();
			}
		}
	}
	
}
