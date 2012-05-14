package cc.bebop.spraydigital;

import java.util.LinkedList;

import krister.Ess.AudioChannel;
import krister.Ess.Ess;
import processing.core.PApplet;
import processing.core.PImage;
import cc.bebop.spraydigital.event.DistanceEvent;
import cc.bebop.spraydigital.event.DistanceListener;
import ddf.minim.AudioSnippet;
import ddf.minim.Minim;

public class Canvas extends UIComponent implements DistanceListener {

	//Audio
	private Minim minim;
	private AudioSnippet canshake;
	private AudioSnippet emptycan;
	private AudioChannel airspray;

	private Cursor cursorAtual;
	
	private double
			x1, x2,
			y1, y2,
			z1, z2,
			r1, r2
			;
	
	private long
			t1, t2
			;
	
	private int Q = 30000;

	private double K = Math.tan(Math.PI / 4) / 6;
	
	/*
	 * historico de modificações da imagem
	 * 
	 */
	private final int HIST_MAX = 16;

	private LinkedList<PImage> hist;

	public void histClear()
	{
		hist.clear();
	}

	public int histAdd()
	{
		if(hist.size() >= HIST_MAX)
			hist.removeLast();

		hist.push(papp.get());
		return hist.size();
	}

	public int histBack()
	{
		if(hist.size() <= 1)
			return hist.size();

		hist.pop();
		papp.image(hist.getFirst(), 0, 0);
		return hist.size();
	}

	private Brush brush;
	
	/////////////////
	// Constructor //
	/////////////////

	public Canvas(PApplet pApplet, int Q)
	{
		super(pApplet);
		this.inicializarEfeitosSonoros();
		
		this.Q = Q;
		// TODO: this.K = K;
		
		z2 = 100;
		r2 = z2 * K;
		
		z1 = z2;
		z1 = z1 + 0;
		r1 = r2;
		
		hist = new LinkedList<PImage>();
		histClear();
		histAdd();
	}

	public void setBrush(Brush brush) 
	{
		this.brush = brush;
	}

	boolean cursorAtualizado = true;
	
	private static final int cursorTimeout = 50;

	public void draw()
	{
//		long T1, T2;
//		
//		T1 = System.currentTimeMillis();
		
		if(cursorAtual == null)
		{
			if(cursorAtualizado)
			{
				airspray.stop();
				
				cursorAtualizado = false;
			}
			
			return;
		}
		
		if((!cursorAtualizado) && ((papp.millis() - t1) < cursorTimeout))
			return;
		
		cursorAtualizado = false;
		
		t2 = papp.millis();
				
		//System.err.printf("sprayLine(\n%f, %f, %f, \n%f, %f, %f, \n%f)\n", x1, y1, r1, x2, y2, r2, Q);
		
		brush.sprayLine(
				x1, y1, r1,
				x2, y2, r2,
				(int) ((Q * (t2 - t1)) / 1000)
				);

		x1 = x2;
		y1 = y2;
		z1 = z2;
		r1 = r2;
		t1 = t2;
		
//		T2 = System.currentTimeMillis();
//		
//		System.err.println("TIEM " + (T2 - T1));
	}

	void reset()
	{
		emptycan.play();
		emptycan.rewind();
		papp.delay(2000);
		canshake.play();
		canshake.rewind();
	}

	//////////////////
	// Cursor Event //
	//////////////////

	public void addCursor(Cursor cursor)
	{
		// FIXME: sanity check!
		//assert (cursor != null);
		
		if(cursorAtual != null)
			return;

		if(emptycan.isPlaying() || canshake.isPlaying())
				return;
		
		//System.err.printf("add cursor: (%f, %f)\n", cursor.getX(), cursor.getY());
			
		cursorAtual = cursor;
				
		x2 = cursorAtual.getX();
		y2 = cursorAtual.getY();
		t2 = papp.millis();

		x1 = x2;
		y1 = y2;
		t1 = t2;

		loop(airspray);
		//airspray.loop();
		
		cursorAtualizado = true;
		
		//System.err.println("add:\t" + x2 + ",\t" + y2);
	}

	public void updateCursor(Cursor cursor)
	{
		// FIXME: this is happening. Check.
		//assert (cursor != null);
		
		if(cursorAtual == null)
			return;
		
		if(!cursorAtual.equals(cursor))
			return;
		
		//System.err.printf("update cursor: (%f, %f)\n", cursor.getX(), cursor.getY());
		
		x2 = cursorAtual.getX();
		y2 = cursorAtual.getY();
		t2 = papp.millis();
		
		cursorAtualizado = true;
		
		//System.err.println("up:\t" + x2 + ",\t" + y2);
	}

	public void removeCursor(Cursor cursor)
	{
		if(cursorAtual == null)
			return;
		
		if(!cursorAtual.equals(cursor))
			return;
		
		//System.err.printf("remove cursor: \n");
		
		airspray.stop();
		
		x2 = cursorAtual.getX();
		y2 = cursorAtual.getY();
		t2 = papp.millis();
		
		cursorAtual = null;
		
		histAdd();
		
		cursorAtualizado = true;
		
		//System.err.println("remove:\t" + x2 + ",\t" + y2);
	}
	
	//////////////////
	// Spray Events //
	//////////////////

	public void distanceChanged(DistanceEvent event)
	{
		z2 = event.getDistance();
		r2 = z2 * K;
	}

	/////////////////////
	// Efeitos Sonoros //
	/////////////////////

	void inicializarEfeitosSonoros()
	{
		Ess.start(papp);

		minim = new Minim(papp);
		canshake = minim.loadSnippet("canshake.mp3");
		emptycan = minim.loadSnippet("emptycan.aiff");
		airspray = new AudioChannel("airspray.wav");

		canshake.play();
		canshake.rewind();
	}

	void loop(AudioChannel channel) {
		// randomly move the in and out loop points
		channel.in((int)(papp.random(channel.size/3)));
		channel.out(channel.size-1-(int)(papp.random(channel.size/3)));
		// snap the in loop point to the nearest zero crossing
		channel.snapInToZero();
		// snap the out loop point to the nearest zero crossing
		channel.snapOutToZero();
		// cue to our in loop point
		channel.cue(channel.in);
		// start the sound looping forever
		channel.play(Ess.FOREVER);
	}

	////////////////
	// Processing //
	////////////////

	public void stop() {
		Ess.stop();
		emptycan.close();
		canshake.close();
		minim.stop();
	}
}