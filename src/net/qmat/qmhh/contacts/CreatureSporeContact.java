package net.qmat.qmhh.contacts;

import net.qmat.qmhh.Creature;
import net.qmat.qmhh.Models;
import net.qmat.qmhh.Spore;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

public class CreatureSporeContact extends ContactLogic {
	
	public void contact(Contact contact, Manifold manifold, Object objA, Object objB) {
		Creature creature = (Creature)objA;
		Spore spore = (Spore)objB;
		Models.getSporesModel().removeSpore(spore);
	}
}