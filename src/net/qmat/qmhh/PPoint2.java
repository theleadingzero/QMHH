/*
 * Class to help out with polar coordinates
 */

package net.qmat.qmhh;

import org.jbox2d.common.Vec2;

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
}
