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
	
	protected Controllers() {
		handsController = new HandsController();
		sequencerController = new SequencerController();
		orbController = new OrbController();
		// N.B. Instantiate the TuioController last!
		tuioController = new TuioController();
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

}
