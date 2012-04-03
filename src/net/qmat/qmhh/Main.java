/*
 * Main class, this is the main processing applet, the origin of EVERYTHING.
 * 
 */

// TODO: change implementation of spore moving through outer perimeter to categoryBits

package net.qmat.qmhh;

import net.qmat.qmhh.controllers.ContactController;
import net.qmat.qmhh.controllers.Controllers;
import net.qmat.qmhh.models.Background;
import net.qmat.qmhh.models.Models;
import net.qmat.qmhh.utils.Settings;
import pbox2d.PBox2D;
import codeanticode.glgraphics.GLConstants;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import processing.core.*;
import processing.core.PApplet;

public class Main extends PApplet {

	private static final long serialVersionUID = 1L;
	public static Main p;
	public static PBox2D box2d;
	public int frameCount = 0;
	
	/* 
	 * The static visual stuff..
	 */
	private Background bg;

	/*
	 * Cache for speed.
	 */
	public static int centerX, centerY;
	public static int table;
	public static float outerRingInnerRadius, outerRingOuterRadius;

	public Main() {
		p = this;
		centerX = Settings.getInteger(Settings.PR_CENTER_X);
		centerY = Settings.getInteger(Settings.PR_CENTER_Y);
		table = Settings.getInteger(Settings.TABLE);
		outerRingInnerRadius = Settings.getInteger(Settings.PR_RING_INNER_RADIUS);
		outerRingOuterRadius = Settings.getInteger(Settings.PR_RING_OUTER_RADIUS);
	}

	public void setup() {

		/* Set up processing stuff, size() should always be the first call in setup() */
		size(Settings.getInteger(Settings.PR_WIDTH),
				Settings.getInteger(Settings.PR_HEIGHT),
				GLConstants.GLGRAPHICS);
		p.hint(DISABLE_DEPTH_TEST);

		box2d = new PBox2D(this);
		box2d.createWorld();
		box2d.setGravity(0.0f, 0.0f);
		box2d.world.setContactListener(new ContactController());

		bg = new Background();

		/* 
		 * N.B. The controllers should be loaded at the end of setup(), otherwise
		 * the tuio events might trigger actions before everything is set up properly.
		 */
		Controllers.init(); 
		Models.init();
		Controllers.initTuio(); 

		/*
		 * Creatures some creatures for testing
		for(int i=0; i<3; i++) {
			Models.getCreaturesModel().addCreature();
		}
		*/
		
		// Add a hand for testing
		/*
		Models.getHandsModel().addHand(1L, 80f, 700f); //740f, 325.0f);
		Models.getHandsModel().addHand(2L, 730f, 40f); //740f, 325.0f);
		Models.getHandsModel().addHand(3L, 730f, 730f); //740f, 325.0f);
		Models.getHandsModel().addHand(4L, 30f, 30f); //740f, 325.0f);
		Models.getHandsModel().addHand(5L, 320f, 30f); //740f, 325.0f);
		Models.getHandsModel().addHand(6L, 320f, 730f); //740f, 325.0f);
		Models.getHandsModel().addHand(7L, 730f, 326f); //740f, 325.0f);
		Models.getHandsModel().addHand(7L, 30f, 326f); //740f, 325.0f);
		*/

		smooth();
	}

	public void draw() {
		Controllers.update();
		//draw background
		bg.draw();
		//p.background(255);
		Models.update();
		Models.draw();

		box2d.step();
		frameCount++;
		
		/*
		if(frameCount == 60) {
			// Add a hand for testing
			Controllers.getHandsController().addHand(1L, 0.0f, 0.0f); //740f, 325.0f);
			Controllers.getHandsController().addHand(2L, 1.0f, 1.0f); //740f, 325.0f);
			Controllers.getHandsController().removeHand(1L);
			Controllers.getHandsController().removeHand(2L);
			Controllers.getHandsController().addHand(1L, 0.1f, 0.9f); //740f, 325.0f);
			Controllers.getHandsController().addHand(2L, 0.9f, 0.1f); //740f, 325.0f);
			Controllers.getHandsController().addHand(3L, 0.9f, 0.9f); //740f, 325.0f);
			Controllers.getHandsController().addHand(4L, 0.1f, 0.1f); //740f, 325.0f);
		}
		*/
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

	public static void main(String args[]) {
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

		if(Settings.getBoolean(Settings.PR_FULLSCREEN)) {
			String location = "--location=0,0";
			PApplet.main(new String[] {location, "net.qmat.qmhh.Main" });
		} else {
			PApplet.main(new String[] { "net.qmat.qmhh.Main" });
		}
	}

	public void init(){
		if(frame!=null){
			frame.removeNotify();//make the frame not displayable
			frame.setResizable(false);
			frame.setUndecorated(true);
			println("frame is at "+frame.getLocation());
			frame.addNotify();
		}
		super.init();
	}

	public void mousePressed() {
		Models.getSporesModel().startRipple(10f);
	}
}