/*
logo maker
*/

Deffstar weGot;
int[] capModes = {ROUND, SQUARE};

void
setup() {
  size(400, 400);
  smooth();
  background(255);
  strokeCap(ROUND);
  
  int cm = capModes[(int)random(0, capModes.length)];
  weGot = new Deffstar(width/2, height/2, 1, cm);
}

void
draw() {
  weGot.render();
}

void
mousePressed() {
  int cm = capModes[(int)random(0, capModes.length)];
  
  if(mouseButton == RIGHT) {
    String filename = "Kaynting_" + year() + "-" + month() + "-" + day() + "_" + hour() +"."+minute()+"."+second()+".png";
    println("Saving image: " + filename);
    save(filename);
  } else if(mouseButton == LEFT) { 
    weGot = new Deffstar(width/2, height/2, 1, cm);
  } else {
    background(255);
    weGot = new Deffstar(width/2, height/2, 0, cm);
  }
}
