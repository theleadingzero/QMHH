package net.qmat.qmhh;

import net.qmat.qmhh.contacts.*;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

public class ContactController implements ContactListener {
	


	@Override
	public void beginContact(Contact contact) {}

	@Override
	public void endContact(Contact contact) {}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}

	@Override
	public void preSolve(Contact contact, Manifold manifold) {
		Object objA = contact.getFixtureA().getBody().getUserData();
		Object objB = contact.getFixtureB().getBody().getUserData();
		if(objA == null || objB == null)
			return;
		if(contactPair(contact, manifold, objA, objB, Creature.class, Spore.class, CreatureSporeContact.class)) return;
		if(contactPair(contact, manifold, objA, objB, Background.class, Spore.class, BackgroundSporeContact.class)) return;
	}
	
	/*
	 * N.B. Some JAVA magic: this calls the right ContactLogic class with the 
	 * arguments in the right order.
	 */
	private boolean contactPair(Contact contact, 
							 Manifold manifold, 
							 Object objA, 
							 Object objB, 
							 Class<?> clA,
							 Class<?> clB, 
							 Class<? extends ContactLogic> clCL) {
		try {
			if(objA.getClass().equals(clA) && objB.getClass().equals(clB)) {
				ContactLogic cl = clCL.newInstance();
				cl.contact(contact, manifold, objA, objB);
				return true;
			} else if(objA.getClass().equals(clB) && objB.getClass().equals(clA)) {
				ContactLogic cl = clCL.newInstance();
				cl.contact(contact, manifold, objB, objA);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
