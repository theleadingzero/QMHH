package net.qmat.qmhh.contacts;

import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

public class BackgroundSporeContact extends ContactLogic {
	
	public void preSolveContact(Contact contact, Manifold manifold, Object objA, Object objB) {
		//Background background = (Background)objA;
		contact.setEnabled(false);
		//Spore spore = (Spore)objB;
		//Models.getSporesModel().removeSpore(spore);
	}
}