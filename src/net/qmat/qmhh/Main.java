package net.qmat.qmhh;

import fullscreen.FullScreen;
import processing.core.*;
import processing.opengl.*;
//import fullscreen.*; 

public class Main extends PApplet {
	
	private static final long serialVersionUID = 1L;
	TuioController tuioController;
	//FullScreen fs; 

	public void setup() {
    
	    /* Load settings at the start of the program so all the settings
	     * are cached before the rest of the code needs them. You should use the
	     * settings manager like so:
	     * 
	     * 	Settings.get(Settings.SETTING_NAME);
	     * 
	     * which will return a string. 
	     */
		Settings.getInstance();
		
	    /* Set up processing stuff.
		 */
		size(Settings.getInteger(Settings.PR_WIDTH), 
			 Settings.getInteger(Settings.PR_HEIGHT),
			 OPENGL);
		
		/*
		if(Settings.getBoolean(Settings.PR_FULLSCREEN)) {
			fs = new FullScreen(this);
			fs.setResolution(Settings.getInteger(Settings.PR_WIDTH), 
							 Settings.getInteger(Settings.PR_HEIGHT));
			fs.enter(); 
		} 
		*/
	    
	    /* N.B. The TuioController should be loaded at the end of setup(), otherwise
	     * the tuio events might trigger actions before everything is set up properly.
	     */
	    tuioController = new TuioController(this); 
    
	}

	public void draw() {
		background(0);
	    stroke(255);
	    tuioController.draw();
	}
}