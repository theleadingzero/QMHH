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
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;
import processing.opengl.*;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.nio.*;
import processing.core.*;

public class Main extends PApplet {

	private static final long serialVersionUID = 1L;
	public static Main p;
	public static PBox2D box2d;
	public static int frameCount = 0;

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
				OPENGL);
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
		 */
		for(int i=0; i<3; i++) {
			Models.getCreaturesModel().addCreature();
		}
	}

	public void draw() {
		Controllers.update();

		bg.draw();
		Models.update();
		Models.draw();

		box2d.step();
		frameCount++;
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
			int primary_display = 0; //index into Graphic Devices array...  
			int primary_width;
			GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice devices[] = environment.getScreenDevices();
			String location;
			if(devices.length>1 ){ //we have a 2nd display/projector
				primary_width = devices[0].getDisplayMode().getWidth();
				System.out.println(primary_width);
				location = "--location="+(primary_width+75)+",0";
			} else {//leave on primary display
				location = "--location=0,0";
			}
			String display = "--display="+(primary_display+1);  //processing considers the first display to be # 1
			PApplet.main(new String[] {location, display, "net.qmat.qmhh.Main" });
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
		/*
		if(p.random(0.0f, 1.0f) < 0.5f)
			Models.getOrbModel().increaseRadius();
		else
			Models.getOrbModel().decreaseRadius();
		 */
		//Models.getSporesModel().startRipple(Models.getOrbModel().getRadius()/2.0f);

	}
}