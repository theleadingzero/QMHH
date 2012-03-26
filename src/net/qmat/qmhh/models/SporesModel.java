package net.qmat.qmhh.models;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.media.opengl.GL;

import net.qmat.qmhh.Main;
import net.qmat.qmhh.utils.CPoint2;
import net.qmat.qmhh.utils.PPoint2;
import processing.opengl.PGraphicsOpenGL;

import com.sun.opengl.util.BufferUtil;

public class SporesModel extends ProcessingObject {
	
	ArrayList<Spore> spores;
	FloatBuffer vbuffer;
	
	public SporesModel() {
		spores = new ArrayList<Spore>();
	}
	
	public void startRipple(float startRadius) {
		synchronized(spores) {
			for(int i=0; i<25; i++)
				spores.add(new Spore(startRadius));
		}
	}
	
	public void removeSpore(Spore spore) {
		spore.markForRemoval();
	}
	
	public void update() {
		synchronized(spores) {
			// check if the spores are outside of the outer ring
			Iterator<Spore> it = spores.iterator();
			while(it.hasNext()) {
				Spore spore = it.next();
				PPoint2 ppos = spore.getPPosition();
				if(ppos.r > Main.outerRingOuterRadius || spore.getAbsoluteVelocity() < 1.0f || spore.isMarkedForRemoval()) {
					spore.destroy();
					it.remove();
				}
			}
		}
	}
	
	public void draw() {
		synchronized(spores) {
			int nrSpores = spores.size();
			if(nrSpores>0) {
				if(vbuffer == null || vbuffer.capacity()<nrSpores*2)
					vbuffer = BufferUtil.newFloatBuffer(nrSpores*2 * 2);
				// TODO: just make the spores ArrayList work concurrently
				for(Spore spore : spores) {
					CPoint2 cpos = spore.getCPosition();
					vbuffer.put(cpos.x);
					vbuffer.put(cpos.y);
				}
				vbuffer.rewind();
				
				PGraphicsOpenGL pgl = (PGraphicsOpenGL) p.g;  // g may change
			    GL gl = pgl.beginGL();  // always use the GL object returned by beginGL
			     
			    gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
			    gl.glVertexPointer(2, GL.GL_FLOAT, 0, vbuffer);
		
			    gl.glPointSize(Spore.w);
			    gl.glColor4f(0.47f, 0.47f, 1.0f, 0.89f);
			    gl.glDrawArrays(GL.GL_POINTS, 0, spores.size());
			     
			    pgl.endGL();
			}
		}
	}
	
}
