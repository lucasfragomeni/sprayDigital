package cc.bebop.spraydigital.event;

@SuppressWarnings("serial")
public class RadiusEvent extends Event {

	private int radius;
	
	public RadiusEvent(Object source, int radius) {
		super(source);
		this.radius = radius;
	}

	public int getRadius() {
		return radius;
	}
	
}
