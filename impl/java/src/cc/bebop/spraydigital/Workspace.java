package cc.bebop.spraydigital;

import java.io.IOException;
import java.util.Properties;

import processing.core.PApplet;
import cc.bebop.spraydigital.event.ButtonEvent;
import cc.bebop.spraydigital.event.ButtonListener;
import cc.bebop.spraydigital.event.ColorEvent;
//import cc.bebop.spraydigital.network.TwitpicService;

public class Workspace implements ButtonListener
{
	/*
	 * properties
	 * 
	 */
	private static final String propsPath =
			"sprayDigital.properties";
	
	private static final Properties props =
			new Properties();
	
	/*
	 * twitpic
	 * 
	 */
	//private TwitpicService twitpicService;
	
	private PApplet pApplet;

	private static final int FUNDO = 255;

	private SprayCan sprayCan;
	private Brush brush;

	private Canvas canvas;
	private PalhetaCores palhetaCores;
	
	private static final long DELAY_CLICK = 500;

	public Workspace(PApplet pApplet)
	{
		/*
		 * read properties file
		 * 
		 */
		try
		{
			props.load(pApplet.createReader(propsPath));
		}
		
		catch (IOException e)
		{
			System.out.println("Ocorreu um erro ao carregar o arquivo de propriedades: " + e.getMessage());
			e.printStackTrace();
		}
		
		/*
		if(true)
		{
			try
			{
				throw new RuntimeException();
			}
			
			catch(RuntimeException e)
			{
				e.printStackTrace();
			}
		}
		*/
		
		/*
		twitpicService = new TwitpicService(
				props.getProperty("twitpic.user"),
				props.getProperty("twitpic.pass"),
				props.getProperty("twitpic.text")
				);
		*/

		this.pApplet = pApplet;
		this.pApplet.image(pApplet.loadImage("brickwall.jpg"), 0, 0);
		this.pApplet.fill(0);
		this.pApplet.smooth();
		this.pApplet.stroke(0);

		//Ponteiras
		brush = new Brush(pApplet);

		//Componentes
		canvas = new Canvas(pApplet);
		canvas.setBrush(brush);

		palhetaCores = new PalhetaCores(pApplet);
		palhetaCores.addColorChangeListener(brush);

		//Interface c/ Hardware
		sprayCan = new SprayCan(pApplet);
		sprayCan.addDistanceChangeListener(brush);
		sprayCan.addColorChangeListener(palhetaCores);
		sprayCan.addButtonListener(this);

	}

	public void draw()
	{
		sprayCan.lerSensores();
		brush.ajustarRaio();

		canvas.draw();
//		menu.draw();
		palhetaCores.draw();
	}

	///////////////////
	// Cursor Events //
	///////////////////

	long cursorTimestamp;

	public void addCursor(Cursor cursor)
	{
		cursorTimestamp = pApplet.millis();

		if(palhetaCores.isVisible())
		{
			return;
		}

		canvas.addCursor(cursor);
	}

	public void updateCursor(Cursor cursor)
	{
		if(palhetaCores.isVisible())
		{
			return;
		}

		canvas.updateCursor(cursor);
	}

	public void removeCursor(Cursor cursor)
	{
		boolean click = false;
		if(pApplet.millis() - cursorTimestamp <= DELAY_CLICK)
		{
			click = true;
		}

		if(palhetaCores.isVisible())
		{
			if(click && palhetaCores.isOver(cursor))
			{
				palhetaCores.onClick(cursor);
			}
		}

		canvas.removeCursor(cursor);
	}

	////////////
	// Events //
	////////////

	public void keyPressed() {
		//Reinicializa
		if(pApplet.key == ' ') {
			canvas.reset();
			pApplet.background(FUNDO);
			canvas.histClear();
			canvas.histAdd();
		}
		else if(pApplet.key == 'b') {
			limpar();
		}
		//Salva
		else if(pApplet.key == 's') {
			salvar();
		}
		else if(pApplet.key >= '0' && pApplet.key <= '9') {
			palhetaCores.colorChanged(new ColorEvent(this, pApplet.key-48));
			brush.colorChanged(new ColorEvent(this, pApplet.key-48));
		}
		else if(pApplet.key == 'u') {
			canvas.histBack();
		}
	}
	
	//@Override
	public void buttonPressed(ButtonEvent event) {
		if(event.getAction().equals(ButtonEvent.SALVAR)) {
			salvar();
			limpar();
		}
		else if(event.getAction().equals(ButtonEvent.DESFAZER)) {
			desfazer();
		}
		else if(event.getAction().equals(ButtonEvent.LIMPAR)) {
			limpar();
		}
	}

	private void desfazer() {
		canvas.histBack();
	}
	
	private void salvar() {
		String path = props.getProperty("savePrefix") + System.currentTimeMillis() + ".jpg";
		
		pApplet.saveFrame(path);
		//byte buf[] = pApplet.loadBytes(path);
		
		//twitpicService.send(buf);
	}
		
	private void limpar() {
		canvas.reset();
		pApplet.image(pApplet.loadImage("brickwall.jpg"), 0, 0);
		canvas.histClear();
		canvas.histAdd();
	}
	
	////////////////
	// Processing //
	////////////////

	public void stop() {
		sprayCan.stop();
		canvas.stop();
	}
}