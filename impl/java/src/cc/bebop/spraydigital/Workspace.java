package cc.bebop.spraydigital;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import processing.core.PApplet;
import processing.core.PImage;
import cc.bebop.spraydigital.event.ButtonEvent;
import cc.bebop.spraydigital.event.ButtonListener;
import cc.bebop.spraydigital.event.ColorEvent;
import cc.bebop.spraydigital.event.ColorListener;
import cc.bebop.spraydigital.network.TwitpicService;

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
	
	private PApplet papp;

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
	
	/*
	 * resources
	 * 
	 */
	PImage
		imageNoticeSave,
		imageNoticeClean,
		imageNoticeUndo
		;

	public Workspace(PApplet papp)
	{
		/*
		 * read properties file
		 * 
		 */
		try
		{
			Reader reader;
			
			reader = new InputStreamReader(
					papp.createInput(propsPath),
					"ISO-8859-1"
			);
			
			props.load(reader);
			
			reader.close();
		}
		
		catch (IOException e)
		{
			System.out.println("Ocorreu um erro ao carregar o arquivo de propriedades: " + e.getMessage());
			e.printStackTrace();
		}
		
		imageNoticeSave = papp.loadImage("imageNoticeSave.png");
		imageNoticeClean = papp.loadImage("imageNoticeClean.png");
		imageNoticeUndo = papp.loadImage("imageNoticeUndo.png");
		
		// EXPERIMENTAL: twitpic
		if (props.getProperty("twitpic.enable").equals("true")) {
			TwitpicService.init(
					props.getProperty("twitpic.user"),
					props.getProperty("twitpic.pass"),
					props.getProperty("twitpic.text"),
					Long.parseLong(props.getProperty("twitpic.delay"))
					);
		}
		
		this.papp = papp;
		
		//this.pApplet.fill(0);
		//this.pApplet.smooth();
		//this.pApplet.stroke(0);
		
		//System.err.println("1 TIEM: " + System.currentTimeMillis());

		//Ponteiras
		brush = new Brush(papp);
		
		//System.err.println("2 TIEM: " + System.currentTimeMillis());

		//Componentes
		canvas = new Canvas(
				papp,
				Integer.parseInt(props.getProperty("canvas.discharge"))
		);
		
		canvas.setBrush(brush);
		
		//System.err.println("3 TIEM: " + System.currentTimeMillis());

		palhetaCores = new PalhetaCores(papp);
		palhetaCores.addColorChangeListener(brush);
		
		//System.err.println("4 TIEM: " + System.currentTimeMillis());
		
		notice = new Notice(papp, imageNoticeSave);
		
		//System.err.println("5 TIEM: " + System.currentTimeMillis());

		//Interface c/ Hardware
		sprayCan = new SprayCan(papp, props.getProperty("SprayCan.port"));
		sprayCan.addDistanceChangeListener(canvas);
		sprayCan.addColorChangeListener(this);
		sprayCan.addColorChangeListener(palhetaCores);
		sprayCan.addButtonListener(this);
		
		//System.err.println("6 TIEM: " + System.currentTimeMillis());
		
		actionClean();

	}

	public void draw()
	{
		sprayCan.lerSensores();

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
		papp.saveFrame(path);
		
		// EXPERIMENTAL: twitpic
		if (props.getProperty("twitpic.enable").equals("true")) {
			TwitpicService.getInstance().queue(new File(path));
		}
		
		actionClean();
	}
	
	public void actionUndo()
	{
		canvas.histBack();
	}
	
	public void actionClean()
	{
		canvas.reset();
		papp.image(papp.loadImage("brickwall.jpg"), 0, 0);
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
		cursorTimestamp = papp.millis();

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
		if(papp.millis() - cursorTimestamp <= DELAY_CLICK)
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
		switch(papp.key)
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
			palhetaCores.colorChanged(new ColorEvent(this, papp.key - '0'));
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
		if(papp.millis() > buttonTimeout || buttonLastAction != event.getAction())
		{
			buttonTimeout = papp.millis() + buttonDelay;
			buttonLastAction = event.getAction();
			
			if(palhetaCores.isVisible())
				palhetaCores.hide();
			
			switch(event.getAction())
			{
			case ButtonEvent.SALVAR:
				notice.setImageNotice(imageNoticeSave);
				break;
				
			case ButtonEvent.LIMPAR:
				notice.setImageNotice(imageNoticeClean);
				break;
				
			case ButtonEvent.DESFAZER:
				notice.setImageNotice(imageNoticeUndo);
				break;
				
			default:
				System.err.println("unknown button");
			}
			
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
		
		//buttonTimeout = papp.millis() + buttonDelay;
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