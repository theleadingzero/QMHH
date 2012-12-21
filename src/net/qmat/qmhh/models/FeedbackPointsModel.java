package net.qmat.qmhh.models;

import java.util.ArrayList;
import java.util.Iterator;

import net.qmat.qmhh.models.trees.TreesModel;
import net.qmat.qmhh.utils.CPoint2;

public class FeedbackPointsModel extends ProcessingObject {
	
	ArrayList<FeedbackPoint> points;
	
	public FeedbackPointsModel() {
		points = new ArrayList<FeedbackPoint>();
	}
	
	public void addFeedbackPoint(float x, float y) {
		addFeedbackPoint(new CPoint2(x, y));
	}
	
	public void addFeedbackPoint(CPoint2 cpos) {
		// filter feedback points close to the center
		if(cpos.toPPoint2().r > TreesModel.CENTER_BODY_RADIUS*1.1f)
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
