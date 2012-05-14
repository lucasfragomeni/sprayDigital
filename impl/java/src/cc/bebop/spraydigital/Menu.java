package cc.bebop.spraydigital;

import processing.core.PApplet;
import processing.core.PImage;

public class Menu extends UIComponent {

	boolean openingMenu = false;
	boolean hidingMenu = false;
	boolean menuOpened = false;

	boolean menuCollapsed = false;
	boolean menuExpanded = false;

	int menuCounter;

	PImage imgMenu;

	private boolean visible;

	public Menu(PApplet pApplet) {
		super(pApplet);
		imgMenu = pApplet.loadImage("menu.png");

		//Posiciona o menu no canto inferior esquerdo, meio escondido
		x = - (imgMenu.width/2) + 40;
		y = pApplet.screenHeight/2; //papp.screenHeight + 70;// + 180;
		width = imgMenu.width;
		height = imgMenu.height;
	}

	public void draw() {
//		if(openingMenu) {
//			if(menuCounter < 40) {
//				revertState();
//				papp.noStroke();
//				papp.fill(200, 127);
//				papp.rectMode(PApplet.CORNER);
//				
//				width = menuCounter;
//				papp.rect(x, y, width, height);
//
//				menuCounter += 2;
//			} else {
//				menuOpened = true;
//				openingMenu = false;
//			}
//		} else if(hidingMenu) {
//			if(menuCounter >= 0) {
//				paintScreen();
//				papp.noStroke();
//				papp.fill(200, 127);
//				papp.rectMode(PApplet.CORNER);
//
//				width = menuCounter;
//				papp.rect(x, y, width, height);
//
//				menuCounter -= 2;
//			} else {
//				hidingMenu = false;
//				menuVisible = false;
//			}
//		} else {

//			if(isVisible()) {
//				papp.imageMode(PApplet.CENTER);
//				papp.image(imgMenu, x, y);
//			}

//				papp.stroke(255);
//				papp.fill(127);
//				papp.ellipseMode(PApplet.CENTER);
//				papp.ellipse(x, y, width, height);
//			}
//		}
	}

	public void hide() {
		visible = false;
	}

	public void show() {
		visible = true;
	}

	public boolean isVisible() {
		return visible;
	}

	//////////////////
	// Click Events //
	//////////////////

	public void onClick(Cursor cursor) {
		if(!menuOpened && !openingMenu) {
			System.out.println("openingMenu");
			saveState();
			menuCounter = 0;
			openingMenu = true;
		} else if(menuOpened && !hidingMenu) {
			System.out.println("hidingMenu");
			hidingMenu = true;
			openingMenu = false;
		}
	}

}