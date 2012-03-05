/*
 * The Models class is a singleton that allows the different controllers to find
 * the models. Otherwise we would have to pass references everywhere.
 */

package net.qmat.qmhh;

public class Models {

	private static Models instance = null;
	
	private HandsModel handsModel;
	
	protected Models() {
		handsModel = new HandsModel();
	}
	
    public static Models getInstance() {
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
    		instance = new Models();
    	}
    }
    
    /*
     * Getter methods for the models.
     */
    
    public static HandsModel getHandsModel() {
    	return instance.handsModel;
    }
    
    /* 
     * Draw!
     */
    
    public static void draw() {
    	// Call all the models' draw functions here.
    	Models models = Models.getInstance();
    	models.handsModel.draw();
    }
}
