/*
 * Class to help out with polar coordinates
 */

package net.qmat.qmhh.utils;

import net.qmat.qmhh.Main;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

public class PPoint2 {
	
	public float r, t;
	
	public PPoint2(float r, float t) {
		this.r = r;
		this.t = t;
	}
	
	public CPoint2 toCPoint2() {
		return new CPoint2((float)(r * Math.cos(t) + Main.centerX), 
						   (float)(-r * Math.sin(t) + Main.centerY));
	}
	
	public Vec2 toVec2() {
		return toCPoint2().toVec2();
	}
	
	public static PPoint2 fromBody(Body body) {
		return (new CPoint2(Main.box2d.getBodyPixelCoord(body)).toPPoint2());
	}
	
	public static float calculateAngularDistance(float a1, float a2) {
		float d = a1 - a2;
		if(d < -Main.PI) return d + Main.TWO_PI;
		if(d > Main.PI) return d - Main.TWO_PI;
		return d;
	}
}
