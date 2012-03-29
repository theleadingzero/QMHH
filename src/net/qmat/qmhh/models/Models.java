/*
 * The Models class is a singleton that allows the different controllers to find
 * the models. Otherwise we would have to pass references everywhere.
 */

package net.qmat.qmhh.models;

import net.qmat.qmhh.models.creatures.CreaturesModel;
import net.qmat.qmhh.models.trees.TreesModel;

public class Models {

	private static Models instance = null;
	
	private HandsModel handsModel;
	private OrbModel orbModel;
	private CreaturesModel creaturesModel;
	//private PlayheadModel playheadModel;
	private SporesModel sporesModel;
	private TreesModel treesModel;
	private FeedbackPointsModel feedbackPointsModel;
	
	protected Models() {
		handsModel = new HandsModel();
		orbModel = new OrbModel();
		creaturesModel = new CreaturesModel();
		//playheadModel = new PlayheadModel();
		sporesModel = new SporesModel();
		treesModel = new TreesModel();
		feedbackPointsModel = new FeedbackPointsModel();
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
    
    public static OrbModel getOrbModel() {
    	return instance.orbModel;
    }
    
    public static CreaturesModel getCreaturesModel() {
    	return instance.creaturesModel;
    }
    
//    public static PlayheadModel getPlayheadModel() {
//    	return instance.playheadModel;
//    }
    
    public static SporesModel getSporesModel() {
    	return instance.sporesModel;
    }
    
    public static TreesModel getTreesModel() {
    	return instance.treesModel;
    }
    
    public static FeedbackPointsModel getFeedbackPointsModel() {
    	return instance.feedbackPointsModel;
    }
    
    public static void draw() {
    	// Call all the models' draw functions here.
    	Models models = Models.getInstance();
    	models.handsModel.draw();
    	models.feedbackPointsModel.draw();
    	
    	models.treesModel.draw();
    	//models.playheadModel.draw();
    	models.creaturesModel.draw();
    	models.sporesModel.draw();
    	//models.orbModel.draw();
    }
    
    public static void update() {
    	Models models = Models.getInstance();
    	models.handsModel.update();
    	models.sporesModel.update();
    	models.creaturesModel.update();
    	//models.playheadModel.update();
    }
}
