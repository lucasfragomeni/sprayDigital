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

void setup() {
  tuio = new TuioProcessing(this);
  workspace = new Workspace(this);
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
  if(key == '-') {
    smoothing -= 0.1;
  }
  else if(key == '=') {
    smoothing += 0.1;
  }
  println(smoothing);
    
  workspace.keyPressed();
}

//////////
// TUIO //
//////////

Cursor cursor;
boolean addCursor = false;
boolean updateCursor = false;
boolean removeCursor = false;

float smoothing = 0.85;
float smoothedX = 0;
float smoothedY = 0;

float smoothCursorPoint(float smoothed, float actual) {
  return smoothing * smoothed + (1-smoothing) * actual;
}

void addTuioCursor(TuioCursor tuioCursor) {
  if (cursor == null) {
    smoothedX = tuioCursor.getScreenX(width);
    smoothedY = tuioCursor.getScreenY(height);
    cursor = new Cursor(smoothedX, smoothedY, tuioCursor.getCursorID());
    //--
//    cursor = new Cursor(tuioCursor.getScreenX(width), tuioCursor.getScreenY(height), tuioCursor.getCursorID());
    addCursor = true;
  }
}

void updateTuioCursor(TuioCursor tuioCursor) {
  if (cursor != null) {
    //teste
    smoothedX = smoothCursorPoint(smoothedX, tuioCursor.getScreenX(width));
    smoothedY = smoothCursorPoint(smoothedY, tuioCursor.getScreenY(height));
    cursor.setX(smoothedX);
    cursor.setY(smoothedY);
    //--
//    cursor.setX(tuioCursor.getScreenX(width));
//    cursor.setY(tuioCursor.getScreenY(height));
    updateCursor = true;
  }
}

void removeTuioCursor(TuioCursor tuioCursor) {
  if (cursor != null) {
    //teste
    smoothedX = smoothCursorPoint(smoothedX, tuioCursor.getScreenX(width));
    smoothedY = smoothCursorPoint(smoothedY, tuioCursor.getScreenY(height));
    cursor.setX(smoothedX);
    cursor.setY(smoothedY);
    //--
//    cursor.setX(tuioCursor.getScreenX(width));
//    cursor.setY(tuioCursor.getScreenY(height));
    removeCursor = true;
  }
}

////////////////
// Processing //
////////////////

void stop() {
  workspace.stop();
  super.stop();
}

