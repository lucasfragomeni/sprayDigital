package cc.bebop.spraydigital;

import processing.core.PApplet;
import cc.bebop.processing.util.ObjectUtil;

class UIRectangularComponent extends UIComponent {

	public UIRectangularComponent(PApplet pApplet) {
		super(pApplet);
	}

	public boolean isOver(int x, int y) {
		return ObjectUtil.isOver(x, y, this.x, this.y, this.width, this.height);
	}
}
