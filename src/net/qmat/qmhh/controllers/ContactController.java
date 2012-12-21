package net.qmat.qmhh.controllers;

import net.qmat.qmhh.contacts.BackgroundSporeContact;
import net.qmat.qmhh.contacts.ContactLogic;
import net.qmat.qmhh.contacts.CreatureBaseBeamContact;
import net.qmat.qmhh.contacts.CreatureBaseSporeContact;
import net.qmat.qmhh.contacts.CreatureBaseTreeBranchContact;
import net.qmat.qmhh.contacts.SporeBranchContact;
import net.qmat.qmhh.models.Background;
import net.qmat.qmhh.models.Hand.Beam;
import net.qmat.qmhh.models.Spore;
import net.qmat.qmhh.models.creatures.CreatureBase;
import net.qmat.qmhh.models.trees.Branch;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

public class ContactController implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Object objA = contact.getFixtureA().getBody().getUserData();
		Object objB = contact.getFixtureB().getBody().getUserData();
		if(objA == null || objB == null)
			return;
		if(beginContactPair(contact, objA, objB, CreatureBase.class, Beam.class, CreatureBaseBeamContact.class)) return;

	}

	@Override
	public void endContact(Contact contact) {
		Object objA = contact.getFixtureA().getBody().getUserData();
		Object objB = contact.getFixtureB().getBody().getUserData();
		if(objA == null || objB == null)
			return;
		if(endContactPair(contact,objA, objB, CreatureBase.class, Beam.class, CreatureBaseBeamContact.class)) return;

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {}

	@Override
	public void preSolve(Contact contact, Manifold manifold) {
		Object objA = contact.getFixtureA().getBody().getUserData();
		Object objB = contact.getFixtureB().getBody().getUserData();
		if(objA == null || objB == null)
			return;
		if(preSolvePair(contact, manifold, objA, objB, CreatureBase.class, Spore.class, CreatureBaseSporeContact.class)) return;
		if(preSolvePair(contact, manifold, objA, objB, Background.class, Spore.class, BackgroundSporeContact.class)) return;
		if(preSolvePair(contact, manifold, objA, objB, Spore.class, Branch.class, SporeBranchContact.class)) return;
		if(preSolvePair(contact, manifold, objA, objB, CreatureBase.class, Branch.class, CreatureBaseTreeBranchContact.class)) return;
	}

	/*
	 * N.B. Some JAVA magic: this calls the right ContactLogic class with the 
	 * arguments in the right order.
	 */
	private boolean preSolvePair(Contact contact, 
			Manifold manifold, 
			Object objA, 
			Object objB, 
			Class<?> clA,
			Class<?> clB, 
			Class<? extends ContactLogic> clCL) {
		try {
			ContactLogic cl = clCL.newInstance();
			if(compare(objA, objB, clA, clB)) {
				cl.preSolveContact(contact, manifold, objA, objB);
				return true;
			}
			else if(compare(objA, objB, clB, clA)) {
				cl.preSolveContact(contact, manifold, objB, objA);
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean beginContactPair(Contact contact, 
			Object objA, 
			Object objB, 
			Class<?> clA,
			Class<?> clB, 
			Class<? extends ContactLogic> clCL) {
		try {
			ContactLogic cl = clCL.newInstance();
			if(compare(objA, objB, clA, clB)) {
				cl.beginContact(contact, objA, objB);
				return true;
			}
			else if(compare(objA, objB, clB, clA)) {
				cl.beginContact(contact, objB, objA);
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean endContactPair(Contact contact, 
								   Object objA, 
								   Object objB, 
								   Class<?> clA,
								   Class<?> clB, 
								   Class<? extends ContactLogic> clCL) {
		try {
			ContactLogic cl = clCL.newInstance();
			if(compare(objA, objB, clA, clB)) {
				cl.endContact(contact, objA, objB);
				return true;
			}
			else if(compare(objA, objB, clB, clA)) {
				cl.endContact(contact, objB, objA);
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean compare(Object objA, Object objB, Class<?> clA, Class<?> clB) {
		return clA.isAssignableFrom(objA.getClass()) && clB.isAssignableFrom(objB.getClass());
	}

}
