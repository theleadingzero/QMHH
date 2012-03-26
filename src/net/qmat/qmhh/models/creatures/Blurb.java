package net.qmat.qmhh.models.creatures;
import net.qmat.qmhh.Main;

import org.jbox2d.common.Vec2;

public class Blurb  extends CreatureBase {
	
	
	Blurb(){
		super();	
		
	}
	
	public void draw(){
		p.beginShape();

		int steps = 11;

		int now2 = p.millis();

		float index = 0;//(now2 % cycle) / cycle + indexOffset;

		for(int i=0; i<steps+1; i++) {

		float angle = (Main.TWO_PI) / steps * i;

		float length = 60.0f;

		if(i % 2 == 0)

		length += 1.0 + Main.sin((index+i*0.09f) * Main.TWO_PI) * 20.0f;

		float o = Main.sin(angle) * length;

		float a = Main.cos(angle) * length;

		//curveVertex(o, a);

		p.curveVertex(o, a);


		p.endShape(Main.CLOSE);
	}

}
	
}
