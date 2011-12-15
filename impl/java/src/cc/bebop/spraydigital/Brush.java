package cc.bebop.spraydigital;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;
import cc.bebop.processing.util.ObjectUtil;
import cc.bebop.spraydigital.event.ColorEvent;
import cc.bebop.spraydigital.event.ColorListener;
import cc.bebop.spraydigital.event.DistanceEvent;
import cc.bebop.spraydigital.event.DistanceListener;
import cc.bebop.spraydigital.event.RadiusEvent;
import cc.bebop.spraydigital.event.RadiusListener;

public class Brush implements ColorListener, DistanceListener {

	public static final int RAIO_MIN = 4;//12;
	public static final int RAIO_MAX = 90;

	private static final int PADROES = 50;
	private static final int DENSIDADE_MAX = 3500;

	private PApplet pApplet;

	private int cor = 0;
	private int densidade = 100;
	private int raio = RAIO_MIN;
	private int raioSugerido = RAIO_MIN;

	private int ultimoRaio = 0;
	private PVector ultimoPonto = new PVector(0, 0);

	private float opac = 10F;
	private float realOpac;
	private float moveX[] = new float[DENSIDADE_MAX];
	private float moveY[] = new float[DENSIDADE_MAX];
	private float opacChange[] = new float[DENSIDADE_MAX];

	private BibliotecaPadroes padroes;

	private List<RadiusListener> radiusChangeListeners = new ArrayList<RadiusListener>();

	public Brush(PApplet pApplet) {
		this.pApplet = pApplet;
		this.padroes = new BibliotecaPadroes(this.pApplet, RAIO_MIN, RAIO_MAX, PADROES, DENSIDADE_MAX);

		randomizarPadrao();
	}

	/**
	 * Pinta um jato de spray no ponto indicado.
	 * @param x
	 * @param y
	 * @param velocidade - distância entre esse ponto e o ponto anterior
	 * @param sobrepor
	 * @param indiceCor
	 */
	public void spray(float x, float y, float velocidade, boolean sobrepor) {
		//Calcula a densidade final inversamente proporcional a velocidade
		//(distância entre os pontos no ciclo) 
		int percentualDensidade = (int)((100 - velocidade));
		int pontas = Math.min((int)(densidade * percentualDensidade) / 100, DENSIDADE_MAX - 1);

		ajustarRaio();
		randomizarPadrao();

		if(ultimoRaio == 0) {
			ultimoRaio = raio;
		}

		for(int i = 0; i < pontas; i++) {
			if(sobrepor || !ObjectUtil.isOver(x + moveX[i], y + moveY[i], ultimoPonto.x, ultimoPonto.y, ultimoRaio)) {
				realOpac = opac + opacChange[i];
				pApplet.stroke(cor, realOpac);
				pApplet.fill(cor, realOpac);
				pApplet.strokeWeight(1);
				pApplet.ellipse(x + moveX[i], y + moveY[i], 1.5f, 1.5f);
			}
		}

		ultimoPonto = new PVector(x, y);
		ultimoRaio = raio;
	}

	private void randomizarPadrao() {
		padroes.randomizarPadrao(raio);
		moveX = padroes.getMoveX();
		moveY = padroes.getMoveY();
	}

	////////////
	// Events //
	////////////

	public void colorChanged(ColorEvent event) {
		this.cor = event.getColor();
	}

	public void distanceChanged(DistanceEvent event) {
		//Ajusta a sensibiliade do raio mínimo.
		raioSugerido = (int)PApplet.map(event.getDistance(), 1, RAIO_MAX, RAIO_MIN, RAIO_MAX-1);
		//PApplet.println("raio: " + raioSugerido);
	}

	//////////
	// Raio //
	//////////

	/**
	 * Evita que o raio mude bruscamente, adequando-o aos poucos ao raio sugerido pelo sensor.
	 */
	void ajustarRaio() {
		if(raio == 0) {
			raio = raioSugerido;
		} 
		else if(PApplet.abs(raio - raioSugerido) >= 5) {
			raio += (raio > raioSugerido) ? -2 : 2;
		} 
		else {
			raio = raioSugerido;
		}

		if(raio >= RAIO_MAX) raio = RAIO_MAX - 1;
		
		raio = 20;

		//Calcula a densidade proporcional ao tamanho do raio
		// -> Quanto menor o raio, menor a densidade.
		densidade = (DENSIDADE_MAX * (raio * 2)) / 100;
		if(densidade > DENSIDADE_MAX) densidade = DENSIDADE_MAX;

		fireRadiusChangeListener(raio);
	}

	private void fireRadiusChangeListener(int raio) {
		for(RadiusListener listener : radiusChangeListeners) {
			listener.radiusChanged(new RadiusEvent(this, raio));
		}
	}

	public void addRadiusChangeListener(RadiusListener listener) {
		radiusChangeListeners.add(listener);
	}

	public void removeRadiusChangeListener(RadiusListener listener) {
		radiusChangeListeners.remove(listener);
	}
}