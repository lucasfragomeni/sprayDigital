package cc.bebop.spraydigital;

import java.util.LinkedList;
import java.util.List;

public class ContentManager {

	private List<UIComponent> components = new LinkedList<UIComponent>();
	
	public void add(UIComponent component) {
		components.add(component);
	}
	
	public void remove(UIComponent component) {
		components.remove(component);
	}
	
}
