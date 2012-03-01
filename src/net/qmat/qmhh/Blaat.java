package net.qmat.qmhh;

import processing.core.*;
//import processing.opengl.*;

public class Blaat extends PApplet {
	
  TuioController tuioController;

  public void setup() {
    size(640,480, P3D);    
    tuioController = new TuioController(this); 
    println(this.width);
    println(this.height);
  }

  public void draw() {
	background(0);
    stroke(255);
    tuioController.draw();
  }
  
}