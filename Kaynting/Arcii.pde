public class
Arcii {
  public int aWidth;
  public int aHeight;
  
  public float aStart;
  public float aStop;
  
  public int x = width/2;
  public int y = height/2;
  
  public float strokeWidth = 1.0;
  
  private float PI_2 = PI/2;
  
  private float lengthFactor = 2.5;
  
  public
  Arcii(int x, int y, float astart, float astop, float strokeWidth) {
    aStart = astart;
    aStop = astop;
    
    aWidth = (int)random(20, 350);
    aHeight = aWidth; //(int)random(25, 300);
    
    this.strokeWidth = strokeWidth;
    
    this.x = x;
    this.y = y;
    
    lengthFactor = 2.82; //random(2.2, 2.825);
  }
  
  public void
  render() {
    fill(255);
    strokeWeight(strokeWidth);
    arc(x, y, aWidth, aHeight, aStart, aStop);
    
    pushMatrix();
    //stroke(color(255, 0, 0));
    translate(x, y);
    float rotAng = aStart/2;
    
    float fx = aWidth/lengthFactor;
    float fy = aHeight/lengthFactor;
    
    rotate(aStart-(PI/4));
    // line(0, 0, 800, 600);
    line(0, 0, fx, fy);
    popMatrix();
    
    pushMatrix();
    //stroke(color(0, 255, 0));
    translate(x, y);
    
    //stroke(color(0, 255, 0));
    rotate(aStart+(PI/4));
    // line(0, 0, 800, 600);
    line(0, 0, fx, fy);
    popMatrix();
    
    //stroke(color(0, 0, 0));
  }
}
