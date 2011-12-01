package cc.bebop.spraydigital.event;

import java.util.EventListener;

public interface DistanceListener extends EventListener {

	public void distanceChanged(DistanceEvent event);
}
