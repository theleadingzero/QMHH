package net.qmat.qmhh;

import fullscreen.FullScreen;
import processing.core.*;
import processing.opengl.*;
//import fullscreen.*; 

public class Main extends PApplet {
	
	public static Main p;
	
	private static final long serialVersionUID = 1L;
	TuioController tuioController;
	//FullScreen fs; 

	public void setup() {
		
		p = this;
    
	    /* Load settings at the start of the program so all the settings
	     * are cached before the rest of the code needs them. You should use the
	     * settings manager like so:
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
		
	    /* Set up processing stuff. */
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
		background(0);
	    stroke(255);
	    Models.draw();
	}
	
	public static float relativeToPixelsX(float x) {
		return x * p.width;
	}
	
	public static float relativeToPixelsY(float y) {
		return y * p.height;
	}
}