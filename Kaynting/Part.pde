public class
Part {
  public int nCross = 1;
  public color crossColor = color(0, 0, 0);
  public ArrayList arcii = new ArrayList();
  
  private float PI_180 = PI/180;
  private float PI_2 = PI/2;
  
  public
  Part(int x, int y) {
    // one radian is equal to 180/π degrees
    // rad = deg*(pi/180)
 
    float radStart = radians(random(0, 360)); // * PI_180);
    float sw = random(0.5, 7.5); // stroke width
    
    Arcii a1 = new Arcii(x, y, radStart, radStart+(PI_2), sw);
    arcii.add(a1);   
    radStart+=(PI_2);
    
    Arcii a2 = new Arcii(x, y, radStart, radStart+(PI_2), sw);
    arcii.add(a2);
    radStart+=(PI_2);
   
    Arcii a3 = new Arcii(x, y, radStart, radStart+(PI_2), sw);
    arcii.add(a3);
    radStart+=(PI_2);
    
    Arcii a4 = new Arcii(x, y, radStart, radStart+(PI_2), sw);
    arcii.add(a4);
    radStart+=(PI_2);
  }
  
  public void
  render() {
    for(int i = 0; i < arcii.size(); i++) {
      Arcii a = (Arcii)arcii.get(i);
      a.render();
    }
  }
}
