package cc.bebop.spraydigital;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.serial.Serial;
import cc.bebop.spraydigital.event.ButtonEvent;
import cc.bebop.spraydigital.event.ButtonListener;
import cc.bebop.spraydigital.event.ColorEvent;
import cc.bebop.spraydigital.event.ColorListener;
import cc.bebop.spraydigital.event.DistanceEvent;
import cc.bebop.spraydigital.event.DistanceListener;

/**
 * Representa o hardware, que envia os sinais dos sensores de distância e seleção de cor
 */
public class SprayCan {

	//@SuppressWarnings("unused")
	private PApplet pApplet;

	private static final int DISTANCIA_MIN = 35;
	private static final int LINE_FEED = 10;    // Linefeed in ASCII

	private static final byte INIT = 1;
	private static final byte ACK = 2;

	private int lastSeen = 0;
	private int timeOut = 1000;

	private Serial arduino;

	private List<ColorListener> colorListeners = new ArrayList<ColorListener>();
	private List<ButtonListener> buttonListeners = new ArrayList<ButtonListener>();
	private List<DistanceListener> distanceListeners = new ArrayList<DistanceListener>();

	private void seen() {
		lastSeen = pApplet.millis();
	}

	private long idleTime() {
		return pApplet.millis() - lastSeen;
	}

	public void sendInit() {
//		System.out.println("Sending init...");
		arduino.write(INIT);
	}

	public SprayCan(PApplet pApplet) {
		this.pApplet = pApplet;

		//TODO implementar "timeout"
		while(arduino == null) {
			try {
				PApplet.println(Serial.list());
				arduino = new Serial(pApplet, Serial.list()[0], 9600);

				//Inicializa o spray
				sendInit();
				seen();
				pApplet.delay(200);
			} 
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void lerSensores() {
		//Lê a distância da Arduino
		if(arduino.available() > 0) {
			String dataIn = arduino.readStringUntil(LINE_FEED);
			if(dataIn != null && dataIn.trim().length() > 0) { 
				//Dá o ACK para a Arduino
				arduino.write(ACK);

				//Trata o dado recebido
				dataIn = dataIn.trim();
				System.out.println("dataIn: " + dataIn);
				if(dataIn.startsWith("c:")) {
					fireColorChanged(Integer.parseInt(dataIn.split(":")[1]));
				} else if(dataIn.startsWith("b:")) {
					int btn = Integer.parseInt(dataIn.split(":")[1]);
					switch(btn) {
					case 1:
						fireButtonPressed(ButtonEvent.DESFAZER);
						break;
					case 2:
						fireButtonPressed(ButtonEvent.LIMPAR);
						break;
					case 3:
						fireButtonPressed(ButtonEvent.SALVAR);
					}
				} else {
					try {
						fireDistanciaChanged(Integer.parseInt(dataIn));
					} catch(NumberFormatException e) {
						System.err.println("ler sensores exception: " + e.getMessage());
					}
				}
			}

			seen();
		}

		if(idleTime() > timeOut) {
			sendInit();
		}
	}

	public void stop() {
		arduino.stop();
	}

	////////////////////
	// Button Listener //
	////////////////////

	public void addButtonListener(ButtonListener listener) {
		buttonListeners.add(listener);
	}

	public void removeButtonListener(ButtonListener listener) {
		buttonListeners.remove(listener);
	}

	private void fireButtonPressed(String action) {
		for(ButtonListener buttonListener : buttonListeners) {
			buttonListener.buttonPressed(new ButtonEvent(this, action));
		}
	}
	
	////////////////////
	// Color Listener //
	////////////////////
	
	public void addColorChangeListener(ColorListener listener) {
		colorListeners.add(listener);
	}
	
	public void removeColorChangeListener(ColorListener listener) {
		colorListeners.remove(listener);
	}
	
	private void fireColorChanged(int cor) {
		for(ColorListener colorChangeListener : colorListeners) {
			colorChangeListener.colorChanged(new ColorEvent(this, cor));
		}
	}

	///////////////////////
	// Distance Listener //
	///////////////////////

	public void addDistanceChangeListener(DistanceListener listener) {
		distanceListeners.add(listener);
	}

	public void removeDistanceChangeListener(DistanceListener listener) {
		distanceListeners.remove(listener);
	}

	private void fireDistanciaChanged(int distancia) {
		//A distância enviada pela Arduino varia aprox. entre 8 e 80,
		//mas nós só queremos a partir de DISTANCIA_MIN
		distancia = Math.max(distancia, DISTANCIA_MIN);

		//Subtrai DISTANCIA_MIN-1 para que o valor começe no 1
		distancia -= (DISTANCIA_MIN-1);

		for(DistanceListener distanceChangeListener : distanceListeners) {
			distanceChangeListener.distanceChanged(new DistanceEvent(this, distancia));
		}
	}
}