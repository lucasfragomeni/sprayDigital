import processing.opengl.*;
import ddf.minim.*;
import ddf.minim.signals.*;
import ddf.minim.analysis.*;
import ddf.minim.effects.*;
import krister.Ess.AudioChannel;
import krister.Ess.Ess;
import TUIO.*;
import cc.bebop.processing.util.*;
import processing.serial.Serial;

TuioProcessing tuio;
Workspace workspace;
Smoother smoother;

void setup() {
  /*
   * NOTE: Use P2D rather than OPENGL. Processing docs says its usually faster.
   * NOTE2: P2D is not working for some reason. Using P3D for now.
   * NOTE3: Falling back to OPENGL. Better image quality.
   *
   * WARNING: Keep teh call to size() above _EVERYTHING_! Processing docs says
   * it must be teh first line on setup()! In fact, everything called before
   * size() is run twice!
   *
   */
  size(screenWidth, screenHeight, OPENGL);
  frameRate(30);
  
  //fill(0);
  //stroke(0);
  smooth();
  //strokeWeight(2);

  tuio = new TuioProcessing(this);
  workspace = new Workspace(this);
  smoother = new Smoother();
}

void draw() {
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

void keyPressed() {
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

void addTuioCursor(TuioCursor tuioCursor) {
  if (cursor == null) {
    smoother = new Smoother(tuioCursor.getScreenX(width), tuioCursor.getScreenY(height));
    cursor = new Cursor(smoother.getSmoothedX(), smoother.getSmoothedY(), tuioCursor.getCursorID());
//    cursor = new Cursor(tuioCursor.getScreenX(width), tuioCursor.getScreenY(height), tuioCursor.getCursorID());
    addCursor = true;
  }
}

void updateTuioCursor(TuioCursor tuioCursor) {
  if (cursor != null) {
    smoother.smooth(tuioCursor.getScreenX(width), tuioCursor.getScreenY(height));
    cursor.setX(smoother.getSmoothedX());
    cursor.setY(smoother.getSmoothedY());
//    cursor.setX(tuioCursor.getScreenX(width));
//    cursor.setY(tuioCursor.getScreenY(height));
    updateCursor = true;
  }
}

void removeTuioCursor(TuioCursor tuioCursor) {
  if (cursor != null) {
    smoother.smooth(tuioCursor.getScreenX(width), tuioCursor.getScreenY(height));
    cursor.setX(smoother.getSmoothedX());
    cursor.setY(smoother.getSmoothedY());
//    cursor.setX(tuioCursor.getScreenX(width));
//    cursor.setY(tuioCursor.getScreenY(height));
    removeCursor = true;
  }
}

void addTuioObject(TuioObject tobj)
{
}

void refresh(TuioTime btime)
{
}

void removeTuioObject(TuioObject tobj)
{
}

void updateTuioObject(TuioObject tobj)
{
}

////////////////
// Processing //
////////////////

void stop() {
  workspace.stop();
  super.stop();
}
