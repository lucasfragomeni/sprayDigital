package cc.bebop.spraydigital.event;

import java.util.EventListener;

public interface SensorListener extends EventListener {

	public void valueChanged(SensorEvent event);
}
