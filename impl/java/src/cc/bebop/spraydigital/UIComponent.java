package cc.bebop.spraydigital;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PImage;

abstract class UIComponent {

	protected PApplet papp;
	private PImage imgState;
	
	protected int x;
	protected int y;
	protected int width;
	protected int height;

	protected List<UIComponent> children = new ArrayList<UIComponent>(); 
	
	public UIComponent(PApplet pApplet) {
		this.papp = pApplet;
	}

	public void draw() {
		for(UIComponent child : children) {
			child.draw();
		}
	}
	
	public void add(UIComponent child) {
		children.add(child);
	}
	
	public void remove(UIComponent child) {
		children.add(child);
	}
	
	protected void saveState() {
		imgState = papp.get();
		
		/*
		System.err.printf(
				"state: (%d, %d); screen: (%d, %d);",
				imgState.width,
				imgState.height,
				papp.screenWidth,
				papp.screenHeight
				);
				*/
	}
	
	protected void revertState() {
		if(imgState != null) {
			papp.imageMode(PApplet.CORNER);
			papp.image(imgState, 0, 0);
		}
	}

	protected boolean isStateSaved() {
		return imgState != null;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
