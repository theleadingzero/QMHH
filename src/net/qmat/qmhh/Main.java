/*
 * Main class, this is the main processing applet, the origin of EVERYTHING.
 * 
 */

// TODO: change implementation of spore moving through outer perimeter to categoryBits

package net.qmat.qmhh;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import net.qmat.qmhh.controllers.ContactController;
import net.qmat.qmhh.controllers.Controllers;
import net.qmat.qmhh.models.Models;
import net.qmat.qmhh.utils.Settings;
import pbox2d.PBox2D;
import codeanticode.glgraphics.GLConstants;

import processing.core.PApplet;

public class Main extends PApplet {

	private static final long serialVersionUID = 1L;
	public static Main p;
	public static PBox2D box2d;
	public int frameCount = 0; 

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
		
		/* 
		 * N.B. The controllers should be loaded at the end of setup(), otherwise
		 * the tuio events might trigger actions before everything is set up properly.
		 */
		Controllers.init(); 
		Models.init();
		Controllers.initTuio(); 
	}

	public void draw() {
		Controllers.update();

		Models.update();
		Models.draw();

		box2d.step();
		frameCount++;
		
		if(frameCount == 60) {
			// Add a hand for testing
			Controllers.getHandsController().addHand(1L, 0.0f, 0.0f); //740f, 325.0f);
			Controllers.getHandsController().addHand(2L, 1.0f, 1.0f); //740f, 325.0f);
			Controllers.getHandsController().removeHand(1L);
			Controllers.getHandsController().removeHand(2L);
			Controllers.getHandsController().addHand(3L, 0.15f, 0.85f); //740f, 325.0f);
			Controllers.getHandsController().addHand(4L, 0.9f, 0.1f); //740f, 325.0f);
			Controllers.getHandsController().addHand(5L, 0.9f, 0.9f); //740f, 325.0f);
			Controllers.getHandsController().addHand(6L, 0.1f, 0.1f); //740f, 325.0f);
		}
		
		smooth();
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
		 */
		
		Options options = new Options();
		options.addOption("t", true, "specify the table number");
		
		
		CommandLineParser parser = new PosixParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			Main.table = Integer.parseInt(cmd.getOptionValue("t"));
			if(Main.table < 1 || Main.table > 3)
				throw new Exception();
		} catch(ParseException e) {
			System.err.println("Could not parse command line options, exiting.");
			System.exit(1);
		} catch(Exception e) {
			System.err.println("Please provide a valid table number with the -t option (1, 2, or 3).");
			System.exit(1);
		}
		
		Settings.init();
		String location = "--location=0,0";
		PApplet.main(new String[] {location, "net.qmat.qmhh.Main" });
	}

	public void init(){
		if(frame!=null){
			frame.removeNotify();//make the frame not displayable
			frame.setResizable(false);
			frame.setUndecorated(true);
			frame.addNotify();
		}
		super.init();
	}

	public void mousePressed() {
		Models.getSporesModel().startRipple(10f);
	}
}