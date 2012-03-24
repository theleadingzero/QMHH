package net.qmat.qmhh.models.trees;

import net.qmat.qmhh.Main;
import net.qmat.qmhh.utils.CPoint2;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

public class DeepSeaBranchDrawer extends BranchDrawerBase {
	
	public DeepSeaBranchDrawer() {
		super();
	}
	
	public void drawBranch(Branch branch) {
		// only draw the branch if it is marked as active
		float leafSize = 3.f + Main.sqrt(Main.pow(branch.endX-branch.startX, 2) + 
										 Main.pow(branch.endY-branch.startY, 2)) / 50.0f;
		if(branch.activeP) {
			float alpha = 255.0f;
			// if the branch hasn't stopped growing, the alpha is less than 255.0
			if(!branch.stoppedGrowing) {
				alpha = 255.0f * (float)((double)(System.nanoTime() - branch.startGrowTimestamp) / (double)Tree.BRANCH_GROW_TIME);
				if(alpha >= 255.0f)
					branch.stoppedGrowing = true;
			}
			p.stroke(0, 100, 0, alpha);
			p.fill(0, 255, 0, alpha);
			
			// Get the shape of the box2d body, and get its coordinates
			p.beginShape();
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
			p.endShape(Main.CLOSE);
			
			// draw leafs after the other branches
			p.pushMatrix();
			CPoint2 leafPos = branch.getLeafPosition();
			p.translate(leafPos.x, leafPos.y);
			//p.rotate(leafAngle);
			p.rotate(-branch.body.getAngle() + -0.25f * Main.PI);// + Main.PI);
			p.noStroke();
			p.fill(0, 255, 0, alpha * 0.25f);
			p.ellipseMode(Main.CORNER);
			float s = 0.0f;
			for(int i=7; i>2; i--) {
				s = leafSize*i;
				p.ellipse(0, 0, s, s);
			}
			p.ellipse(0, 0, s, s);
			p.popMatrix();
			p.strokeWeight(1.0f);
		}
	}
}
