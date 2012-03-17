package net.qmat.qmhh.contacts;

import net.qmat.qmhh.models.creatures.CreatureBase;
import net.qmat.qmhh.models.Hand.Beam;

import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

public class CreatureBaseBeamContact extends ContactLogic {
	
	public void beginContact(Contact contact, Object objA, Object objB) {
		CreatureBase creature = (CreatureBase)objA;
		if(creature.stage >= 2) {
			Beam beam = (Beam)objB;
			System.out.println("detected stage 2 creature in beam");
		}
	}
	
	public void endContact(Contact contact, Object objA, Object objB) {
		CreatureBase creature = (CreatureBase)objA;
		if(creature.stage >= 2) {
			Beam beam = (Beam)objB;
			System.out.println("stage 2 creature left beam");
		}
	}
}