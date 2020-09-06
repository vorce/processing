/*
Sketch idea stolen from: https://s3.amazonaws.com/data.tumblr.com/tumblr_m5m237sDBw1rwi1s3o1_500.jpg
https://en.wikipedia.org/wiki/Bridget_Riley

*/
//import promidi.*;
//import promidi.Controller;

//KNKState nanoKontrol;
//KNKState tmpState;

float theta = 0.0;
ArrayList vl; 

void
setup() {
  size(800, 600);
  
  /*nanoKontrol = new KNKState(this);
  
  nanoKontrol.getScene1().setControls1(new ControlJags());
  nanoKontrol.getScene1().setControls2(new ControlGlobals());
  nanoKontrol.getScene1().setControls3(new ControlGlobals());
  */
  smooth();
  background(255);
  //frameRate(5);
  drawVerticalCurves(8, 110);
  drawStripes(20, 25);
}

void
draw() {
  /*background(255);
  drawVerticalCurves(8, 110);
  drawStripes(20, 25);
  */
}


void
drawVerticalCurves(int amount, int xspacing) {
  //ControlJags ctrlJ = (ControlJags)nanoKontrol.getScene1().getControls1();
  //ControlGlobals ctrlG = (ControlGlobals)nanoKontrol.getScene1().getControls2();
  
  vl = new ArrayList();
  theta += 0.02;
  //fill(0, 200, 0);
  stroke(color(0, 0, 0));
  float y = theta;
  
  for(int i = 0; i < amount/*ctrlG.getAmount()*/; i++) {
    ArrayList v = new ArrayList();
    
    int amp = (int)random(30, 50);
    float dy = (TWO_PI / (int)random(500, 900));
    
    for(int h = 0; h < height; h++) {
      float x = (amp+1)+(xspacing*i)+(cos(y)*amp);
      
      if(false) {
        point(x, h);
      }

      y += dy;
      v.add(x);
    }
    vl.add(v);
    
    y += xspacing;
  }
}

void
drawStripes(int amount, int yspacing) {
  /*ControlJags ctrlJ = (ControlJags)nanoKontrol.getScene1().getControls1();
  ControlGlobals ctrlG = (ControlGlobals)nanoKontrol.getScene1().getControls3();
  */
  stroke(color(0, 0, 0));
  fill(color(0, 0, 0));
 
  for(int a = 0; a < amount /*ctrlG.getAmount()*/; a++) {
    int y = 30+(yspacing*a);
    
    int h = (int)random(2, 15);
    int s = -1;
    for(int i = 0; i < vl.size()-1; i++) {
      if(i%2 == 0)
        s = -1;
      else
        s = 1;
        
      ArrayList v1 = (ArrayList)vl.get(i);
      ArrayList v2 = (ArrayList)vl.get(i+1);
      
      if(y >= v1.size())
        y = v1.size()-1;
        
      float x1 = (Float)v1.get(y);
      int offset = 25*s;
      int y2 = y+offset;
      
      if(y2 < 0)
        y2 = 0;
        
      float x2 = (Float)v2.get(y2);
      
      if(y+h >= v1.size())
        h = 0;
      float x3 = (Float)v1.get(y+h);
      int h2 = (int)random(2, 15);
      float x4 = (Float)v2.get(y2+h2);
      
      beginShape();
       vertex(x1, y);
       vertex(x2, y2);
       vertex(x4, y2+h2);
       vertex(x3, y+h);
      endShape(CLOSE);
      
      y = y2;
      h = h2;
    }
  }
}

void
mousePressed() {
  if(mouseButton == RIGHT) {
    String filename = "sj_" + year() + "-" + month() +
      "-" + day() + "_" + hour() +"."+minute()+"."+second()+".png";

    println("Saving image: " + filename);
    save(filename);
  }
}

/*void
controllerIn(promidi.Controller controller, int device, int channel) {
    nanoKontrol.controllerIn(controller);
}

class
ControlJags extends KNKAbstractControl {
  int lineThickness = 1;
  int lineOffset = 40;
  boolean showSines = false;
  
  public ControlJags() {
  }
  
  void sliderChanged(int val) {
    lineOffset = (int)(val - 63.0);
  }
  
  void
  knobChanged(int val) {
    lineThickness = (int)val+1;
  }
  
  void
  upperButtonPressed() {
    showSines = !showSines;
  }
  
  void
  lowerButtonPressed() {
  }
  
  void
  doBeat() {
  }
  
  void
  doNoBeat() {
  }
  
  public int
  getLineOffset() {
    return lineOffset;
  }
  
  public int
  getLineThickness() {
    return lineThickness;
  }
  
  public boolean
  isShowSines() {
    return showSines;
  }
}

class
ControlGlobals extends KNKAbstractControl {
  int amount = 8;
  int spacing = 25;
  
  public ControlGlobals() {
  }
  
  void sliderChanged(int val) {
    amount = (int)(val / 2);
  }
  
  void
  knobChanged(int val) {
    spacing = (int)val;
  }
  
  void
  upperButtonPressed() {
  }
  
  void
  lowerButtonPressed() {
  }
  
  void
  doBeat() {
  }
  
  void
  doNoBeat() {
  }
  
  public int
  getAmount() {
    return amount;
  }
  
  public int
  getSpacing() {
    return spacing;
  }
}*/

