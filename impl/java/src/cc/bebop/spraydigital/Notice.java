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
		if(visible && papp.millis() - timestampNotice > DELAY_NOTICE) {
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
		
		papp.imageMode(PApplet.CORNER);
		papp.image(imageLogo, papp.screenWidth - imageLogo.width - 15, 15);
			
		papp.imageMode(PApplet.CENTER);

		papp.image(
				imageNotice, 
				papp.screenWidth/2,
				papp.screenHeight/2
				);

		timestampNotice = papp.millis();
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
		//int posX = papp.screenWidth/2;
		//int posY = papp.screenHeight;
		//return ObjectUtil.isOver(posX, posY, cursor.getX(), cursor.getY(), imgNotice.width/2);
		/* FIXME: do something */
		return false;
	}

	public void onClick(Cursor cursor) {
	}

}