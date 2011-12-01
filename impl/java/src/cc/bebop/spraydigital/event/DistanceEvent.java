package cc.bebop.spraydigital.event;

@SuppressWarnings("serial")
public class DistanceEvent extends Event {

	private int distance;
	
	public DistanceEvent(Object source, int distance) {
		super(source);
		this.distance = distance;
	}

	public int getDistance() {
		return distance;
	}
	
}
