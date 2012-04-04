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

public class TechBranchDrawer extends BranchDrawerBase {

	public TechBranchDrawer() {
		super();
	}
	/*
	 * Fixes needed
	 * v - depending on the branch level it has to be differently resistant
	 * v - the first branch start point should not move
	 * v - the odd bug of the growing line where it is always around 180 degrees
	 * v - the "doubling of the lines"
	 * may be reverse dependency of how curved branch is to the level of it
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
				 
				//shadow lines
				p.noFill();
				p.stroke(180, 240, 120, 10);
				p.strokeWeight(2.8f);
				drawLineShadows(points,branch);
*/
				//lines
				p.noFill();
				p.stroke(184, 131, 17, 255);
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


				p.stroke(184, 131, 17, 255);
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
/*					//tree points shadow for the middlepoints
					//shadowing
					p.noStroke();
					p.pushMatrix();
					p.scale(1.3f);
					p.translate(-(Main.centerX-Main.centerX/1.3f),-(Main.centerY-Main.centerY/1.3f));
					p.fill(180, 240, 120, 40);
					drawPoint(mx, my);
					p.popMatrix();
*/
					//tree points for the middlepoints
					p.stroke(184, 131, 17, 255);
					p.fill(71, 124, 238, 255);
					drawPoint(mx, my);
/*
					//tree points shadow for the endpoints
					p.noStroke();
					p.pushMatrix();
					p.scale(1.3f);
					p.translate(-(Main.centerX-Main.centerX/1.3f),-(Main.centerY-Main.centerY/1.3f));
					p.fill(180, 240, 120, 40);
					if(!branch.stoppedGrowing&&i==points.size()-1){
						//p.fill(255, 0, 0, 255);
						drawPoint(growingEndPoint.x, growingEndPoint.y);
					}
					else {
						//p.fill(255, 255, 0, 255);
						drawPoint(points.get(i).x, points.get(i).y);
					}
					p.popMatrix();
*/
					//tree points for the endpoints
					p.stroke(184, 131, 17, 255);
					if(!branch.stoppedGrowing&&i==points.size()-1){
						p.fill(71, 124, 238, 255);
						drawPoint(growingEndPoint.x, growingEndPoint.y);
					}
					else {
						p.fill(71, 124, 238, 255);
						drawPoint(points.get(i).x, points.get(i).y);
					}
				}

			}
		}
	}

	public void drawLineShadows(Vector<CPoint2> points, Branch branch){
		//shadowing
		p.pushMatrix();

		p.scale(1.3f);
		p.translate(-(Main.centerX-Main.centerX/1.3f),-(Main.centerY-Main.centerY/1.3f));

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
		p.popMatrix();
		///end 

	}
	public void drawLine(Vector<CPoint2> points, Branch branch){
		for(int i=0;(branch.stoppedGrowing&&i<points.size())||i<points.size()-1;i++){
			int tempi=(Tree.MAX_BRANCH_LEVELS-i);
			if(tempi>=points.size())tempi=i;

			float mx,my;
			float tx=branch.length*(tempi)/18;
			float ty=branch.length*(tempi)/18;
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

		float tx=howLong*branch.length*howLong*howLong*tempi/18;
		float ty=howLong*branch.length*howLong*howLong*tempi/18;

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
