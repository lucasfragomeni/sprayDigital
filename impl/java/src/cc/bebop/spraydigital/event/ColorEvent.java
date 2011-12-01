package cc.bebop.spraydigital.event;

@SuppressWarnings("serial")
public class ColorEvent extends Event {

	private int color;
	
	public ColorEvent(Object source, int color) {
		super(source);
		this.color = color;
	}

	public int getColor() {
		return color;
	}
	
}
