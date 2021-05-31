// import damkjer.ocd.*;

import toxi.util.datatypes.*;
import toxi.math.noise.*;
import toxi.math.waves.*;
import toxi.geom.*;
import toxi.math.*;
import toxi.math.conversion.*;
import toxi.geom.util.*;

import processing.opengl.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;

int X_SPACE = 32;
int Y_SPACE = 32;
int NR_X_POINTS = 0;
int NR_Y_POINTS = 0;
int XY = 0;
GLU glu;
PGraphicsOpenGL pgl;
GL gl;
ArrayList punkter = null;
ArrayList noisePunkter = null;
PerlinNoise perlin;
float NS = 0.005f; // noise scale (try from 0.005 to 0.5)
float NOISE_IMPACT = 50;
float noiseOffset = 1000;
// Camera camera1;


void
setup() {
  size(800, 600, OPENGL);
  punkter = new ArrayList();
  noisePunkter = new ArrayList();
  NR_X_POINTS = width/X_SPACE;
  NR_Y_POINTS = height/Y_SPACE;
  XY = NR_X_POINTS * NR_Y_POINTS;
  perlin = new PerlinNoise();
  
  for(int x = 0; x < NR_X_POINTS; x++) {
    for(int y = 0; y < NR_Y_POINTS; y++) {
      punkter.add(new Punkt(new Vec3D(x*X_SPACE, y*Y_SPACE, perlin.noise(x*X_SPACE, y*Y_SPACE)*random(75,125)),
        color(90, 150, 100, 50)));
    }
  }
  println("punkter.size(): " + punkter.size());
  // camera1 = new Camera(this, 0, 0, 1000); //cx, cy, cz);
  smooth();
  
  glu=((PGraphicsOpenGL)g).glu;
  pgl = (PGraphicsOpenGL) g;
  gl = pgl.gl;
  gl.glPointSize(random(2, 10));
  gl.glLineWidth(random(1, 4));
}


void
draw() {
  background(0);
  glu=((PGraphicsOpenGL)g).glu;
  pgl = (PGraphicsOpenGL) g;
  gl = pgl.gl;
  noisePunkter.clear();
  //camera1.feed();
  //camera1.circle(0.002);
  
  gl.glDepthMask(false);
  gl.glEnable(GL.GL_BLEND);
  gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
  
  //noStroke();
  //fill(150, 60, 40, 128);
  pgl.beginGL(); 
  
  gl.glBegin(gl.GL_POINTS);
  //gl.glColor4f(0.4, 0.9, 0.7, 0.5);
  gl.glColor4f(0.8, 0.4, 0.1, 0.5);
  for(int i = 0; i < punkter.size(); i++) {
    Punkt p = (Punkt)punkter.get(i);
      
      // float noiseVal = /*perlin.noise(p.position.x*NS+noiseOffset, p.position.y*NS+noiseOffset);*/ (float)SimplexNoise.noise(p.position.x*NS+noiseOffset, p.position.y*NS+noiseOffset);
      // noiseVal *= 10;

      float xNoise = /*perlin.noise(p.position.x*NS+noiseOffset);*/ (float)SimplexNoise.noise(p.position.x*NS+noiseOffset, 0, 0);
      xNoise *= NOISE_IMPACT;
      float yNoise = /*perlin.noise(p.position.y*NS+noiseOffset);*/(float)SimplexNoise.noise(0, p.position.y*NS+noiseOffset, 0);
      yNoise *= NOISE_IMPACT;
      float zNoise = /*perlin.noise(p.position.z*NS+noiseOffset);*/(float)SimplexNoise.noise(0, 0, p.position.z*NS+noiseOffset);
      zNoise *= NOISE_IMPACT;
      
      // gl.glVertex3f(np.x, np.y, np.z);
      gl.glVertex3f(p.position.x+xNoise, 
                   p.position.y+yNoise, p.position.z+zNoise);
      //ellipse(p.position.x+xNoise+i, p.position.y+yNoise+i, abs(p.position.z+zNoise), abs(p.position.z+zNoise));
      noisePunkter.add(new Punkt(new Vec3D(p.position.x+xNoise, 
                    p.position.y+yNoise, p.position.z+zNoise), color(200, 200, 200)));
  }
  gl.glEnd();
  
  
  int p = 0;
  stroke(100, 120, 150, 140);
  
  gl.glBegin(gl.GL_LINES);
  gl.glColor4f(0.5, 0.7, 0.8, 0.5);
  
  for(int i = 0; i < noisePunkter.size()-1; i++) {
    Punkt p1, p2, p3;
    p1 = p2 = p3 = null;
    
    p1 = (Punkt)noisePunkter.get(i);
    p2 = (Punkt)noisePunkter.get(i+1);
  
    if(p1.position.y < p2.position.y) {
      // line(p1.position.x, p1.position.y, p1.position.z, p2.position.x, p2.position.y, p2.position.z);
      gl.glVertex3f(p1.position.x, p1.position.y, p1.position.z);
      gl.glVertex3f(p2.position.x, p2.position.y, p2.position.z);
    }
    
    if(i < ((XY)-NR_Y_POINTS)) {
      p3 = (Punkt)noisePunkter.get((i+NR_Y_POINTS));
      // println("i: " + i + ", p3: " + p3.position);
      if(p1.position.x <= p3.position.x) {
        gl.glVertex3f(p1.position.x, p1.position.y, p1.position.z);
        gl.glVertex3f(p3.position.x, p3.position.y, p3.position.z);
        // line(p1.position.x, p1.position.y, p1.position.z, p3.position.x, p3.position.y, p3.position.z);
       }
    }  
  }
  gl.glEnd();
  pgl.endGL();
  noiseOffset+=NS/2;
}

void
keyPressed()
{
  if(key == CODED)
  {
    if(keyCode == LEFT)
    { 
      NS /= 2;
    }
    else if(keyCode == RIGHT)
    { 
      NS *= 2;
    }
    else if(keyCode == UP)
    {
      NOISE_IMPACT += 5;
    } else if(keyCode == DOWN) {
      NOISE_IMPACT -= 5;
    }
  } 
  else if(key == ' ')
  {
    // screenshot  
  }
}

/*void mouseMoved()
{
    camera1.arc(radians(mouseY - pmouseY)/4);
    camera1.circle(radians(mouseX - pmouseX)/4);
}*/

// Function to manage mouse movement
/*void mouseDragged()
{
  if (mouseButton == CENTER)
  {
    // Look around
    camera1.truck(mouseX - pmouseX);
    camera1.boom(mouseY - pmouseY);
  }
  else if (mouseButton == LEFT) {
    camera1.tilt(radians(mouseY - pmouseY) / 2.0);
    camera1.pan(radians(mouseX - pmouseX) / 2.0);
  }
  else {
    camera1.dolly((pmouseX-mouseX)+(pmouseY-mouseY)/2);
  }
}*/

