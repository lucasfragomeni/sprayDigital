package cc.bebop.spraydigital;

import processing.core.PApplet;
import TUIO.TuioCursor;
import TUIO.TuioObject;
import TUIO.TuioProcessing;
import TUIO.TuioTime;

/**
 * @author IGOrrrr
 * 
 */
public class SprayDigital extends PApplet
{
	private static final long serialVersionUID = 1L;

	Cursor cursor = new Cursor(0, 0, 0);
	boolean addCursor = false;
	boolean updateCursor = false;
	boolean removeCursor = false;
	
	TuioProcessing tuio;
	Workspace workspace;

	///////////
	// Setup //
	///////////

	@Override
	public void setup() {
		//
		// WARNING:
		//
		// size() must be teh first function called in setup(). Anything before
		// it may get called more than once.
		//
		// NOTE:
		//
		// OPENGL renderer is teh best in speed and quality.
		//
		// NOTE2:
		//
		// For some reason, we cannot call size(screenWidth, screenHeight,
		// OPENGL).
		//
		// NOTE3:
		//
		// NOTE2 only applies when trying to execute as applet from eclipse.
		//
		//size(screenWidth, screenHeight, OPENGL);
		size(1280, 720, OPENGL);
		smooth();
		frameRate(30);

		tuio = new TuioProcessing(this);
		workspace = new Workspace(this);

		// ((PGraphicsOpenGL) g).gl.glDisable(GL.GL_LIGHTING);
		// ((PGraphicsOpenGL) g).gl.glDisable(GL.GL_CULL_FACE);
		// ((PGraphicsOpenGL) g).gl.glDisable(GL.GL_DEPTH_TEST);
	}

	//////////
	// Loop //
	//////////

	@Override
	public void draw() {
		synchronized (cursor) {
			try {
				workspace.draw();
			}
			
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//////////////
	// keyboard //
	//////////////

	public void keyPressed() {
		workspace.keyPressed();
	}

	//////////
	// TUIO //
	//////////
	
	private void setCursorXYScaled(TuioCursor tcursor)
	{
		cursor.setX(tcursor.getX() * screenWidth);
		cursor.setY(tcursor.getY() * screenHeight);
	}

	public void addTuioCursor(TuioCursor tcursor)
	{
		synchronized (cursor) {
			if (cursor.getCursorID() != 0)
				return;
			
			setCursorXYScaled(tcursor);
			cursor.setCursorID(tcursor.getCursorID());
			workspace.addCursor(cursor);
		}
	}
	
	public void updateTuioCursor(TuioCursor tcursor)
	{
		synchronized (cursor) {
			if (cursor.getCursorID() != tcursor.getCursorID())
				return;
			
			setCursorXYScaled(tcursor);
			workspace.updateCursor(cursor);
		}
	}

	public void removeTuioCursor(TuioCursor tcursor)
	{
		synchronized (cursor) {
			if (cursor.getCursorID() != tcursor.getCursorID())
				return;
			
			setCursorXYScaled(tcursor);
			workspace.removeCursor(cursor);
			cursor.setCursorID(0);
		}
	}

	public void addTuioObject(TuioObject tobj)
	{
	}

	public void refresh(TuioTime btime)
	{
	}

	public void removeTuioObject(TuioObject tobj)
	{
	}

	public void updateTuioObject(TuioObject tobj)
	{
	}

	////////////////
	// Processing //
	////////////////

	//public void stop() {
	//	workspace.stop();
	//	super.stop();
	//}

	//////////
	// main //
	//////////

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			PApplet.main(args);
		}
		
		catch (RuntimeException e) {
			e.printStackTrace();
		}
	}
}
