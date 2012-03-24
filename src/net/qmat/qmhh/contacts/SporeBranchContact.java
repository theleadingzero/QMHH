package net.qmat.qmhh.contacts;

import net.qmat.qmhh.models.Models;
import net.qmat.qmhh.models.Spore;
import net.qmat.qmhh.models.trees.Branch;

import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

public class SporeBranchContact extends ContactLogic {
	
	public void preSolveContact(Contact contact, Manifold manifold, Object objA, Object objB) {
		Spore spore = (Spore)objA;
		Branch branch = (Branch)objB;
		// if the branch is active, it can react to spores
		// TODO: add timing
		if(branch.isActiveP()) {
			if(!branch.branchesCompleteP()
			   && !branch.isStillGrowingP()
			   && !branch.isStillSproutingP()) {
				branch.sprout();
			} 
			spore.markForRemoval();
		} else {
			contact.setEnabled(false);
		}
	}
}