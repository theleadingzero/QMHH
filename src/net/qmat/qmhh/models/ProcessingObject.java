package net.qmat.qmhh.models;
import net.qmat.qmhh.Main;
import pbox2d.PBox2D;

public class ProcessingObject {
	
	protected Main p;
	protected PBox2D box2d;
	private boolean markedForRemovalP = false;
	
	/*
	 * Give all ProcessingObjects a reference to the main applet and 
	 * the box2d world.
	 */
	public ProcessingObject()
	{
		this.p = Main.p;
		this.box2d = Main.box2d;
	}
	
	/*
	 * If an object needs to do some calculations before it is drawn, put that
	 * stuff in the update() function. Flocking behaviour should be implemented 
	 * here for example.
	 */ 
	public void update() {}
	
	/*
	 * This draws the object on the screen
	 */
	public void draw() {}
	
	/*
	 *  This does any clean up, should only be necessary when dealing with box2d.
	 */
	//public void destroy() {}
	

	public void markForRemoval() {
		markedForRemovalP = true;
	}
	
	public boolean isMarkedForRemovalP() {
		return markedForRemovalP;
	}
	
	
	
	
}
