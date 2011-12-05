package disabled;
import processing.core.*; 
import TUIO.*;
import cc.bebop.spraydigital.*; 
import cc.bebop.util.*; 

public class SprayDigital extends PApplet {
/**
         * 
         */
        private static final long serialVersionUID = 1L;
TuioProcessing tuio;
Workspace workspace;
Smoother smoother;

public void setup() {
  tuio = new TuioProcessing(this);
  workspace = new Workspace(this);
  smoother = new Smoother();
}

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
  catch(Exception e) {
    println(e.getMessage());
  }
}

public void keyPressed() {
//  if(key == '-') {
//    smoothing -= 0.1;
//  }
//  else if(key == '=') {
//    smoothing += 0.1;
//  }
//  println(smoothing);
    
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
    smoother = new Smoother(tuioCursor.getScreenX(width), tuioCursor.getScreenY(height));
    cursor = new Cursor(smoother.getSmoothedX(), smoother.getSmoothedY(), tuioCursor.getCursorID());
//    cursor = new Cursor(tuioCursor.getScreenX(width), tuioCursor.getScreenY(height), tuioCursor.getCursorID());
    addCursor = true;
  }
}

public void updateTuioCursor(TuioCursor tuioCursor) {
  if (cursor != null) {
    smoother.smooth(tuioCursor.getScreenX(width), tuioCursor.getScreenY(height));
    cursor.setX(smoother.getSmoothedX());
    cursor.setY(smoother.getSmoothedY());
//    cursor.setX(tuioCursor.getScreenX(width));
//    cursor.setY(tuioCursor.getScreenY(height));
    updateCursor = true;
  }
}

public void removeTuioCursor(TuioCursor tuioCursor) {
  if (cursor != null) {
    smoother.smooth(tuioCursor.getScreenX(width), tuioCursor.getScreenY(height));
    cursor.setX(smoother.getSmoothedX());
    cursor.setY(smoother.getSmoothedY());
//    cursor.setX(tuioCursor.getScreenX(width));
//    cursor.setY(tuioCursor.getScreenY(height));
    removeCursor = true;
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

public void stop() {
  workspace.stop();
  super.stop();
}

  static public void main(String args[]) {
    PApplet.main(new String[] { "--present", "--bgcolor=#666666", "--hide-stop", "SprayDigital" });
  }
}