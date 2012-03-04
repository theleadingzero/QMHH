/*
 * The HandsController takes care of checking whether the hands are 
 * out of bounds and should be removed or even instantiated in the first place.
 */

package net.qmat.qmhh;

public class HandsController {
	
	public HandsController() {}
	
	public void addHand(Long id, float x, float y) {
		// TODO: Check if it's not out of bounds.
		Models.getHandsModel().addHand(id, 
									   Main.relativeToPixelsX(x), 
									   Main.relativeToPixelsY(y));
	}
	
	public void updateHand(Long id, float x, float y)
	{
		// TODO: Check if it's not out of bounds.
		Models.getHandsModel().updateHand(id,
										  Main.relativeToPixelsX(x), 
										  Main.relativeToPixelsY(y));
	}
	
	public void removeHand(Long id) {
		// TODO: Think about what happens if a player's hand is removed.
		Models.getHandsModel().removeHand(id);
	}
	
	

}
