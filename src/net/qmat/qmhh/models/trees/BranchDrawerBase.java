package net.qmat.qmhh.models.trees;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

import net.qmat.qmhh.Main;
import net.qmat.qmhh.models.ProcessingObject;

public class BranchDrawerBase extends ProcessingObject {
	
	public BranchDrawerBase() {
		super();
	}
	
	public void drawBranch(Branch br) {
		float alpha = 255.0f;
		// if the branch hasn't stopped growing, the alpha is less than 255.0
		if(!br.stoppedGrowing) {
			alpha = 255.0f * (float)((double)(System.nanoTime() - br.startGrowTimestamp) / (double)Tree.BRANCH_GROW_TIME);
			if(alpha >= 255.0f)
				br.stoppedGrowing = true;
		}
		p.stroke(0, 100, 0, alpha);
		p.fill(0, 255, 0, alpha);

		// Get the shape of the box2d body, and get its coordinates
		p.beginShape();
		// get the body transform so that we can convert the shape's coordinates to world coordinates 
		Transform transform = br.body.getTransform();
		Vec2 pos;
		for(Fixture f=br.body.getFixtureList(); f!=null; f=f.getNext()) {
			PolygonShape shape = (PolygonShape) f.getShape();
			for(int i=0; i<shape.getVertexCount(); i++) {
				// apply the transform to the shape coordinates, and 
				// then convert box2d coordinates to processing coordinates
				pos = box2d.coordWorldToPixels(Transform.mul(transform, shape.getVertex(i)));
				p.vertex(pos.x, pos.y);
			}
		}
		p.endShape(Main.CLOSE);
	}

}
