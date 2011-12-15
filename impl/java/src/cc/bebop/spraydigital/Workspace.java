package cc.bebop.spraydigital;

import java.io.IOException;
import java.util.Properties;

import processing.core.PApplet;
import cc.bebop.spraydigital.event.ButtonEvent;
import cc.bebop.spraydigital.event.ButtonListener;
import cc.bebop.spraydigital.event.ColorEvent;
//import cc.bebop.spraydigital.network.TwitpicService;
import cc.bebop.spraydigital.event.ColorListener;

public class Workspace implements ButtonListener, ColorListener
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

	//private static final int FUNDO = 255;

	private SprayCan sprayCan;
	private Brush brush;

	private Canvas canvas;
	private PalhetaCores palhetaCores;
	private Notice notice;
	
	//private static final long DELAY_CLICK = 500;
	
	long buttonTimeout;
	private static final long buttonDelay = 3000;
	
	int buttonLastAction;

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
		twitpicService = new TwitpicService(
				props.getProperty("twitpic.user"),
				props.getProperty("twitpic.pass"),
				props.getProperty("twitpic.text")
				);
		*/

		this.pApplet = pApplet;
		
		//this.pApplet.fill(0);
		//this.pApplet.smooth();
		//this.pApplet.stroke(0);
		
		//System.err.println("1 TIEM: " + System.currentTimeMillis());

		//Ponteiras
		brush = new Brush(pApplet);
		
		//System.err.println("2 TIEM: " + System.currentTimeMillis());

		//Componentes
		canvas = new Canvas(pApplet);
		canvas.setBrush(brush);
		
		//System.err.println("3 TIEM: " + System.currentTimeMillis());

		palhetaCores = new PalhetaCores(pApplet);
		palhetaCores.addColorChangeListener(brush);
		
		//System.err.println("4 TIEM: " + System.currentTimeMillis());
		
		notice = new Notice(pApplet);
		
		//System.err.println("5 TIEM: " + System.currentTimeMillis());

		//Interface c/ Hardware
		sprayCan = new SprayCan(pApplet);
		sprayCan.addDistanceChangeListener(brush);
		sprayCan.addColorChangeListener(this);
		sprayCan.addColorChangeListener(palhetaCores);
		sprayCan.addButtonListener(this);
		
		//System.err.println("6 TIEM: " + System.currentTimeMillis());
		
		actionClean();

	}

	public void draw()
	{
		sprayCan.lerSensores();
		brush.ajustarRaio();

		canvas.draw();
		palhetaCores.draw();
		notice.draw();
	}

	/////////////
	// Actions //
	/////////////

	public void actionSave()
	{
		String path = props.getProperty("savePrefix") + System.currentTimeMillis() + ".jpg";
		pApplet.saveFrame(path);
		
		actionClean();
	}
	
	public void actionUndo()
	{
		canvas.histBack();
	}
	
	public void actionClean()
	{
		canvas.reset();
		pApplet.image(pApplet.loadImage("brickwall.jpg"), 0, 0);
		canvas.histClear();
		canvas.histAdd();
	}

	///////////////////
	// Cursor Events //
	///////////////////

	long cursorTimestamp;

	private boolean cursorOn;

	public void addCursor(Cursor cursor)
	{
		cursorTimestamp = pApplet.millis();

		if(palhetaCores.isVisible())
			palhetaCores.hide();
		
		if(notice.isVisible())
		{
			notice.hide();
			buttonTimeout = 0;
		}
		
		cursorOn = true;
			
		canvas.addCursor(cursor);
	}

	public void updateCursor(Cursor cursor)
	{
		if(palhetaCores.isVisible())
			palhetaCores.hide();
		
		if(notice.isVisible())
		{
			notice.hide();
			buttonTimeout = 0;
		}

		canvas.updateCursor(cursor);
	}

	public void removeCursor(Cursor cursor)
	{
		/*
		boolean click = false;
		if(pApplet.millis() - cursorTimestamp <= DELAY_CLICK)
		{
			click = true;
		}

		if(palhetaCores.isVisible() || notice.isVisible())
		{
			if(click && palhetaCores.isOver(cursor))
			{
				palhetaCores.onClick(cursor);
			}
		}
		*/
		
		if(palhetaCores.isVisible())
			palhetaCores.hide();
		
		if(notice.isVisible())
		{
			notice.hide();
			buttonTimeout = 0;
		}
		
		cursorOn = false;

		canvas.removeCursor(cursor);
	}

	////////////
	// Events //
	////////////

	public void keyPressed()
	{		
		switch(pApplet.key)
		{
		/* blank */
		case ' ':
			/* FIXME: make screen blank, not brick */
			actionClean();
			break;
			
		/* brick wall */
		case 'b':
			actionClean();
			break;
			
		/* save */
		case 's':
			actionSave();
			break;
			
		/* undo */
		case 'u':
			actionUndo();
			break;
		
		/* set color */
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			/* somewhat hacky, but works */
			palhetaCores.colorChanged(new ColorEvent(this, pApplet.key - '0'));
			break;
		
		/* wat */
		default:
			return;
		}
	}
	
	public void buttonPressed(ButtonEvent event)
	{
		if(cursorOn)
			return;
		
		//System.err.println("button press");
		if(pApplet.millis() > buttonTimeout || buttonLastAction != event.getAction())
		{
			buttonTimeout = pApplet.millis() + buttonDelay;
			buttonLastAction = event.getAction();
			
			if(palhetaCores.isVisible())
				palhetaCores.hide();
			
			notice.show();
			
			return;
		}
		
		/*
		 * hide notice on confirmation
		 * 
		 * NOTE: must hide before saving frame!
		 * 
		 */
		notice.hide();
		
		switch(event.getAction())
		{
		case ButtonEvent.SALVAR:
			//System.err.println("button salvar");
			actionSave();
			break;
			
		case ButtonEvent.DESFAZER:
			//System.err.println("button desfazer");
			actionUndo();
			break;
			
		case ButtonEvent.LIMPAR:
			//System.err.println("button limpar");
			actionClean();
			break;
			
		default:
			System.err.println("invalid button event!");
			break;
		}
		
		//buttonTimeout = pApplet.millis() + buttonDelay;
		//buttonLastAction = event.getAction();
		
		buttonTimeout = 0;
		
	}
	
        public void colorChanged(ColorEvent event)
        {
        	if(cursorOn)
        		return;
        	
	        if(notice.isVisible())
	        {
	        	notice.hide();
	        	buttonTimeout = 0;
	        }
        }
	
	////////////////
	// Processing //
	////////////////

	public void stop() {
		sprayCan.stop();
		canvas.stop();
	}
}