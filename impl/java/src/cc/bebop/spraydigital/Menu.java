package cc.bebop.spraydigital;

import processing.core.PApplet;
import processing.core.PImage;
import cc.bebop.processing.util.ObjectUtil;

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
		y = pApplet.screenHeight/2; //pApplet.screenHeight + 70;// + 180;
		width = imgMenu.width;
		height = imgMenu.height;
	}

	public void draw() {
//		if(openingMenu) {
//			if(menuCounter < 40) {
//				revertState();
//				pApplet.noStroke();
//				pApplet.fill(200, 127);
//				pApplet.rectMode(PApplet.CORNER);
//				
//				width = menuCounter;
//				pApplet.rect(x, y, width, height);
//
//				menuCounter += 2;
//			} else {
//				menuOpened = true;
//				openingMenu = false;
//			}
//		} else if(hidingMenu) {
//			if(menuCounter >= 0) {
//				paintScreen();
//				pApplet.noStroke();
//				pApplet.fill(200, 127);
//				pApplet.rectMode(PApplet.CORNER);
//
//				width = menuCounter;
//				pApplet.rect(x, y, width, height);
//
//				menuCounter -= 2;
//			} else {
//				hidingMenu = false;
//				menuVisible = false;
//			}
//		} else {

//			if(isVisible()) {
//				pApplet.imageMode(PApplet.CENTER);
//				pApplet.image(imgMenu, x, y);
//			}

//				pApplet.stroke(255);
//				pApplet.fill(127);
//				pApplet.ellipseMode(PApplet.CENTER);
//				pApplet.ellipse(x, y, width, height);
//			}
//		}
	}

	public boolean isOver(Cursor cursor) {
		return ObjectUtil.isOver(x, y, cursor.getX(), cursor.getY(), width/2);
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