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
	
	private double x1, x2;
	private double y1, y2;
	private double r1, r2;
	
	private long t1, t2;
	
	private int Q = 30000;

	private double K = Math.tan(Math.PI / 4) / 6;

	private boolean spraying = false;

	private Brush brush = new Brush(papp);
	
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
	
	/////////////////
	// Constructor //
	/////////////////

	public Canvas(PApplet pApplet, int Q)
	{
		super(pApplet);
		this.inicializarEfeitosSonoros();
		
		this.Q = Q;
		// TODO: this.K = K;
		
		putXY(0, 0);
		putZ(0);
		shiftXYZ();
		
		hist = new LinkedList<PImage>();
		histClear();
		histAdd();
	}

	public void draw()
	{
		t2 = papp.millis();
		
		if (spraying) {
			// sound on!
			loop(airspray);
			
			//System.err.printf("{%f, %f, %f} {%f, %f, %f} {%d}\n", x1, y1, r1, x2, y2, r2, (int) ((Q * (t2 - t1)) / 1000));
			
			brush.sprayLine(x1, y1, r1, x2, y2, r2,
					(int) ((Q * (t2 - t1)) / 1000));

			shiftXYZ();
		}
		
		else {
			// sound off!
			airspray.stop();
		}
		
		t1 = t2;
	}

	void reset()
	{
		emptycan.play();
		emptycan.rewind();
		papp.delay(2000);
		canshake.play();
		canshake.rewind();
	}
	
	////////////////////
	// point handling //
	////////////////////
	
	private void shiftXYZ()
	{
		x1 = x2;
		y1 = y2;
		r1 = r2;
	}
	
	private void putXY(float x, float y)
	{
		x2 = x;
		y2 = y;
	}
	
	private void putZ(float z)
	{
		r2 = z * K;
		r2 = Math.max(15, Math.min(r2, 200));
	}

	//////////////////
	// Cursor Event //
	//////////////////

	public void addCursor(Cursor cursor)
	{
		//if(emptycan.isPlaying() || canshake.isPlaying())
		//		return;
		
		putXY(cursor.getX(), cursor.getY());
		shiftXYZ();
		
		spraying = true;

	}

	public void updateCursor(Cursor cursor)
	{
		putXY(cursor.getX(), cursor.getY());
	}

	public void removeCursor(Cursor cursor)
	{
		spraying = false;
		
		histAdd();
	}
	
	//////////////////
	// Spray Events //
	//////////////////

	public void distanceChanged(DistanceEvent event)
	{
		putZ(event.getDistance());
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