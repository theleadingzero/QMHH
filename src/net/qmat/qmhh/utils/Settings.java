package net.qmat.qmhh.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Properties;

import net.qmat.qmhh.Main;

public class Settings {
	
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
	public static String OSC_LOCAL_PORT = "OSC_LOCAL_PORT";
	public static String OSC_SEQUENCER_REMOTE_IP = "OSC_SEQUENCER_REMOTE_IP";
	public static String OSC_SEQUENCER_REMOTE_PORT = "OSC_SEQUENCER_REMOTE_PORT";
	public static String OSC_SOUND_REMOTE_IP = "OSC_SOUND_REMOTE_IP";
	public static String OSC_SOUND_REMOTE_PORT = "OSC_SOUND_REMOTE_PORT";
	
	// collision settings
	public static String PR_SPORE_COLLISION_GROUP = "PR_SPORE_COLLISION_GROUP";
	public static String PR_BRANCH_COLLISION_GROUP = "PR_BRANCH_COLLISION_GROUP";
	
	// growth settings
	public static String PR_MINIMAL_GROWTH_INTERVAL = "PR_MINIMAL_GROWTH_INTERVAL";
	
	// sequencer settings
	public static String PR_SEQUENCER_SECTIONS = "PR_SEQUENCER_SECTIONS";
	
	// creature settings
	public static String PR_CREATURE_CLASS = "PR_CREATURE_CLASS";
	
	// hand settings
	public static String PR_HAND_CHARGE_TIME = "PR_HAND_CHARGE_TIME";

	// background settings
	public static String PR_BACKDROP_REVEALED = "PR_BACKDROP_REVEALED";
	public static String PR_BACKDROP_UNREVEALED = "PR_BACKDROP_UNREVEALED";
	
	// what class draws the branches
	public static String PR_BRANCH_DRAWER = "PR_BRANCH_DRAWER";
	
	// spore colors
	public static String PR_SPORE_COLOR_1 = "PR_SPORE_COLOR_1";
	public static String PR_SPORE_COLOR_2 = "PR_SPORE_COLOR_2";
	
	private static Settings instance = null;
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
			properties.load(new BufferedReader(new InputStreamReader(this.getClass().getResource("/table"+Main.table+"_preferences.properties").openStream())));
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
	
	public static float[] getFloatArray(String key) {
		String array = getString(key);
		String split[] = array.split(",");
		float result[] = new float[split.length];
		for(int i=0; i<split.length; i++)
			result[i] = Float.parseFloat(split[i]);
		return result;
	}
	
}
