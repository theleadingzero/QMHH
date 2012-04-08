package net.qmat.qmhh.models.trees;

import java.util.Iterator;
import java.util.Vector;

import net.qmat.qmhh.Main;
import net.qmat.qmhh.utils.CPoint2;
import net.qmat.qmhh.utils.PPoint2;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

public class CherryBlossomBranchDrawer extends BranchDrawerBase {

	public CherryBlossomBranchDrawer() {
		super();
	}
	/*
	 * Fixes needed
	 * v - depending on the branch level it has to be differently resistant
	 * v - the first branch start point should not move
	 * v - the odd bug of the growing line where it is always around 180 degrees
	 * v - the "doubling of the lines"
	 * may be reverse dependancy of how curved branch is to the level of it
	 * 
	 */
	public void drawBranch(Branch branch) {

		if(branch.activeP) {		
			if(shouldDrawP(branch)) {
				p.curveTightness(0.1f);

				Vector<CPoint2> points = branch.getParentPoints();
				points.add(branch.getMiddleEndPosition());
				/*
				PPoint2 firstPPoint = points.firstElement().toPPoint2();
				//firstPPoint.r = Tree.BRANCH_START_LENGTH/2f;
				firstPPoint.r = branch.length/2f;
				CPoint2 firstCPoint = firstPPoint.toCPoint2();
				points.set(0, firstCPoint);
				 */
				//shadow lines
				//p.noFill();
				//p.stroke(56, 14, 4, 80);
				//p.strokeWeight(2.8f);
				//drawLineShadows(points,branch);

				//lines
				p.noFill();
				p.stroke(190, 90, 205, 100);
				p.strokeWeight(0.8f);
				p.beginShape();
				p.curveVertex(Main.centerX, Main.centerY);
				/*
				for(CPoint2 cpos : points) {					
					p.curveVertex(cpos.x, cpos.y);					
				}*/
				CPoint2 growingEndPoint=new CPoint2(0,0);
				drawLine(points, branch);
				//if the last is growing
				if(!branch.stoppedGrowing){
					growingEndPoint=drawLastLine(points, branch);
				}else if(branch.stoppedGrowing) {	
					PPoint2 endGuidePPoint = points.lastElement().toPPoint2();
					CPoint2 endGuideCPoint = new PPoint2(endGuidePPoint.r*1.2f, endGuidePPoint.t).toCPoint2(); 
					p.curveVertex(endGuideCPoint.x, endGuideCPoint.y);
				}
				p.endShape();


				p.stroke(190, 90, 205, 255);
				p.strokeWeight(0.8f);
				for(int i=0; i<points.size();i++){	
					float tx=branch.length*i/18;
					float ty=branch.length*i/18;
					float mx,my;
					if (i>0){
						mx=points.get(i-1).x+(points.get(i).x-points.get(i-1).x)/2+tx/4;
						my=points.get(i-1).y+(points.get(i).y-points.get(i-1).y)/2+ty/4;
					}else {
						mx=points.get(i).x+tx;
						my=points.get(i).y+ty;
					}
					//tree points shadow for the middlepoints
					//shadowing
					p.noStroke();
					p.pushMatrix();
					p.scale(1.3f);
					p.translate(-(Main.centerX-Main.centerX/1.3f),-(Main.centerY-Main.centerY/1.3f));
					p.fill(180, 240, 120, 40);
					//drawPoint(mx, my);
					p.popMatrix();

					//tree points for the middlepoints
					p.stroke(190, 90, 205, 255);
					//p.stroke(180, 240, 120, 255);
					p.fill(255, 0, 0, 255);
					//drawPoint(mx, my);
					

					//tree points shadow for the endpoints
					p.noStroke();
					p.pushMatrix();
					p.scale(1.3f);
					p.translate(-(Main.centerX-Main.centerX/1.3f),-(Main.centerY-Main.centerY/1.3f));
					p.fill(150, 180, 120, 50);
					if(!branch.stoppedGrowing&&i==points.size()-1){
						//p.fill(255, 0, 0, 255);
						//drawPoint(growingEndPoint.x, growingEndPoint.y);
						//drawLeaf(growingEndPoint.x, growingEndPoint.y, branch);
						
					}
					else {
						//p.fill(255, 255, 0, 255);
						//drawPoint(points.get(i).x, points.get(i).y);
						//drawLeaf(points.get(i).x, points.get(i).y, branch);
						
					}
					p.popMatrix();

					//tree points for the endpoints
					p.stroke(190, 90, 205, 255);
					if(!branch.stoppedGrowing&&i==points.size()-1){
						p.fill(255, 0, 0, 255);
						//drawPoint(growingEndPoint.x, growingEndPoint.y);
						drawLeaf(growingEndPoint.x, growingEndPoint.y, branch);
					}
					else {
						p.fill(255, 255, 0, 255);
						//drawPoint(points.get(i).x, points.get(i).y);
						//drawLeaf(mx, my, branch);
						
					}
				}

			}
		}
	}

	/*public void drawLineShadows(Vector<CPoint2> points, Branch branch){
		//shadowing
		p.pushMatrix();

		p.scale(1.3f);
		p.translate(-(Main.centerX-Main.centerX/1.3f),-(Main.centerY-Main.centerY/1.3f));

		p.beginShape();
		p.curveVertex(Main.centerX, Main.centerY);
		
		for(CPoint2 cpos : points) {					
			p.curveVertex(cpos.x, cpos.y);					
		}
		CPoint2 growingEndPoint=new CPoint2(0,0);
		drawLine(points, branch);
		//if the last is growing
		if(!branch.stoppedGrowing){
			growingEndPoint=drawLastLine(points, branch);
		}else if(branch.stoppedGrowing) {	
			PPoint2 endGuidePPoint = points.lastElement().toPPoint2();
			CPoint2 endGuideCPoint = new PPoint2(endGuidePPoint.r*1.2f, endGuidePPoint.t).toCPoint2(); 
			p.curveVertex(endGuideCPoint.x, endGuideCPoint.y);
		}
		p.endShape();
		p.popMatrix();
		///end 

	}*/
	public void drawLine(Vector<CPoint2> points, Branch branch){
		for(int i=0;(branch.stoppedGrowing&&i<points.size())||i<points.size()-1;i++){
			int tempi=(Tree.MAX_BRANCH_LEVELS-i);
			if(tempi>=points.size())tempi=i;

			float mx,my;
			float tx=branch.length*(i)/18;
			float ty=branch.length*(i)/18;
			if(Tree.MAX_BRANCH_LEVELS-((points.size()-1-i))%2==0){;
			tx*=-1; ty*=-1;	
			}
			if(Tree.MAX_BRANCH_LEVELS-((points.size()-1-i))%3==0){;
			tx*=1; ty*=-1;	
			}
			if(Tree.MAX_BRANCH_LEVELS-((points.size()-1-i))%5==0){;
			tx*=-1; ty*=1;	
			}
			if (i>0){
				mx=points.get(i-1).x+(points.get(i).x-points.get(i-1).x)/2+tx;
				my=points.get(i-1).y+(points.get(i).y-points.get(i-1).y)/2+ty;
			}else {
				mx=points.get(i).x+tx;
				my=points.get(i).y+ty;
			}
			p.curveVertex(mx, my);
			p.curveVertex(points.get(i).x, points.get(i).y);
		}
	}
	public CPoint2 drawLastLine(Vector<CPoint2> points, Branch branch){
		int i=points.size()-1;
		float tempi=(Tree.MAX_BRANCH_LEVELS-points.size()+1);
		if(tempi>=points.size())tempi=(points.size()-1);
		
		float mx,my;				
		float howLong;
		howLong = (float)((double)(System.nanoTime() - branch.startGrowTimestamp) / (double)Tree.BRANCH_GROW_TIME);
		if(howLong>=1)howLong=1;

		float tx=howLong*branch.length*howLong*howLong*i/18;
		float ty=howLong*branch.length*howLong*howLong*i/18;

		if(Tree.MAX_BRANCH_LEVELS-((points.size()-1-i))%2==0){
			tx*=-1; ty*=-1;	
		}
		if(Tree.MAX_BRANCH_LEVELS-((points.size()-1-i))%3==0){
			tx*=1; ty*=-1;	
		}
		if(Tree.MAX_BRANCH_LEVELS-((points.size()-1-i))%5==0){
			tx*=-1; ty*=1;	
		}
		PPoint2  beforePPoint=points.get(i-1).toPPoint2();
		PPoint2 lastPPoint = points.get(i).toPPoint2();
		//if((lastPPoint.r-beforePPoint.r)<10)lastPPoint.t +=10;//Main.TWO_PI-lastPPoint.t ;
		//if(beforePPoint.t==Main.PI)beforePPoint.t +=10;//Main.TWO_PI-beforePPoint.t ;
		lastPPoint.r = (lastPPoint.r-beforePPoint.r)*howLong*howLong+beforePPoint.r;
		lastPPoint.t = (PPoint2.calculateAngularDistance(lastPPoint.t, beforePPoint.t)*howLong*howLong+beforePPoint.t);

		CPoint2 lastCPoint = lastPPoint.toCPoint2();
		if (i>0){
			mx=points.get(i-1).x+(lastCPoint.x-points.get(i-1).x)/2+tx;
			my=points.get(i-1).y+(lastCPoint.y-points.get(i-1).y)/2+ty;
		}else {
			mx=points.get(i).x+tx;
			my=points.get(i).y+ty;			
		}				
		p.curveVertex(mx, my);
		p.curveVertex(lastCPoint.x, lastCPoint.y);	

		CPoint2 endGuideCPoint = new PPoint2(lastPPoint.r*1.2f, lastPPoint.t).toCPoint2();
		p.curveVertex(endGuideCPoint.x, endGuideCPoint.y);
		/*if(howLong >= 1)
			branch.stoppedGrowing = true;*/
		return lastCPoint;
	}

	public void drawPoint(float x, float y){
		p.strokeWeight(0.8f);
		p.ellipse(x, y, 1.8f, 1.8f);
		//p.println(y);
	}
	public void drawLeaf(float x, float y,Branch branch){
		// only draw the branch if it is marked as active
				float leafSize = 1.f + Main.sqrt(Main.pow(branch.endX-branch.startX, 2) + 
												 Main.pow(branch.endY-branch.startY, 2)) / 160.0f;
				/*if(branch.activeP) {
					float alpha = 255.0f;
					// if the branch hasn't stopped growing, the alpha is less than 255.0
					if(!branch.stoppedGrowing) {
						alpha = 255.0f * (float)((double)(System.nanoTime() - branch.startGrowTimestamp) / (double)Tree.BRANCH_GROW_TIME);
						if(alpha >= 255.0f)
							branch.stoppedGrowing = true;
					}*/
					//p.stroke(0, 100, 0, 255);
					//p.fill(0, 255, 0, 255);
					
					// Get the shape of the box2d body, and get its coordinates
					/*p.beginShape();
					// get the body transform so that we can convert the shape's coordinates to world coordinates 
					Transform transform = branch.body.getTransform();
					Vec2 pos;
					for(Fixture f=branch.body.getFixtureList(); f!=null; f=f.getNext()) {
						PolygonShape shape = (PolygonShape) f.getShape();
						for(int i=0; i<shape.getVertexCount(); i++) {
							// apply the transform to the shape coordinates, and 
							// then convert box2d coordinates to processing coordinates
							pos = box2d.coordWorldToPixels(Transform.mul(transform, shape.getVertex(i)));
							p.vertex(pos.x, pos.y);
						}
					}
					p.endShape(Main.CLOSE);*/
					
					// draw leafs after the other branches
					p.pushMatrix();
					CPoint2 leafPos = branch.getLeafPosition();
					p.translate(x, y);
					//p.rotate(leafAngle);
					
					p.noStroke();
					
					p.ellipseMode(Main.CORNER);
					float s = 0.3f;
					for(int i=7; i>2; i--) {
						p.fill(110-(i*3), 180-(i*3), 255-(i*3), 255 * 0.8f);
						p.rotate(-branch.body.getAngle() + (-0.1f*i) * Main.PI);// + Main.PI);
						s = leafSize*i;
						p.ellipse(0, 0, s, s);
					}
					p.ellipse(0, 0, s, s);
					p.popMatrix();
					p.strokeWeight(1.0f);
				//}
			}
	//}
	public boolean shouldDrawP(Branch branch) {
		// if the branch is not active, then no
		if(!branch.isActiveP())
			return false;
		// if the branch has no children we should draw
		if(branch.branches.size() <= 0) return true;
		// if the branch has active children we shouldn't draw
		for(Branch br : branch.branches) {
			if(br.isActiveP())
				return false;
		}
		// all other situations
		return true;
	}

	public void drawliner(Branch br, float[][] points, float alpha){
		///	
		p.stroke(120, 0, 200, alpha);
		p.strokeWeight(0.1f);
		p.fill(190, 90, 255, alpha);

		Vec2 position = box2d.getBodyPixelCoord(br.body);
		float angleB = br.body.getAngle();
		float tHX = br.length * Main.cos(angleB);
		float tHY = br.length * Main.sin(angleB);
		float midStart[]={
				(points[0][0]+points[1][0])/2,
				(points[0][1]+points[1][1])/2,
		};
		float midEnd[]={
				(points[2][0]+points[3][0])/2,
				(points[2][1]+points[3][1])/2,
		};


		p.beginShape();
		//p.line(br.startX,br.startY,br.endX,br.endY);
		//p.curveVertex(points[0][0],points[0][1]+tHY/10);
		p.curveVertex(points[0][0],points[0][1]);
		//p.curveVertex(points[0][0]+tHX/2,points[0][1]+tHY/2);
		p.curveVertex(points[2][0],points[2][1]);
		//p.curveVertex(points[2][0],points[2][1]-tHY/10);

		p.endShape();//Main.CLOSE

		p.ellipse(midStart[0], midStart[1], 5, 5);

	}

	
}
