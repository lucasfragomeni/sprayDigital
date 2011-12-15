package cc.bebop.spraydigital.event;

@SuppressWarnings("serial")
public class ButtonEvent extends Event {
	
	public static final int LIMPAR = 1;
	public static final int DESFAZER = 1 << 1;
	public static final int SALVAR = 1 << 2;

	private int action;
	
	public ButtonEvent(Object source, int action) {
		super(source);
		this.action = action;
	}

	public int getAction() {
		return action;
	}
	
}
