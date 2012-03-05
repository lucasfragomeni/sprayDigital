package cc.bebop.spraydigital;

//import java.util.ArrayList;
//import java.util.List;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import cc.bebop.spraydigital.event.ColorEvent;
import cc.bebop.spraydigital.event.ColorListener;



public class Brush implements ColorListener {

	public static final int RAIO_MIN = 4;//12;
	public static final int RAIO_MAX = 90;

	private PApplet papp;

	private int cor = 0;

	public Brush(PApplet pApplet) {
		this.papp = pApplet;
	}
	


	/**
	 * Spray a line
	 * @param x1 - start x
	 * @param y1 - start y
	 * @param r1 - start radius
	 * @param x2 - end x
	 * @param y2 - end y
	 * @param r2 - end radius
	 * @param Q - discharge (points)
	 */
	public void sprayLine(double x1, double y1, double r1, double x2, double y2, double r2, int Q)
	{
		//long t1, t2;
		PGraphics g;
		
		//t1 = papp.millis();
		
		int i;

		double w = x2 - x1;
		double h = y2 - y1;
			
		double len = Math.hypot(w, h);
		
		int n = len == 0 ? 1 : (int) Math.ceil((len/(Math.min(r1, r2) * .1)));
		
		//System.err.println("len, n = "+ len + ", " + "n");
			
		double dx = w / n;
		double dy = h / n;
		double dr = (r2 - r1) / n;
		
		/* FIXME: use cursor time to control discharge */
		int Qi = Q / n;
		
		if (NEWSPRAY)
		{
			g = papp.g;
			g.beginShape(PConstants.POINT);

			for (i = 0; i < n; i++)
				spray(x1 + i * dx, y1 + i * dy, r1 + dr * i, Qi);

			g.endShape();
		}
		
		else
		{
			for (i = 0; i < n; i++)
				spray(x1 + i * dx, y1 + i * dy, r1 + dr * i, Qi);
		}
		
		//t2 = papp.millis();
		//
		//System.err.println("time: " + (t2 - t1));
	}
	
	public static final boolean EXPMAGIC = true;
	public static final boolean NEWMAGIC = true;
	
	/*
	 * magic: do not touch.
	 * 
	 */
	public double magic(double x)
	{
		if (EXPMAGIC)
		{
			// NOTE: This is teh best magic so far. Seriously.
			return Math.sqrt((Math.exp(x) - 1)/(Math.E-1));
		}
		
		else if (NEWMAGIC)
		{
		final double
			x0 = 0,		y0 = 0,
			x1 = .2,	y1 = .2 * .8,
			x2 = .4,	y2 = .4 * .8,
			x3 = 1,		y3 = 1
			;
		
		return Math.sqrt(
			(y0 * (x - x1) *  (x - x2) * (x - x3))/((x0 - x1) *  (x0 - x2) * (x0 - x3)) +
			(y1 * (x - x0) *  (x - x2) * (x - x3))/((x1 - x0) *  (x1 - x2) * (x1 - x3)) +
			(y2 * (x - x1) *  (x - x0) * (x - x3))/((x2 - x1) *  (x2 - x0) * (x2 - x3)) +
			(y3 * (x - x1) *  (x - x2) * (x - x0))/((x3 - x1) *  (x3 - x2) * (x3 - x0)))
			;
		}
		
		else
		{
			final double
				x0 = 0,		y0 = 0,
				x1 = .05,	y1 = .1,
				x2 = .9,	y2 = .8,			
				x3 = 1,		y3 = 1
				;
			
			return 
				(y0 * (x - x1) *  (x - x2) * (x - x3))/((x0 - x1) *  (x0 - x2) * (x0 - x3)) +
				(y1 * (x - x0) *  (x - x2) * (x - x3))/((x1 - x0) *  (x1 - x2) * (x1 - x3)) +
				(y2 * (x - x1) *  (x - x0) * (x - x3))/((x2 - x1) *  (x2 - x0) * (x2 - x3)) +
				(y3 * (x - x1) *  (x - x2) * (x - x0))/((x3 - x1) *  (x3 - x2) * (x3 - x0))
				;
		}
	}
	
	private static final boolean NEWSPRAY = true;
	
	/*
	 * spray point
	 * 
	 */
	private void spray(double x, double y, double r, int Q)
	{
		int i;
		
		if (NEWSPRAY)
		{
			PGraphics g;
			g = papp.g;
			
			for (i = 0; i < Q; i++)
			{
				int alpha = (int) (Math.random() * 10 + 10);
				float angle, rnd, px, py;

				angle = (float) (Math.random() * 2 * Math.PI);
				rnd = (float) (magic(Math.random()) * r);

				px = (float) (Math.cos(angle) * rnd);
				py = (float) (Math.sin(angle) * rnd);

				papp.stroke(cor, alpha);
				papp.strokeWeight(2);
				g.vertex((float) x + px, (float) y + py);
			}
		}
		
		else
		{
			for (i = 0; i < Q; i++)
			{
				int alpha = (int) (Math.random() * 100 - 50);
				float angle, rnd, px, py;

				angle = (float) (Math.random() * 2 * Math.PI);
				rnd = (float) (magic(Math.random()) * r);

				px = (float) (Math.cos(angle) * rnd);
				py = (float) (Math.sin(angle) * rnd);

				papp.stroke(cor, alpha);
				papp.fill(cor, alpha);
				papp.strokeWeight(1);

				papp.ellipse((float) x + px, (float) y + py, 1.5f, 1.5f);
			}
		}
	}
	
	////////////
	// Events //
	////////////

	public void colorChanged(ColorEvent event) {
		this.cor = event.getColor();
	}

}