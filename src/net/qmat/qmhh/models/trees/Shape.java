
package net.qmat.qmhh.models.trees;
import net.qmat.qmhh.Main;
import net.qmat.qmhh.models.ProcessingObject;




public class Shape extends ProcessingObject{
	float indexOffset;
	float cycle = 11100.0f;
	float xb;
        float yb;
        float lengthb = 13.0f;
        int steps = 79;
        
	Shape(){
	indexOffset=p.random(1.0f);	
	}

public	void drawShape(){
                p.pushMatrix();
                p.translate(xb, yb);
                p.noStroke();
                p.fill(255,255,255,80);
                p.ellipse(0,0,lengthb,lengthb);
                p.fill(255,0,50,255);
                p.ellipse(0,0,lengthb/5,lengthb/5);
                p.fill(255,255,220,70);
                
                float length = lengthb/2;
		int now2 = p.millis();
                float index = (now2 % cycle) / cycle + indexOffset;
                float angle,o,a;
                p.curveTightness(9.0f);
                              //length = 60.0f;
                /* 
                //right half
                beginShape();             
                for(int i=0; i<steps+2; i++) {
		angle = (TWO_PI) / (steps+1) * i;
		if(i % 2 == 0 )
		length += 1.0 + sin((index+i*0.09f) * TWO_PI) * 20.0f;
		o = sin(angle) * length;
		a = cos(angle) * length;
		curveVertex(o, a);
                }
                endShape(CLOSE);
               */ 
      for(int m=0;m<4;m++){
               length=lengthb;
                
                p.rotate(Main.PI/2);
                p.beginShape();
                for(int i=steps+1; i>=0; i--) {
		angle = (Main.TWO_PI) / (steps+1) * i;
		if(i % 2 == 0 )
		length += 1.0 + p.sin((index+i*0.09f) * Main.TWO_PI) * 7.0f;
		o = p.sin(angle) * length;
		a = p.cos(angle) * length;
		p.curveVertex(o, a);
                } 
                p.endShape(Main.CLOSE);
      }
      p.fill(255,0,50,155);
      p.ellipse(0,0,lengthb/2,lengthb/2);
      p.popMatrix();
 	}
	
      }