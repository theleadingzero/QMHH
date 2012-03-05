package net.qmat.qmhh;

import processing.core.*;
import processing.opengl.*;


public class Main extends PApplet {
	
	
	public static Main p;
	
	
	private static final long serialVersionUID = 1L;
	
	/* 
	 * The static visual stuff..
	 */
	private Background bg;
	
	/*
	 * Remember for speed.
	 */
	private int centerX, centerY;
	
	public Main() {

		p = this;
		
		/* 
		 * Load settings at the start of the program so all the settings
	     * are cached before the rest of the code needs them. Sadly, can't be 
	     * loaded before setup(), otherwise code in setup could execute twice.
	     * 
	     * You should use the settings manager like so:
	     * 
	     *  // returns a string
	     * 	Settings.get(Settings.SETTING_NAME); 
	     *  // tries to interpret the setting as a boolean and returns it.
	     *  Settings.getBoolean(Settings.SETTING_NAME);
	     *  // tries to interpret the setting as an integer and returns it.
	     *  Settings.getBoolean(Settings.SETTING_NAME); 
	     *  
	     */
		Settings.init();
		
		centerX = Settings.getInteger(Settings.PR_CENTER_X);
		centerY = Settings.getInteger(Settings.PR_CENTER_Y);
		
		bg = new Background();
	}

	public void setup() {
		
	    /* Set up processing stuff, size() should always be the first call in setup() */
		size(Settings.getInteger(Settings.PR_WIDTH),
			 Settings.getInteger(Settings.PR_HEIGHT),
			 OPENGL);
		
		/* Fullscreen doesn't work at the moment because we use OPENGL.
		if(Settings.getBoolean(Settings.PR_FULLSCREEN)) {
			fs = new FullScreen(this);
			fs.setResolution(Settings.getInteger(Settings.PR_WIDTH), 
							 Settings.getInteger(Settings.PR_HEIGHT));
			fs.enter(); 
		} 
		*/
	    
	    /* N.B. The controllers should be loaded at the end of setup(), otherwise
	     * the tuio events might trigger actions before everything is set up properly.
	     */
		Models.init();
		Controllers.init(); 
			
	}

	public void draw() {
		bg.draw();
	    stroke(255);
	    Models.draw();
	}
	
	/*
	 * Utility functions! It's funny because they're useful.
	 */
	
	public static float relativeToPixelsX(float x) {
		return x * p.width;
	}
	
	public static float relativeToPixelsY(float y) {
		return y * p.height;
	}
	
	// polar to cartesian
	public static CPoint2 p2c(float r, float t) {
		return new CPoint2((float)(r * Math.cos(t)), (float)(r * Math.sin(t)));
	}
	
	// cartesian to polar conversion
	public static PPoint2 c2p(float x, float y) {
		return new PPoint2((float)Math.sqrt((x - p.centerX) * (x - p.centerX) +
									 (y - p.centerY) * (y - p.centerY)),
						   (float)Math.atan2(y, x));
	}
}