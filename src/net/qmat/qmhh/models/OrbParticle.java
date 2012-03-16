package net.qmat.qmhh.models;

import processing.core.PVector;

public class OrbParticle extends ProcessingObject {

	PVector loc;
	PVector vel;
	PVector acc;
	float ms;
	float counter;

	OrbParticle(PVector a, PVector v, PVector l, float ms_, float counter_) {
		acc = a;
		vel = v;
		loc = l;
		ms = ms_;
		counter = counter_;
	}

	public void update() {
		vel.add(acc);
		loc.add(vel);
		acc = new PVector();
	}

	public void draw() {
		update();
		p.strokeWeight(ms/30);
		p.stroke(120, 120, 245, 200);
		p.point(loc.x,loc.y);
	}

	void move(PVector target) {
		acc.add(steer(target));
	}   

	PVector getLocation() {
		return loc;
	}

	PVector steer(PVector target) {
		PVector steer;
		PVector desired = PVector.sub(target,loc);
		//float d = desired.mag();
		desired.normalize();
		desired.mult(3.5f);
		steer = PVector.sub(desired,vel);
		steer.limit(3.0f);
		steer.div(ms);
		return steer;
	}


}
