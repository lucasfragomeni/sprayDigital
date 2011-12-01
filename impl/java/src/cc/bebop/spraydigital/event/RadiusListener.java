package cc.bebop.spraydigital.event;

import java.util.EventListener;

public interface RadiusListener extends EventListener {

	public void radiusChanged(RadiusEvent event);
}
