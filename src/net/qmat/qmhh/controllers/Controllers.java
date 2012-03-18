/*
 * The Controllers class is a singleton that allows the right controllers to be
 * found throughout the application.
 */

package net.qmat.qmhh.controllers;

public class Controllers {

	private static Controllers instance = null;
	
	private HandsController handsController;
	private TuioController tuioController;
	private SequencerController sequencerController;
	private OrbController orbController;
	private SoundController soundController;
	private OscController oscController;
	
	protected Controllers() {
		handsController = new HandsController();
		sequencerController = new SequencerController();
		orbController = new OrbController();
		soundController = new SoundController();
		oscController = new OscController();
		oscController.start();
	}
	
	public static void update() {
		Controllers controllers = getInstance();
		controllers.orbController.update();
	}
	
    public static Controllers getInstance() {
        /* N.B. I'm not doing any checking here because it'll force everyone 
         * to deal with Exception stuff throughout Eclipse. Just be sure to 
         * call init() in Main's setup().
	    if(instance == null) {
            throw new Exception("The Models singleton hasn't been initialized yet.");
        }
        */
    	return instance;
    }
    
    public static void init() {
    	if(instance == null) {
    		instance = new Controllers();
    	}
    }
    
    public static void initTuio() {
    	// N.B. Instantiate the TuioController last!
    	instance.tuioController = new TuioController();
    }
    
    /*
     * Getter methods for the controllers.
     */
    
	public static HandsController getHandsController() {
		return getInstance().handsController;
	}

	public static TuioController getTuioController() {
		return getInstance().tuioController;
	}

	public static SequencerController getSequencerController() {
		return getInstance().sequencerController;
	}
	
	public static OrbController getOrbController() {
		return getInstance().orbController;
	}
	
	public static SoundController getSoundController() {
		return getInstance().soundController;
	}
	
	public static OscController getOscController() {
		return getInstance().oscController;
	}

}
