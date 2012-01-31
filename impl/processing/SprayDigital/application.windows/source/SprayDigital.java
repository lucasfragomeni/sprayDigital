import processing.core.*; 
import processing.xml.*; 

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

import com.sun.opengl.util.j2d.*; 
import ddf.minim.*; 
import ddf.minim.javasound.*; 
import org.tritonus.share.sampled.*; 
import processing.core.*; 
import com.sun.opengl.impl.nurbs.*; 
import com.sun.opengl.impl.error.*; 
import com.harrison.lee.twitpic4j.*; 
import com.sun.opengl.impl.packrect.*; 
import processing.xml.*; 
import javazoom.jl.player.*; 
import javazoom.spi.mpeg.sampled.file.*; 
import com.sun.opengl.impl.mipmap.*; 
import gnu.io.*; 
import com.sun.opengl.util.texture.*; 
import com.sun.opengl.impl.tessellator.*; 
import com.sun.opengl.impl.*; 
import ddf.minim.signals.*; 
import org.tritonus.share.*; 
import cc.bebop.spraydigital.*; 
import com.sun.gluegen.runtime.*; 
import processing.serial.*; 
import com.sun.opengl.impl.windows.*; 
import javazoom.jl.decoder.*; 
import processing.opengl.*; 
import ddf.minim.effects.*; 
import com.sun.opengl.cg.*; 
import ddf.minim.analysis.*; 
import javazoom.jl.player.advanced.*; 
import com.sun.opengl.util.texture.spi.*; 
import org.tritonus.share.midi.*; 
import com.sun.opengl.impl.registry.*; 
import org.tritonus.share.sampled.mixer.*; 
import cc.bebop.spraydigital.event.*; 
import com.sun.opengl.util.*; 
import com.illposed.osc.utility.*; 
import cc.bebop.spraydigital.network.*; 
import org.tritonus.sampled.file.*; 
import javazoom.spi.mpeg.sampled.file.tag.*; 
import javax.media.opengl.*; 
import cc.bebop.util.*; 
import cc.bebop.http.*; 
import cc.bebop.processing.util.*; 
import org.tritonus.share.sampled.file.*; 
import org.tritonus.share.sampled.convert.*; 
import com.sun.opengl.impl.macosx.*; 
import javazoom.spi.mpeg.sampled.convert.*; 
import ddf.minim.spi.*; 
import com.sun.opengl.impl.x11.*; 
import javazoom.spi.*; 
import com.sun.opengl.impl.glue.*; 
import com.harrison.lee.twitpic4j.exception.*; 
import krister.Ess.*; 
import javazoom.jl.converter.*; 
import com.illposed.osc.*; 
import TUIO.*; 
import javax.media.opengl.glu.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class SprayDigital extends PApplet {












TuioProcessing tuio;
Workspace workspace;
Smoother smoother;

public void setup() {
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
