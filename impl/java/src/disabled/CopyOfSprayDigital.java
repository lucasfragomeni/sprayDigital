package cc.bebop.spraydigital;

import processing.core.PApplet;
import TUIO.TuioCursor;
import TUIO.TuioObject;
import TUIO.TuioProcessing;
import TUIO.TuioTime;
import cc.bebop.util.Smoother;

//import cc.bebop.processing.util.*;

/**
 * @author IGOrrrr
 * 
 */
public class CopyOfSprayDigital extends PApplet {
	private static final long serialVersionUID = 1L;
	
	TuioProcessing tuio;
	Workspace workspace;
	Smoother smoother;

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
		size(1280, 720, OPENGL);
		smooth();
		frameRate(30);

		tuio = new TuioProcessing(this);
		workspace = new Workspace(this);
		smoother = new Smoother();
	}

	//////////
	// Loop //
	//////////

	@Override
	public void draw() {
		try {
			if (addCursor) {
				if (workspace != null) {
					workspace.addCursor(cursor);
				}
				addCursor = false;
			}

			else if (updateCursor) {
				if (workspace != null) {
					workspace.updateCursor(cursor);
				}
				updateCursor = false;
			}

			else if (removeCursor) {
				if (workspace != null) {
					workspace.removeCursor(cursor);
				}
				cursor = null;
				removeCursor = false;
			}

			workspace.draw();
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
	}

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

	//////////////
	// keyboard //
	//////////////

	public void keyPressed() {
		workspace.keyPressed();
	}

	//////////
	// TUIO //
	//////////

	Cursor cursor;
	boolean addCursor = false;
	boolean updateCursor = false;
	boolean removeCursor = false;

	public void addTuioCursor(TuioCursor tuioCursor) {
		if (cursor == null) {
			smoother = new Smoother(tuioCursor.getScreenX(width),
					tuioCursor.getScreenY(height));
			cursor = new Cursor(smoother.getSmoothedX(),
					smoother.getSmoothedY(), tuioCursor.getCursorID());
			// cursor = new Cursor(tuioCursor.getScreenX(width),
			// tuioCursor.getScreenY(height), tuioCursor.getCursorID());
			addCursor = true;
		}
	}

	public void updateTuioCursor(TuioCursor tuioCursor) {
		if (cursor != null) {
			smoother.smooth(tuioCursor.getScreenX(width),
					tuioCursor.getScreenY(height));
			cursor.setX(smoother.getSmoothedX());
			cursor.setY(smoother.getSmoothedY());
			// cursor.setX(tuioCursor.getScreenX(width));
			// cursor.setY(tuioCursor.getScreenY(height));
			updateCursor = true;
		}
	}

	public void removeTuioCursor(TuioCursor tuioCursor) {
		if (cursor != null) {
			smoother.smooth(tuioCursor.getScreenX(width),
					tuioCursor.getScreenY(height));
			cursor.setX(smoother.getSmoothedX());
			cursor.setY(smoother.getSmoothedY());
			// cursor.setX(tuioCursor.getScreenX(width));
			// cursor.setY(tuioCursor.getScreenY(height));
			removeCursor = true;
		}
	}

	public void addTuioObject(TuioObject tobj) {
	}

	public void refresh(TuioTime btime) {
	}

	public void removeTuioObject(TuioObject tobj) {
	}

	public void updateTuioObject(TuioObject tobj) {
	}

	////////////////
	// Processing //
	////////////////

//	public void stop() {
//		workspace.stop();
//		super.stop();
//	}
}
