package cc.bebop.spraydigital;

import java.util.LinkedList;

import krister.Ess.AudioChannel;
import krister.Ess.Ess;
import processing.core.PApplet;
import processing.core.PImage;
import cc.bebop.processing.util.MediaMovel;
import cc.bebop.processing.util.ObjectUtil;
import cc.bebop.spraydigital.event.RadiusEvent;
import cc.bebop.spraydigital.event.RadiusListener;
import ddf.minim.AudioSnippet;
import ddf.minim.Minim;

public class Canvas extends UIComponent implements RadiusListener {

	//Audio
	private Minim minim;
	private AudioSnippet canshake;
	private AudioSnippet emptycan;
	private AudioChannel airspray;

	private Cursor cursorAtual;
	private float cursorX;
	private float cursorY;

	private int raio = Brush.RAIO_MIN;

	private float x;
	private float y;
	private float px;
	private float py;

	/*
	 * historico de modificações da imagem
	 * 
	 */
	private final int HIST_MAX = 16;

	private LinkedList<PImage> hist;

	public void hist_clear()
	{
		hist.clear();
	}

	public int hist_add()
	{
		if(hist.size() >= HIST_MAX)
		{
			hist.removeLast();
		}

		hist.push(pApplet.get());
		return hist.size();
	}

	public int hist_back()
	{
		if(hist.size() <= 1)
			return hist.size();

		hist.pop();
		pApplet.image(hist.getFirst(), 0, 0);
		return hist.size();
	}


	/*
	 * 
	 * 
	 */

	private MediaMovel mediaVelocidade = new MediaMovel(10);

	private Brush brush;

	public Canvas(PApplet pApplet) {
		super(pApplet);
		this.inicializarEfeitosSonoros();

		hist = new LinkedList<PImage>();
		hist_clear();
		hist_add();
	}

	public void setBrush(Brush brush) {
		this.brush = brush;
		this.brush.addRadiusChangeListener(this);
	}

	boolean cursorAtualizado = true;

	public void draw() {
		if(!isStateSaved()) {
			saveState();
		}

		//Só processa a luz se não estiver mudando de cor
		if(cursorAtual != null) {
			if(cursorAtualizado) {
				cursorAtualizado = false;
				x = cursorX;
				y = cursorY;
				if(px == 0 && py == 0) {
					px = x;
					py = y;
				}

				if(pApplet.frameCount > 2) {
					float diffX = x - px;
					float diffY = y - py;

					float fator = (raio <= 15) ? 0.8f : (raio <= 25) ? 1.1f : (raio <= 40) ? 1.2f : 1.4f;
					float qtdPontosX = PApplet.abs(diffX / (raio * fator));
					float qtdPontosY = PApplet.abs(diffY / (raio * fator));
					float qtdPontosTotal = PApplet.ceil(PApplet.max(qtdPontosX, qtdPontosY));
					if(qtdPontosTotal < 1) qtdPontosTotal = 1F;
					float incrementoX = diffX / qtdPontosTotal;
					float incrementoY = diffY / qtdPontosTotal;

					float velocidade = mediaVelocidade.media(PApplet.dist(x, y, x + incrementoX, y + incrementoY));

					//Preenche todos os pontos intermediários não capturados
					if(qtdPontosX > 1 || qtdPontosY > 1) {
						//TODO resolver "bug do canto superior esquerdo"
						if(x == 0 || y == 0) return;

						float nx = px;
						float ny = py;

						//Itere pelas bolas que faltam
						for(int i = 0; i < qtdPontosTotal; i++) {
							nx += incrementoX;
							ny += incrementoY;

							//Para cada bola, começando da primeira nossa
							//Nota: em caso de preenchimento de pontos não capturados, não sobrepõe as bolas.
							spray(nx, ny, velocidade);
						}
					}
					else {
						spray(x, y, velocidade);
					}

					//Só atualiza PX e PY quando tiver mudado
					px = x;
					py = y;
				}
			}
		}
		else {
			airspray.stop();
		}
	}

	void spray(float x, float y, float velocidade) {
		spray(x, y, velocidade, true);
	}

	void spray(float x, float y, float velocidade, boolean sobrepor) {
		brush.spray(x, y, velocidade, sobrepor);
	}

	void reset() {
		emptycan.play();
		emptycan.rewind();
		pApplet.delay(2000);
		canshake.play();
		canshake.rewind();
	}

	public boolean isOver(Cursor cursor) {
		return ObjectUtil.isOver(this.x, this.y, cursor.x, cursor.y, this.width, this.height);
	}

	//////////////////
	// Radius Event //
	//////////////////

	public void radiusChanged(RadiusEvent event) {
		this.raio = event.getRadius();
	}

	//////////////////
	// Cursor Event //
	//////////////////

	public void addCursor(Cursor cursor) {
		if(cursorAtual == null) {
			if(emptycan.isPlaying() || canshake.isPlaying()) {
				return;
			}

			if(cursorAtual == null) {
	//			revertState();

				cursorAtual = cursor;
				cursorAtualizado = true;
				cursorX = cursorAtual.getX();
				cursorY = cursorAtual.getY();
				px = 0;
				py = 0;

				loop(airspray);
			}
		}
	}

	public void updateCursor(Cursor cursor) {
		if(cursorAtual != null && cursorAtual.equals(cursor)) {
			cursorAtualizado = true;
			cursorX = cursorAtual.getX();
			cursorY = cursorAtual.getY();
		}
	}

	public void removeCursor(Cursor cursor) {
		if(cursorAtual != null && cursorAtual.equals(cursor)) {
			saveState();

			cursorAtual = null;
			cursorAtualizado = true;
			px = 0F;
			py = 0F;

			airspray.stop();

			hist_add();
		}
	}

	/////////////////////
	// Efeitos Sonoros //
	/////////////////////

	void inicializarEfeitosSonoros()
	{
		Ess.start(pApplet);

		minim = new Minim(pApplet);
		canshake = minim.loadSnippet("canshake.mp3");
		emptycan = minim.loadSnippet("emptycan.aiff");
		airspray = new AudioChannel("airspray.wav");

		canshake.play();
		canshake.rewind();
	}

	void loop(AudioChannel channel) {
		// randomly move the in and out loop points
		channel.in((int)(pApplet.random(channel.size/3)));
		channel.out(channel.size-1-(int)(pApplet.random(channel.size/3)));
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