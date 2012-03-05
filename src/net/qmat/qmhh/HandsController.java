/*
 * The HandsController takes care of checking whether the hands are 
 * out of bounds and should be removed or even instantiated in the first place.
 */

package net.qmat.qmhh;

public class HandsController {
	
	// cache for speed
	private float ringInnerRadius, ringOuterRadius;
	
	public HandsController() {
		ringInnerRadius = Settings.getInteger(Settings.PR_RING_INNER_DIAMETER) / 2.0f;
		ringOuterRadius = Settings.getInteger(Settings.PR_RING_OUTER_DIAMETER) / 2.0f;
	}
	
	// N.B. takes relative positions [0.0 ... 1.0]
	public void addHand(Long id, float x, float y) {
		float pixelX = Main.relativeToPixelsX(x);
		float pixelY = Main.relativeToPixelsY(y);
		if(handInBoundsP(pixelX, pixelY)) {
			Models.getHandsModel().addHand(id, pixelX, pixelY);
		}
	}

	// N.B. takes relative positions [0.0 ... 1.0]
	public void updateHand(Long id, float x, float y)
	{
		float pixelX = Main.relativeToPixelsX(x);
		float pixelY = Main.relativeToPixelsY(y);
		if(handInBoundsP(pixelX, pixelY)) {
			Models.getHandsModel().updateHand(id, pixelX, pixelY);
		} else {
			Models.getHandsModel().removeHand(id);
		}
	}
	
	// N.B. takes relative positions [0.0 ... 1.0]
	public void removeHand(Long id) {
		// TODO: Think about what happens if a player's hand is removed.
		Models.getHandsModel().removeHand(id);
	}
	
	// N.B. takes actual pixel values, not relative ones (like the one from Tuio)
	private boolean handInBoundsP(float x, float y) {
		PPoint2 ppoint = Main.c2p(x, y);
		return (ppoint.r > ringInnerRadius && ppoint.r < ringOuterRadius);
	}
	

}
