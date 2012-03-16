package net.qmat.qmhh.utils;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class Settings {
	
	// what table are we?
	public static String TABLE = "TABLE";
	
	// size and fullscreen settings
	public static String PR_WIDTH = "PR_WIDTH";
	public static String PR_HEIGHT = "PR_HEIGHT";
	public static String PR_FULLSCREEN = "PR_FULLSCREEN";
	
	// where to center the ecosystem and outer ring size
	public static String PR_CENTER_X = "PR_CENTER_X";
	public static String PR_CENTER_Y = "PR_CENTER_Y";
	public static String PR_RING_INNER_RADIUS = "PR_RING_INNER_RADIUS";
	public static String PR_RING_OUTER_RADIUS = "PR_RING_OUTER_RADIUS";
	
	// settings for the orb
	public static String PR_ORB_MAX_RADIUS = "PR_ORB_MAX_RADIUS";
	public static String PR_ORB_MIN_RADIUS = "PR_ORB_MIN_RADIUS";
	
	// osc settings for sound
	public static String OSC_SEQUENCER_PORT = "OSC_SEQUENCER_PORT";
	public static String OSC_SEQUENCER_IP = "OSC_SEQUENCER_IP";
	
	// collision settings
	public static String PR_SPORE_COLLISION_GROUP = "PR_SPORE_COLLISION_GROUP";
	public static String PR_BRANCH_COLLISION_GROUP = "PR_BRANCH_COLLISION_GROUP";
	
	// growth settings
	public static String PR_MINIMAL_GROWTH_INTERVAL = "PR_MINIMAL_GROWTH_INTERVAL";
	
	// sequencer settings
	public static String PR_SEQUENCER_SECTIONS = "PR_SEQUENCER_SECTIONS"; 
	
	private static Settings instance = null;
	String propertyFile = "/data/preferences.properties";
	Properties properties = new Properties();
	
    public static Settings getInstance() {
       return instance;
    }
    
    public static void init() {
    	if(instance == null) {
            instance = new Settings();
        }
    }
	
	protected Settings() {
		try {
			properties.load(new FileReader(new File(System.getProperty("user.dir")+propertyFile)));
		} catch (Exception e) {
			System.err.println("Something went wrong while loading the preferences.");
			e.printStackTrace();
		}
	}
	
	public static String getString(String key) {
		return getInstance().properties.getProperty(key);
	}
	
	public static Boolean getBoolean(String key) {
		String setting = getString(key);
		return setting.equalsIgnoreCase("true") 
			   || setting.equalsIgnoreCase("1") 
			   || setting.equalsIgnoreCase("yes");
	}
	
	public static Integer getInteger(String key) {
		return Integer.parseInt(getString(key));
	}
	
	public static float getFloat(String key) {
		return Float.parseFloat(getString(key));
	}
	
}
