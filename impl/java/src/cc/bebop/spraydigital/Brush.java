package cc.bebop.spraydigital;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import cc.bebop.spraydigital.event.ColorEvent;
import cc.bebop.spraydigital.event.ColorListener;

public class Brush implements ColorListener {

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
		PGraphics g;
		
		int i;

		double w = x2 - x1;
		double h = y2 - y1;
			
		double len = Math.hypot(w, h);
		
		int n = len == 0 ? 1 : (int) Math.ceil((len/(Math.min(r1, r2) * .1)));
		
		double dx = w / n;
		double dy = h / n;
		double dr = (r2 - r1) / n;
		
		/* FIXME: use cursor time to control discharge */
		int Qi = Q / n;

		g = papp.g;
		g.beginShape(PConstants.POINT);

		for (i = 0; i < n; i++)
			spray(x1 + i * dx, y1 + i * dy, r1 + dr * i, Qi);

		g.endShape();
	}
	
	/*
	 * magic: do not touch.
	 * 
	 */
	public double magic(double x)
	{
		return Math.sqrt((Math.exp(x) - 1)/(Math.E-1));
	}
	
	/*
	 * spray point
	 * 
	 */
	private void spray(double x, double y, double r, int Q)
	{
		int i;
		
		PGraphics g;
		g = papp.g;

		for (i = 0; i < Q; i++) {
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
	
	////////////
	// Events //
	////////////

	public void colorChanged(ColorEvent event) {
		this.cor = event.getColor();
	}

}