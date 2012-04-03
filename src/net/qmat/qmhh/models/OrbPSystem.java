package net.qmat.qmhh.models;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import net.qmat.qmhh.Main;
import processing.core.PVector;

public class OrbPSystem extends ProcessingObject {

	private float inx, iny;
	private float r;
	private float th;
	private PVector ps_loc; 
	private ArrayList<OrbParticle> particles;
	private FloatBuffer vbuffer;

	public OrbPSystem(PVector ps_loc, int num, float th, float r, FloatBuffer vbuffer)
	{
		inx = Main.centerX;
		iny = Main.centerY;
		this.r = r;
		this.ps_loc = ps_loc;
		this.th = th;
		this.vbuffer = vbuffer;
		particles = new ArrayList<OrbParticle>();
		for (int i = 0; i < num; i++) {
			particles.add(new OrbParticle(
					new PVector(), 
					new PVector(),
					new PVector(ps_loc.x, ps_loc.y, 0), 
					p.random(5.0f, 7.0f), i));
		}
	}

	public void draw() {
		th += 0.0025f;
		ps_loc.x = inx + r * Main.cos(th);
		ps_loc.y = iny + r * Main.sin(th);
		ps_loc.x += p.random(-5.0f, 5.0f);
		ps_loc.y += p.random(-5.0f, 5.0f);

		for(OrbParticle particle : particles) {     
			particle.update();
			particle.move(new PVector(ps_loc.x,ps_loc.y,0));
			
			vbuffer.put(particle.loc.x);
		    vbuffer.put(particle.loc.y);
		}
	}
	
	public void setR(float r) {
		this.r = r;
	}
	


}
