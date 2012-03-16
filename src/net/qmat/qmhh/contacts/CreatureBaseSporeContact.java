package net.qmat.qmhh.contacts;

import net.qmat.qmhh.models.Models;
import net.qmat.qmhh.models.Spore;
import net.qmat.qmhh.models.creatures.CreatureBase;

import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

public class CreatureBaseSporeContact extends ContactLogic {
	
	public void contact(Contact contact, Manifold manifold, Object objA, Object objB) {
		CreatureBase creature = (CreatureBase)objA;
		creature.grow();
		Spore spore = (Spore)objB;
		Models.getSporesModel().removeSpore(spore);
	}
}