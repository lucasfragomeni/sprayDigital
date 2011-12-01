package cc.bebop.spraydigital.event;

import java.util.EventListener;

public interface SprayListener extends EventListener {

	public void sprayStarted(SprayEvent event);
	
	public void sprayEnded(SprayEvent event);
}
