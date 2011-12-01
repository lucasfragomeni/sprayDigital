package cc.bebop.spraydigital.event;

@SuppressWarnings("serial")
public class SensorEvent extends Event {

	private int value;
	
	public SensorEvent(Object source, int value) {
		super(source);
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
}
