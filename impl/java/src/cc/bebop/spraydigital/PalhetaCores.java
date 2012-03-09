package cc.bebop.spraydigital;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PImage;
import cc.bebop.processing.util.ObjectUtil;
import cc.bebop.spraydigital.event.ColorEvent;
import cc.bebop.spraydigital.event.ColorListener;

public class PalhetaCores extends UIComponent implements ColorListener {

	private static final long DELAY_SELECAO_COR = 1500;

	private int indiceCor;
	private PImage imgMenu;
	private PImage imgPalhetaCores;
	private PImage imgSeletorCor;
	private PImage imgBebop;

	private boolean visible;

	private long timestampSelecaoCor;

	private int color;
	
	private int colors[] = new int[] {
			0xff000000, 0xffcb1d10, 0xfff4a33f, 0xfffefa4e, 
			0xff67b742, 0xff26a7e2, 0xff0d4ec2, 0xff7337cc, 
			0xfff954ee, 0xffff959c, 0xffd6b7fa, 0xffa2c3fb, 
			0xffbdfde2, 0xff851452, 0xffffffff};

	private List<ColorListener> colorChangeListeners = new ArrayList<ColorListener>();

	public PalhetaCores(PApplet pApplet) {
		super(pApplet);
		this.imgMenu = pApplet.loadImage("menu_03.png");
		this.imgPalhetaCores = pApplet.loadImage("palhetaCores.png");
		this.imgSeletorCor = pApplet.loadImage("spraycan.png");
		this.imgBebop = pApplet.loadImage("bebop.png");
	}

	public void draw() {
		//Se já tiver passado o delay para esconder, o faz e finaliza a seleção de cor
		if(visible && papp.millis() - timestampSelecaoCor > DELAY_SELECAO_COR) {
			hide();
		}
	}

	////////////////////
	// Seleção da cor //
	////////////////////

	public void show() {
		if(!visible) {
			revertState();

			papp.imageMode(PApplet.CORNER);
			papp.image(imgBebop, papp.screenWidth - imgBebop.width - 15, 15);

			papp.imageMode(PApplet.CENTER);
			papp.image(imgPalhetaCores, papp.screenWidth/2, papp.screenHeight/2);
//			papp.image(imgMenu, papp.screenWidth/2, papp.screenHeight);

			int posX = (papp.screenWidth/2) + ((indiceCor - 7) * 80);
			int posY = papp.screenHeight/2 - 22;
			papp.image(imgSeletorCor, posX, posY);

			//Obtém a cor do fundo
			int zeroX = papp.screenWidth/2 - imgPalhetaCores.width/2;
			int posXRelativo = (posX - zeroX) - 40;
			color = colors[(int)posXRelativo/80];
//			color = Util.getColor(papp, posX, posY);

			timestampSelecaoCor = papp.millis();
			visible = true;
		}
	}

	public void hide() {
		revertState();
		visible = false;
	}

	public boolean isVisible() {
		return visible;
	}

	public boolean isOver(Cursor cursor) {
		int posX = papp.screenWidth/2;
		int posY = papp.screenHeight;
		return ObjectUtil.isOver(posX, posY, cursor.getX(), cursor.getY(), imgMenu.width/2);
	}

	public void onClick(Cursor cursor) {
		System.out.println(">>>>>> click no menu <<<<<<");
	}

	////////////
	// Events //
	////////////

	/**
	 * Recebe o dado crú do sensor
	 */
	//@Override
	public void colorChanged(ColorEvent event) {
		indiceCor = Math.min(event.getColor(), 14);

		if(!visible) { //Para impedir que substitua a imagem original quando do 'repaint'
			saveState();
		}
		visible = false; //Para forçar o 'repaint' no 'show'

		show();
		fireCorChanged(color);
	}

	////////////////////
	// Color Listener //
	////////////////////

	public void addColorChangeListener(ColorListener listener) {
		colorChangeListeners.add(listener);
	}

	public void removeColorChangeListener(ColorListener listener) {
		colorChangeListeners.remove(listener);
	}

	private void fireCorChanged(int cor) {
		for(ColorListener colorChangeListener : colorChangeListeners) {
			colorChangeListener.colorChanged(new ColorEvent(this, cor));
		}
	}
}