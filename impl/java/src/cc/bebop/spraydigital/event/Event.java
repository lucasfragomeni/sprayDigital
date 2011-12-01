package cc.bebop.spraydigital.event;

import java.util.EventObject;

@SuppressWarnings("serial")
public abstract class Event extends EventObject {

	private boolean consumed;

	public Event(Object source) {
		super(source);
	}

	/**
	 * Indicates if the event is 'consumed'
	 * @return
	 */
	public boolean isConsumed() {
		return consumed;
	}

	/**
	 * Mark the event as 'consumed', so it will stop propagation
	 */
	public void consume() {
		this.consumed = true;
	}

}
