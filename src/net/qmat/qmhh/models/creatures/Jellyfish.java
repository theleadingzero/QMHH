package net.qmat.qmhh.models.creatures;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;

import net.qmat.qmhh.Main;

import org.jbox2d.common.Vec2;

import processing.opengl.PGraphicsOpenGL;

import com.sun.opengl.util.BufferUtil;

public class Jellyfish extends CreatureBase {

	private int numSegments = 4;
	private int maxSegments = 19;
	private float Ra = 150.0f;  // aperture / radius
	private float Ha = 100.0f;  // height
	private float angleSpace = Main.TWO_PI / numSegments;
	private float offsetX, offsetY;
	private float rot = 0.0f;
	private float[][] points = new float[maxSegments][2];
	private float velocitySum = 0.0f;
	private int steps = 6;
	private int maxSteps = 6;
	private FloatBuffer vbuffer;
	private Vec2 pos;

	Jellyfish() {
		super();
		vbuffer = BufferUtil.newFloatBuffer(maxSegments * (maxSteps+1) * 3 * 2);
	}

	public void draw() {

		//drawDebugShape();
		int nrVertices = numSegments * (steps+1) * 2;
		
		Vec2 velocity = body.getLinearVelocity(); 
		velocitySum += velocity.length() * 0.05f + 0.05f;
		
		Ha = ((0.9f + 0.1f * Main.sin(velocitySum)))*h;
		Ra = ((0.9f + 0.1f * Main.cos(velocitySum)))*w/3;

		for (int i = 0; i < numSegments; i++) {
			float x = Main.cos(i * angleSpace) * Ra;
			float y = Main.sin(i * angleSpace) * Ra;
			points[i][0] = x;
			points[i][1] = y;
		}
		
		pos = box2d.getBodyPixelCoord(body);
		
		// draw the umbrella
		for (int i = 0; i < numSegments - 1; i++) {
			umbrellaSegment(points[i][0], points[i][1], points[i + 1][0], points[i + 1][1], Ha);
		}
		// draw last segment of the umbrella
		umbrellaSegment(points[numSegments - 1][0], points[numSegments - 1][1], points[0][0], points[0][1], Ha);

		vbuffer.rewind();

		PGraphicsOpenGL pgl = (PGraphicsOpenGL) p.g;  // g may change
	    GL gl = pgl.beginGL();  // always use the GL object returned by beginGL
	    gl.glPushMatrix();
	     
	    gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
	    gl.glVertexPointer(3, GL.GL_FLOAT, 0, vbuffer);

	    gl.glTranslatef(pos.x, pos.y, 0);
	    gl.glRotatef(Main.atan2(velocity.x, velocity.y) * Main.RAD_TO_DEG, 0, 0, 1);
	    gl.glRotatef(velocity.length()/10.f * Main.RAD_TO_DEG, 1, 0, 0);
	    
	    gl.glColor4f(0.886f, 0.96f, 0.988f, 0.1f);
	    gl.glPointSize(2.0f);
	    gl.glDrawArrays(GL.GL_LINE_STRIP, 0, nrVertices);
	    /*
	    gl.glColor4f(1.0f, 1.0f, 1.0f, 0.7f);
	    gl.glDrawArrays(GL.GL_LINE_STRIP, 0, nrVertices);
	    */
	    gl.glPopMatrix();
	    pgl.endGL();		
	}
	
	public void grow() {
		super.grow();
		int tmpSegments = stage * maxSubStage + subStage + 4;
		if(tmpSegments<maxSegments) {
			numSegments = tmpSegments; 
			angleSpace = Main.TWO_PI / numSegments;
		}
	}

	void umbrellaSegment(float x1, float y1, float x2, float y2, float h) {
		for (int i=0; i<=steps; i++) {
			float t = i / (float)steps;
			float x = p.bezierPoint(x1, x1, x1, 0, t);
			float y = p.bezierPoint(y1, y1, y1, 0, t);
			float z = p.bezierPoint(0, h * 0.5f, h*0.4f, h, t);
			float bx = p.bezierPoint(x2, x2, x2, 0, t);
			float by = p.bezierPoint(y2, y2, y2, 0, t);
			float bz = p.bezierPoint(0, h * 0.5f, h * 0.4f, h, t);
			vbuffer.put(x); //+pos.x);
			vbuffer.put(y); //+pos.y);
			vbuffer.put(z);
			vbuffer.put(bx); //+pos.x);
			vbuffer.put(by); //+pos.y);
			vbuffer.put(bz);
		}
	}
}


