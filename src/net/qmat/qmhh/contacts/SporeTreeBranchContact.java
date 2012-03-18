package net.qmat.qmhh.contacts;

import net.qmat.qmhh.models.Models;
import net.qmat.qmhh.models.Spore;
import net.qmat.qmhh.models.Tree.Branch;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

public class SporeTreeBranchContact extends ContactLogic {
	
	public void preSolveContact(Contact contact, Manifold manifold, Object objA, Object objB) {
		Spore spore = (Spore)objA;
		Branch branch = (Branch)objB;
		// if the branch is active, it can react to spores
		// TODO: add timing
		if(branch.isActiveP() && !branch.isStillGrowingP()) {
			if(!branch.branchesCompleteP()) {
				// grow the plant
				branch.sprout();
			} 
			Models.getSporesModel().removeSpore(spore);
		} else {
			contact.setEnabled(false);
		}
	}
}