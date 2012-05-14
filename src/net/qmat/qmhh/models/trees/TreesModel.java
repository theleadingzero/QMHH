package net.qmat.qmhh.models.trees;

import java.util.ArrayList;

import net.qmat.qmhh.Main;
import net.qmat.qmhh.models.ProcessingObject;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public class TreesModel extends ProcessingObject {
	
	private static int NUMBER_OF_TREES = 13;
	public static float CENTER_BODY_RADIUS = 40.0f;
	
	private ArrayList<Tree> trees;
	private Body body;
	
	public TreesModel() {
		trees = new ArrayList<Tree>();
		createCenterBody();
		for(int i=0; i<NUMBER_OF_TREES; i++) {
			trees.add(new Tree(getTreeAngle(i), 
					  		   Main.PI / (float)NUMBER_OF_TREES,
					  		   body));
		}
	}
	
	private void createCenterBody() {
		BodyDef bd = new BodyDef();
		bd.type = BodyType.STATIC;
		Vec2 center = box2d.coordPixelsToWorld(Main.centerX, Main.centerY);
		bd.position.set(center);
		CircleShape circle = new CircleShape();
		circle.m_radius = box2d.scalarPixelsToWorld(CENTER_BODY_RADIUS);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		//fixtureDef.filter.groupIndex = Settings.getInteger(Settings.PR_SPORE_COLLISION_GROUP);
		//fixtureDef.filter.maskBits = 0x0000;
		body = box2d.createBody(bd);
		body.createFixture(fixtureDef); 
	}
	
	private float getTreeAngle(int treeIndex) {
		return (Main.TWO_PI / NUMBER_OF_TREES) * treeIndex;
	}
	
	public void draw() {
		for(int i=0; i<trees.size(); i++) {
			trees.get(i).draw();
		}
	}
	
	public void destroy() {
		for(Tree tree : trees) {
			tree.destroy();
		}
	}
	
}
