package net.qmat.qmhh.contacts;

import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

public class ContactLogic {
	
	public ContactLogic() {}
	public void preSolveContact(Contact contact, Manifold manifold, Object objA, Object objB) {}
	public void beginContact(Contact contact, Object objA, Object objB) {}
	public void endContact(Contact contact, Object objA, Object objB) {}

}
