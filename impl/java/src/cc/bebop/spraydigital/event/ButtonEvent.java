package cc.bebop.spraydigital.event;

@SuppressWarnings("serial")
public class ButtonEvent extends Event {

	public static final String LIMPAR = "LIMPAR";
	public static final String DESFAZER = "DESFAZER";
	public static final String SALVAR = "SALVAR";
	
	private String action;
	
	public ButtonEvent(Object source, String action) {
		super(source);
		this.action = action;
	}

	public String getAction() {
		return action;
	}
	
}
