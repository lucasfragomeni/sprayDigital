package cc.bebop.spraydigital;

import processing.core.PApplet;
import processing.core.PImage;

public class Notice extends UIComponent {
	
	private static final long DELAY_NOTICE = 3000;

	private PImage imgNotice;

	private boolean visible;

	private long timestampNotice;

	public Notice(PApplet pApplet) {
		super(pApplet);
		this.imgNotice = pApplet.loadImage("notice.png");
	}

	public void draw() {
		//Se já tiver passado o delay para esconder, o faz e finaliza a seleção de cor
		if(visible && pApplet.millis() - timestampNotice > DELAY_NOTICE) {
			hide();
		}
	}

	////////////////////
	// Seleção da cor //
	////////////////////

	public void show() {
		if(!visible) {
			revertState();
			
			pApplet.imageMode(PApplet.CENTER);
			pApplet.image(imgNotice, pApplet.screenWidth/2, pApplet.screenHeight/2);

			timestampNotice = pApplet.millis();
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
		//int posX = pApplet.screenWidth/2;
		//int posY = pApplet.screenHeight;
		//return ObjectUtil.isOver(posX, posY, cursor.getX(), cursor.getY(), imgNotice.width/2);
		/* FIXME: do something */
		return false;
	}

	public void onClick(Cursor cursor) {
		System.out.println(">>>>>> click no Notice <<<<<<");
	}

}