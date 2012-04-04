package net.qmat.qmhh.models.creatures;

import java.util.Vector;

import net.qmat.qmhh.Main;

import org.jbox2d.common.Vec2;

public class Robot extends CreatureBase {

	int num;
	float pas;
	public Vector<Cercle> cercles;

	Robot() {
		super();
		num = (int)p.random(3, 6);
		pas = 200f/num;

		cercles = new Vector<Cercle>();
		//points = new Vector<Poin>();

		for(int a = 1; a < num; a ++)
		{
			cercles.add(new Cercle(a * pas, this));
		}
	}

	public void draw() {

		/*
		 * body is part of the CreatureBase class and we can use it to get
		 * the creature's position and angle.
		 */
		Vec2 position = box2d.getBodyPixelCoord(body);
		float angle = body.getAngle();

		/*
		 * w and h are also part of the CreatureBase class and they are
		 * the width and height of the creature. The field p is a reference to
		 * the Processing applet. This has all the draw functions. So if you'd
		 * want to use a Processing function, prepend it with 'p.'.  
		 */
		p.pushMatrix();
		p.translate(position.x, position.y);
		p.rotateZ(angle);

		for(int a = 0; a < cercles.size(); a ++)
		{
			cercles.get(a).avance();
		}
		p.popMatrix();
	}


	public class Cercle
	{
		int n; 
		float r, v, an, pas;
		public Vector<Poin> mespoints;
		float circRad = p.random(0f, 1f);

		Cercle(float _r, Robot cf)
		{
			mespoints = new Vector<Poin>();
			an = p.random(Main.TWO_PI);
			v = p.radians(p.random(-0.8f, 0.8f));
			r =_r;
			n = (int)(((Main.PI * _r * 2f)/60f) + 1f);
			pas = Main.TWO_PI/n;


			for(int a = 0; a < n; a ++)
			{
				mespoints.add(new Poin(an + a * pas, circRad, this, cf));
			}
		}

		void avance()
		{
			//		an += v;
			for(int a = 0; a < mespoints.size(); a ++)
			{
				mespoints.get(a).avance(v);
			}
		}
	}

	class Poin
	{
		float an, r;
		Cercle c;
		float x, y;
		Robot cf;

		Poin(float _an, float _r, Cercle _c, Robot _cf)
		{
			r =_r; an = _an; c = _c; cf = _cf;
		}

		void avance(float _v)
		{
			an += _v;

			x = Main.cos(an) * r * cf.w * 0.5f;
			y = Main.sin(an) * r * cf.h * 0.5f;

			for(int a = 0; a < cf.cercles.size(); a ++)
			{
				Cercle ci = cf.cercles.get(a);
				if(ci != c)
				{
					if((c.r - ci.r) < 4 * (stage + 1))
					{
						for(int b = 0; b < ci.mespoints.size(); b ++)
						{
							if(getDistance(x, y, ci.mespoints.get(b).x, ci.mespoints.get(b).y) < 4 * (stage + 1))
							{
								p.stroke(255, p.random(60, 250));
								p.strokeWeight(p.random(0, 2));
								p.line(x, y, ci.mespoints.get(b).x, ci.mespoints.get(b).y);
							}
						}
					}
				}
			}
		}
	}

	float getDistance(float _x1, float _y1, float _x2, float _y2)
	{
		return Main.sqrt(Main.pow((_x1 -_x2), 2) + Main.pow((_y1 - _y2), 2));
	}




}


