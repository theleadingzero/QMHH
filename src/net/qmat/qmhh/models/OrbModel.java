package net.qmat.qmhh.models;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;

import net.qmat.qmhh.Main;
import net.qmat.qmhh.models.trees.TreesModel;
import net.qmat.qmhh.utils.Settings;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;

import com.sun.opengl.util.BufferUtil;

public class OrbModel extends ProcessingObject {

	private float radius, r;
	private float maxRadius, minRadius;

	private int numSystems = 40;
	private int numParticles = 15;
	private OrbPSystem[] ps;
	private float theta, theta2 = 0.0f;
	private float amplitude; 
	private float particleRadius;
	
	private FloatBuffer vbuffer;
	private int totalParticles;
	private float chargeIndex;
	
	private float[] sporeColor1;
	private float[] sporeColor2;
	

	public OrbModel() {
		ps =  new OrbPSystem[numSystems];
		maxRadius = Settings.getInteger(Settings.PR_ORB_MAX_RADIUS);
		minRadius = Settings.getInteger(Settings.PR_ORB_MIN_RADIUS);
		radius = 5.5f;//8.0f;
		particleRadius = radius;
		r = 7.0f;
		amplitude = 50.0f;
		
		float inx = Main.centerX;
		float iny = Main.centerY;
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
		
		sporeColor1 = Settings.getFloatArray(Settings.PR_SPORE_COLOR_1);
		sporeColor2 = Settings.getFloatArray(Settings.PR_SPORE_COLOR_2);
	}

	public void draw() {
		p.noFill();
		p.strokeWeight(4.0f);
		//p.fill(23, 31, 77);
		//p.ellipse(Main.centerX, Main.centerY, 25f, 25f);
		p.stroke(255, 255, 255, 20);
		p.ellipse(Main.centerX, Main.centerY, TreesModel.CENTER_BODY_RADIUS*2-2, TreesModel.CENTER_BODY_RADIUS*2-2);
		p.stroke(198, 20, 20, 100);
		p.arc(Main.centerX, Main.centerY, TreesModel.CENTER_BODY_RADIUS*2-2, TreesModel.CENTER_BODY_RADIUS*2-2, 0.0f, Main.TWO_PI * chargeIndex);
		p.strokeWeight(1.0f);
		
		waveR();
		for(int i=0; i<numSystems; i++) {
			ps[i].draw();
		}

		vbuffer.rewind();
		
		PGraphicsOpenGL pgl = (PGraphicsOpenGL) p.g;  // g may change
	    GL gl = pgl.beginGL();  // always use the GL object returned by beginGL
	     
	    gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
	    gl.glVertexPointer(2, GL.GL_FLOAT, 0, vbuffer);

	    gl.glPointSize(3.0f);
	    gl.glColor4f(sporeColor1[0], sporeColor1[1], sporeColor1[2], sporeColor1[3]);
	    gl.glDrawArrays(GL.GL_POINTS, 0, totalParticles);

	    gl.glPointSize(1.0f);
	    gl.glColor4f(sporeColor2[0], sporeColor2[1], sporeColor2[2], sporeColor2[3]);

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
