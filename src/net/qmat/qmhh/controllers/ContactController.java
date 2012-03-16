package net.qmat.qmhh.controllers;

import net.qmat.qmhh.contacts.*;
import net.qmat.qmhh.models.Background;
import net.qmat.qmhh.models.Spore;
import net.qmat.qmhh.models.creatures.CreatureBase;

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
		if(contactPair(contact, manifold, objA, objB, CreatureBase.class, Spore.class, CreatureBaseSporeContact.class)) return;
		if(contactPair(contact, manifold, objA, objB, Background.class, Spore.class, BackgroundSporeContact.class)) return;
	}
	
	/*
	 * N.B. Some magic: this calls the right ContactLogic class with the 
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
			if(clA.isAssignableFrom(objA.getClass()) && clB.isAssignableFrom(objB.getClass())) {
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
