package cc.bebop.spraydigital.event;

import java.util.EventListener;

public interface ColorListener extends EventListener {

	public void colorChanged(ColorEvent event);
}
