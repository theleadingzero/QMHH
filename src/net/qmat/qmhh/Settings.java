package net.qmat.qmhh;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class Settings {
	
	// size and fullscreen settings
	public static String PR_WIDTH = "PR_WIDTH";
	public static String PR_HEIGHT = "PR_HEIGHT";
	public static String PR_FULLSCREEN = "PR_FULLSCREEN";
	
	// where to center the ecosystem and outer ring size
	public static String PR_CENTER_X = "PR_CENTER_X";
	public static String PR_CENTER_Y = "PR_CENTER_Y";
	public static String PR_RING_INNER_DIAMETER = "PR_RING_INNER_DIAMETER";
	public static String PR_RING_OUTER_DIAMETER = "PR_RING_OUTER_DIAMETER";
	
	private static Settings instance = null;
	String propertyFile = "preferences.properties";
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
			properties.load(new FileReader(new File(propertyFile)));
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
	
}
