package cc.bebop.spraydigital;

import processing.core.PApplet;
import processing.core.PImage;

public class Notice extends UIComponent {
	
	private static final long DELAY_NOTICE = 3000;

	private PImage imageNotice;
	private PImage imageLogo;
	
	private boolean visible;

	private long timestampNotice;
	
	/////////////////
	// Constructor //
	/////////////////
	public Notice(PApplet pApplet, PImage imageNotice) {
		super(pApplet);
		setImageNotice(imageNotice);
		
		imageLogo = pApplet.loadImage("bebop.png");
	}
	
	/////////////////////////
	// Getters and setters //
	/////////////////////////
	public PImage getImageNotice() {
        	return imageNotice;
        }

	public void setImageNotice(PImage imageNotice) {
        	this.imageNotice = imageNotice;
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
		
		//System.err.println("SHOW");
		
		if(!visible)
			saveState();
		
		pApplet.imageMode(PApplet.CORNER);
		pApplet.image(imageLogo, pApplet.screenWidth - imageLogo.width - 15, 15);
			
		pApplet.imageMode(PApplet.CENTER);

		pApplet.image(
				imageNotice, 
				pApplet.screenWidth/2,
				pApplet.screenHeight/2
				);

		timestampNotice = pApplet.millis();
		visible = true;
	}

	public void hide() {
		//System.err.println("HIDE");
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
	}

}