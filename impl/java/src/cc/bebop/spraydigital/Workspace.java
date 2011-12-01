package cc.bebop.spraydigital;

import processing.core.PApplet;
import cc.bebop.spraydigital.event.ButtonEvent;
import cc.bebop.spraydigital.event.ButtonListener;
import cc.bebop.spraydigital.event.ColorEvent;
import cc.bebop.spraydigital.network.TwitpicService;

public class Workspace implements ButtonListener {

	private PApplet pApplet;

	private static final int FUNDO = 255;

	private SprayCan sprayCan;
	private Brush brush;

	private Canvas canvas;
	private PalhetaCores palhetaCores;
	
	private static final long DELAY_CLICK = 500;

	public Workspace(PApplet pApplet) {
		this.pApplet = pApplet;
		this.pApplet.size(this.pApplet.screenWidth, this.pApplet.screenHeight, PApplet.OPENGL);
		this.pApplet.hint(PApplet.ENABLE_OPENGL_4X_SMOOTH);
		this.pApplet.frameRate(120F);
		this.pApplet.image(pApplet.loadImage("brickwall.jpg"), 0, 0);
		this.pApplet.fill(0);
		this.pApplet.smooth();
		this.pApplet.stroke(0);

		//Ponteiras
		brush = new Brush(pApplet);

		//Componentes
		canvas = new Canvas(pApplet);
		canvas.setBrush(brush);

		palhetaCores = new PalhetaCores(pApplet);
		palhetaCores.addColorChangeListener(brush);

		//Interface c/ Hardware
		sprayCan = new SprayCan(pApplet);
		sprayCan.addDistanceChangeListener(brush);
		sprayCan.addColorChangeListener(palhetaCores);
		sprayCan.addButtonListener(this);
	}

	public void draw() {
		sprayCan.lerSensores();
		brush.ajustarRaio();

		canvas.draw();
//		menu.draw();
		palhetaCores.draw();
	}

	///////////////////
	// Cursor Events //
	///////////////////

	long cursorTimestamp;

	public void addCursor(Cursor cursor) {
		cursorTimestamp = pApplet.millis();

		if(palhetaCores.isVisible()) {
			return;
		}

		canvas.addCursor(cursor);
	}

	public void updateCursor(Cursor cursor) {
		if(palhetaCores.isVisible()) {
			return;
		}

		canvas.updateCursor(cursor);
	}

	public void removeCursor(Cursor cursor) {
		boolean click = false;
		if(pApplet.millis() - cursorTimestamp <= DELAY_CLICK) {
			click = true;
		}

		if(palhetaCores.isVisible()) {
			if(click && palhetaCores.isOver(cursor)) {
				palhetaCores.onClick(cursor);
			}
		}

		canvas.removeCursor(cursor);
	}

	////////////
	// Events //
	////////////

	public void keyPressed() {
		//Reinicializa
		if(pApplet.key == ' ') {
			canvas.reset();
			pApplet.background(FUNDO);
			canvas.hist_clear();
			canvas.hist_add();
		}
		else if(pApplet.key == 'b') {
			canvas.reset();
			pApplet.image(pApplet.loadImage("brickwall.jpg"), 0, 0);
			canvas.hist_clear();
			canvas.hist_add();
		}
		//Salva
		else if(pApplet.key == 's') {
			pApplet.saveFrame("foo.jpg");
			byte buf[] = pApplet.loadBytes("foo.jpg");
			TwitpicService.send(buf);
		}
		else if(pApplet.key >= '0' && pApplet.key <= '9') {
			palhetaCores.colorChanged(new ColorEvent(this, pApplet.key-48));
			brush.colorChanged(new ColorEvent(this, pApplet.key-48));
		}
		else if(pApplet.key == 'u') {
			canvas.hist_back();
		}
	}
	
	@Override
	public void buttonPressed(ButtonEvent event) {
		if(event.getAction().equals(ButtonEvent.SALVAR)) {
			salvar();
			limpar();
		}
		else if(event.getAction().equals(ButtonEvent.DESFAZER)) {
			desfazer();
		}
		else if(event.getAction().equals(ButtonEvent.LIMPAR)) {
			limpar();
		}
	}

	private void desfazer() {
		canvas.hist_back();
	}
	
	private void salvar() {
		pApplet.saveFrame("imagemTemporaria.jpg");
		byte buf[] = pApplet.loadBytes("imagemTemporaria.jpg");
		TwitpicService.send(buf);
	}
		
	private void limpar() {
		canvas.reset();
		pApplet.image(pApplet.loadImage("brickwall.jpg"), 0, 0);
		canvas.hist_clear();
		canvas.hist_add();
	}
	
	////////////////
	// Processing //
	////////////////

	public void stop() {
		sprayCan.stop();
		canvas.stop();
	}
}