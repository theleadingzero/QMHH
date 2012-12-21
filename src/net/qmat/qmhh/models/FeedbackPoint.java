package net.qmat.qmhh.models;

import net.qmat.qmhh.utils.CPoint2;

public class FeedbackPoint extends ProcessingObject {

	int framesLoop = 30;
	int loops = 2;
	float maxDiameter = 15;
	private CPoint2 position;
	private int startFrame;


	FeedbackPoint(CPoint2 cpos) {
		position = cpos;
		startFrame = p.frameCount;
	}


	public void draw() {
		if(p.frameCount - startFrame > framesLoop*loops)
			markForRemoval();
		else {
			p.noFill();
			p.stroke(226, 245, 252, 100); //old colors 155,111,58

			p.pushMatrix();
			p.translate(position.x, position.y);

			drawFeedback(1, 7, 30);
			drawFeedback(3, 3, 75);
			drawFeedback(2, 1, 155);

			p.popMatrix();
		}
	}


	public void drawFeedback(int nrCircles, float weight, float alpha) {
		p.strokeWeight(weight);
		for(int i=1; i<=nrCircles; i++) {
			float index = (((p.frameCount - startFrame) + framesLoop / nrCircles * i) % framesLoop)/(float)framesLoop;
			float diameter = index * maxDiameter; 
			p.stroke(226, 245, 252, alpha * (0.4f-index) * (0.8f * (0.4f - ((p.frameCount - startFrame) / (float)(framesLoop * loops)))));
			p.ellipse(0, 0, diameter, diameter);
		}
	}


}
