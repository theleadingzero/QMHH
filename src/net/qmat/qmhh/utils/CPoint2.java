/*
 * Class to help out with cartesian coordinates
 */

package net.qmat.qmhh.utils;

import net.qmat.qmhh.Main;

import org.jbox2d.common.Vec2;

public class CPoint2 {
	
	public float x, y;
	
	public CPoint2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public CPoint2(Vec2 p) {
		this.x = p.x;
		this.y = p.y;
	}
	
	public Vec2 toVec2() {
		return new Vec2(x, y);
	}
	
	public PPoint2 toPPoint2() {
		return new PPoint2((float)Math.sqrt((x - Main.centerX) * (x - Main.centerX) +
				 							(y - Main.centerY) * (y - Main.centerY)),
				 		   (float)Math.atan2(Main.centerY - y, x - Main.centerX));
	}
}
