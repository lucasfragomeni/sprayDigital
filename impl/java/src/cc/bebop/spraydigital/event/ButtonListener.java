package cc.bebop.spraydigital.event;

import java.util.EventListener;

public interface ButtonListener extends EventListener {

	public void buttonPressed(ButtonEvent event);
}
