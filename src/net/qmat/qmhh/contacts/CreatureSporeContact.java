package net.qmat.qmhh.contacts;

import net.qmat.qmhh.Creature;
import net.qmat.qmhh.Spore;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

public class CreatureSporeContact extends ContactLogic {
	
	//public CreatureSporeContact() {}
	
	public void contact(Contact contact, Manifold manifold, Object objA, Object objB) {
		Creature creature = (Creature)objA;
		Spore spore = (Spore)objB;
		System.out.println("Creature and Spore contact!");
	}
}