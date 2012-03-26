package net.qmat.qmhh.contacts;

import net.qmat.qmhh.models.trees.Branch;

import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

public class CreatureBaseTreeBranchContact extends ContactLogic {
	
	public void preSolveContact(Contact contact, Manifold manifold, Object objA, Object objB) {
		Branch branch = (Branch)objB;
		if(!branch.isActiveP())
			contact.setEnabled(false);
	}
}