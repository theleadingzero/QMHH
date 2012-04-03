package net.qmat.qmhh.models;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;

import net.qmat.qmhh.Main;
import net.qmat.qmhh.utils.Settings;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;

import com.sun.opengl.util.BufferUtil;

public class OrbModel extends ProcessingObject {

	private float radius, r;
	private float maxRadius, minRadius;

	private int numSystems = 60;
	private int numParticles = 15;
	private OrbPSystem[] ps;
	private float theta, theta2 = 0.0f;
	private float amplitude; 
	private float particleRadius;
	
	private FloatBuffer vbuffer;
	private int totalParticles;
	private float chargeIndex;

	public OrbModel() {
		ps =  new OrbPSystem[numSystems];
		maxRadius = Settings.getInteger(Settings.PR_ORB_MAX_RADIUS);
		minRadius = Settings.getInteger(Settings.PR_ORB_MIN_RADIUS);
		radius = 8.0f;
		particleRadius = radius;
		r = 7.0f;
		amplitude = 50.0f;
		
		float inx = p.width/2.0f;
		float iny = p.height/2.0f;
		float x, y;
		
		totalParticles = numParticles * numSystems;
		vbuffer = BufferUtil.newFloatBuffer(totalParticles * 2);

		for(int i=0; i<numSystems; i++){
			// dispose PSystems in a circle
			x = r * Main.cos(theta);
			y = r * Main.sin(theta);
			x += inx;
			y += iny;     
			ps[i] = new OrbPSystem(new PVector(x, y), 
								   numParticles, 
								   theta, 
								   r,
								   vbuffer);
			theta += Main.TWO_PI / numSystems;
		}
		
	}

	public void draw() {
		
		p.noStroke();
		
		p.ellipseMode(Main.CENTER);
		/*
		p.fill(120, 120, 245, 50);
		p.ellipse(Main.centerX, Main.centerY, 2*radius*3, 2*radius*3);
		p.fill(120, 120, 245, 50);
		p.ellipse(Main.centerX, Main.centerY, 2*radius*2, 2*radius*2);
		
		p.noStroke();
		p.fill(23, 31, 77);
		p.ellipse(Main.centerX, Main.centerY, 25f, 25f);
		p.fill(198, 20, 20);
		p.arc(Main.centerX, Main.centerY, 25f, 25f, 0.0f, Main.TWO_PI * chargeIndex);
		*/
		
		waveR();
		for(int i=0; i<numSystems; i++) {
			ps[i].draw();
		}

		vbuffer.rewind();
		
		PGraphicsOpenGL pgl = (PGraphicsOpenGL) p.g;  // g may change
	    GL gl = pgl.beginGL();  // always use the GL object returned by beginGL
	     
	    gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
	    gl.glVertexPointer(2, GL.GL_FLOAT, 0, vbuffer);

	    gl.glPointSize(1.5f);
	    gl.glColor4f(1.0f, 0.98f, 0.847f, 0.05f);
	    gl.glDrawArrays(GL.GL_POINTS, 0, totalParticles);

	    gl.glPointSize(1.0f);
	    gl.glColor4f(0.99f, 0.95f, 0.85f, 0.59f);
	    gl.glDrawArrays(GL.GL_POINTS, 0, totalParticles);
	     
	    pgl.endGL();
	    
	    // draw feedback
	    /*
	    p.noStroke();
		p.fill(23, 31, 77, 150);
		p.ellipse(Main.centerX, Main.centerY, 25f, 25f);
		//p.fill(198, 20, 20);
		p.fill(255, 225, 11, 100);
		p.stroke(255, 225, 11, 200);
		p.arc(Main.centerX, Main.centerY, 25f, 25f, 0.0f, Main.TWO_PI * chargeIndex);
		*/
	}

	private void waveR()
	{
		theta += 0.0f;
		r = theta;
		r = Main.abs(Main.sin(r)) * amplitude;
		r += particleRadius;
		for(int i=0; i<numSystems; i++) {
			ps[i].setR(r);
		}
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}
	
	public void setParticleRadius(float radius) {
		this.particleRadius = radius;
	}

	public void increaseRadius() {
		setRadius(radius+10.0f);
	}

	public void decreaseRadius() {
		setRadius(radius-10.0f);
	}
	
	public void setChargeIndex(float index) {
		chargeIndex = index;
	}

}
