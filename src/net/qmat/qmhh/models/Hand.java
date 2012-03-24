package net.qmat.qmhh.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import net.qmat.qmhh.Main;
import net.qmat.qmhh.controllers.Controllers;
import net.qmat.qmhh.models.creatures.CreatureBase;
import net.qmat.qmhh.utils.CPoint2;
import net.qmat.qmhh.utils.PPoint2;

import processing.opengl.*;
import codeanticode.glgraphics.*;

public class Hand extends ProcessingObject {

	private float x = 0.0f;
	private float y = 0.0f;
	private boolean rebuildBeamP;
	private Beam beam;
	private boolean markedForRemovalP = false;
	private boolean chargingP = true;
	private Long startTime;
	private Double maxHandSize = 30.0;
	private ArrayList<CreatureBase> beamCreatures;
	private float indexOffset;
	private float cycle = 2000.0f;
	
	private static GLTexture srcTex, bloomMask;
	private static GLTexture tex0, tex2, tex4, tex8, tex16;
	private static GLTextureFilter extractBloom, blur, blend4;
	private static GLGraphicsOffScreen glg1;

	public Hand(float x, float y) {
		startTime = System.nanoTime();
		beamCreatures = new ArrayList<CreatureBase>();
		updatePosition(x, y);
		rebuildBeamP = true;
		indexOffset = p.random(1.0f);
	}
	
	private void initGL() {
		glg1 = new GLGraphicsOffScreen(p, 125, 125);
	    extractBloom = new GLTextureFilter(p, "ExtractBloom.xml");
	    blur = new GLTextureFilter(p, "Blur.xml");
	    blend4 = new GLTextureFilter(p, "Blend4.xml");  
	    int w = glg1.width;
	    int h = glg1.height;
	    
	    // Initializing bloom mask and blur textures.
	    bloomMask = new GLTexture(p, w, h, GLTexture.FLOAT);
	    tex0 = new GLTexture(p, w, h, GLTexture.FLOAT);
	    tex2 = new GLTexture(p, w / 2, h / 2, GLTexture.FLOAT);
	    tex4 = new GLTexture(p, w / 4, h / 4, GLTexture.FLOAT);
	    tex8 = new GLTexture(p, w / 8, h / 8, GLTexture.FLOAT);
	    tex16 = new GLTexture(p, w / 16, h / 16, GLTexture.FLOAT);
	}

	public int nrBeamCreatures() {
		return beamCreatures.size();
	}

	public void addCreature(CreatureBase creature) {
		if(nrBeamCreatures() == 0)
			Controllers.getSoundController().beamBlocked();
		if(!beamCreatures.contains(creature))
			beamCreatures.add(creature);
	}

	public void removeCreature(CreatureBase creature) {
		if(beamCreatures.contains(creature))
			beamCreatures.remove(creature);
		if(nrBeamCreatures() == 0)
			Controllers.getSoundController().beamUnblocked();
	}

	public void updatePosition(float x, float y) {
		this.x = x;
		this.y = y;
		rebuildBeamP = true;
	}

	public CPoint2 getCPosition() {
		return new CPoint2(x, y);
	}

	private void rebuildBeam() {
		if(beam != null) beam.rebuildShape();
		else beam = new Beam(this);
		rebuildBeamP = false;
	}


	public void markForRemoval() {
		markedForRemovalP = true;
	}

	public boolean isMarkedForRemoval() {
		return markedForRemovalP;
	}

	public void draw() {
		if(!markedForRemovalP) {
			if(bloomMask == null) initGL();
			// are we still charging?
			Long now = System.nanoTime();
			Double chargeIndex = (now - startTime) / HandsModel.chargeTimeNano;
			Double handSize;
			if(chargeIndex < 1.0) {
				chargingP = true;
				rebuildBeamP = false;
			} else {
				chargingP = false;
				rebuildBeamP = true;
			}
			
			// rebuild and draw the beam
			if(rebuildBeamP) rebuildBeam();
			if(beam != null) beam.draw();

			// draw hand		
			glg1.beginDraw();
			glg1.background(0);
			glg1.fill(200);
			glg1.stroke(255);
			glg1.pushMatrix();
			glg1.translate(glg1.width/2.0f,glg1.height/2.0f);
			glg1.beginShape();
			
			int steps = 51;
			int now2 = p.millis();
			float index = (now2 % cycle) / cycle + indexOffset;
			
			for(int i=0; i<steps+1; i++) {
				float angle = (Main.TWO_PI) / steps * i;
				float length = 34.0f;
				if(i % 2 == 0)
					length += 1.0 + Main.sin((index+i*0.02f) * Main.TWO_PI) * 20.0f;
				float o = Main.sin(angle) * length;
				float a = Main.cos(angle) * length;
				//curveVertex(o, a);
				glg1.vertex(o, a);
			}
			glg1.endShape(Main.CLOSE);
			glg1.popMatrix();
			glg1.endDraw();
			
			// Extracting the bright regions from input texture.
			srcTex = glg1.getTexture();
		    extractBloom.setParameterValue("bright_threshold", 0.2f);
		    extractBloom.apply(srcTex, tex0);
		  
		    // Downsampling with blur.
		    tex0.filter(blur, tex2);
		    tex2.filter(blur, tex4);    
		    tex4.filter(blur, tex8);    
		    tex8.filter(blur, tex16);     
		    
		    // Blending downsampled textures.
		    blend4.apply(new GLTexture[]{tex2, tex4, tex8, tex16}, new GLTexture[]{bloomMask});
			
			p.pushMatrix();
			p.translate(x, y);
			p.image(bloomMask, 0, 0, srcTex.width, srcTex.height);
			p.popMatrix();
		}
	}

	public void destroy() {
		if(beam != null) beam.destroy();
	}

	public class Beam extends ProcessingObject {

		private Body body;
		public Hand hand;

		Beam(Hand hand) {
			this.hand = hand;
			makeBody();
		}

		private void rebuildShape() {
			// update size of the body
			Fixture f = body.getFixtureList();
			while(f != null) {
				//body.destroyFixture(f);
				PolygonShape sd = (PolygonShape)f.m_shape;
				Vec2[] vs = getShapeVertices();
				sd.set(vs, 4);
				f = f.getNext();
			}
			//body.createFixture(createFixture());
		}

		private void makeBody() {

			FixtureDef fd = createFixture();

			BodyDef bd = new BodyDef();
			bd.type = BodyType.STATIC;
			// set position to be in between the center and the hand position
			//bd.position.set(box2d.coordPixelsToWorld(new Vec2((hand.x + Main.centerX)*0.5f, (hand.y + Main.centerY)*0.5f)));
			// or set the position of the static body to the middle
			bd.position.set(box2d.coordPixelsToWorld(new Vec2(Main.centerX, Main.centerY)));
			//bd.angle = new CPoint2(hand.x, hand.y).toPPoint2().t;

			body = box2d.createBody(bd);
			body.createFixture(fd);
			body.setUserData(this);
		}

		private FixtureDef createFixture() {
			PolygonShape sd = new PolygonShape();
			Vec2[] vs = getShapeVertices();
			sd.set(vs, 4);
			FixtureDef fd = new FixtureDef();
			fd.shape = sd;
			fd.isSensor = true;
			return fd;
		}

		private Vec2[] getShapeVertices() {
			PPoint2 handPos = new CPoint2(x, y).toPPoint2();
			// TODO: calculate the actual angleOffset from the hand size
			float angleOffset = Main.TWO_PI / 92.0f;
			float stopRadius = getGreatestRadius();
			PPoint2 v1 = new PPoint2(handPos.r, handPos.t - angleOffset);
			PPoint2 v2 = new PPoint2(handPos.r, handPos.t + angleOffset);
			// offset from the middle is perpendicular to the beam
			PPoint2 v3 = new PPoint2(stopRadius, handPos.t + angleOffset);
			PPoint2 v4 = new PPoint2(stopRadius, handPos.t - angleOffset);
			Vec2 vs[] = new Vec2[4];
			vs[0] = box2d.coordPixelsToWorld(v1.toVec2());
			vs[1] = box2d.coordPixelsToWorld(v2.toVec2());
			vs[2] = box2d.coordPixelsToWorld(v3.toVec2());
			vs[3] = box2d.coordPixelsToWorld(v4.toVec2());
			return vs;
		}

		public void destroy() {
			box2d.destroyBody(body);
		}

		public float getGreatestRadius() {
			float r = 10.0f;
			for(CreatureBase c : hand.beamCreatures) {
				float cr = c.getPPosition().r + c.getRadius();
				if(cr > r) r = cr;
			}
			return r;
		}

		public void draw() {
			//System.out.println("creatures: " + hand.beamCreatures.size());
			p.fill(237, 212, 69, 150);
			p.stroke(234, 187, 31);
			p.beginShape();
			for(Fixture f=body.getFixtureList(); f!=null; f=f.getNext()) {
				PolygonShape shape = (PolygonShape) f.getShape();
				Transform transform = body.getTransform();
				for(int i=0; i<shape.getVertexCount(); i++) {
					Vec2 pos = shape.getVertex(i);
					Vec2 v2 = box2d.coordWorldToPixels(Transform.mul(transform, pos));
					p.vertex(v2.x, v2.y);
				}
			}
			p.endShape(Main.CLOSE);
		}

	}

}
