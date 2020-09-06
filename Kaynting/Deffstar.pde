public class
Deffstar {
  private ArrayList parts;
  private int cap = ROUND;
  public boolean oneColor = true;
  //public TColor colorOne;
  //public TColor colorTwo;
  
  public
  Deffstar(int sx, int sy, int np, int c) {
    int nParts = 1;
    
    if(np <= 0) {
      nParts = (int)random(2, 12);
    } else {
      nParts = np;
    }
    
    /*TColor col = ColorRange.DARK.getColor();
    ArrayList strategies = ColorTheoryRegistry.getRegisteredStrategies();
    ColorTheoryStrategy s = (ColorTheoryStrategy)strategies.get((int)random(0, strategies.size()));
    ColorList list = ColorList.createUsingStrategy(s, col);
    list=new ColorRange(list).addBrightnessRange(1, 2).getColors(null, 150, 0.5);
    list.sortByDistance(false);
    
    int colIndex = (int)random(list.size()/2, list.size());
    
    TColor colorOne = (TColor)list.get(colIndex);
    TColor colorTwo = (TColor)list.get((int)random(list.size()/2, list.size()));*/
    
    // println(colorOne);
    
    parts = new ArrayList();
    
    for(int i = 0; i < nParts; i++) {
      Part p = new Part(sx, sy);
      parts.add(p);
    }
    
    cap = c;
    strokeCap(cap);
  }
  
  public void
  render() {
    for(int i = 0; i < parts.size(); i++) {     
      Part p = (Part)parts.get(i);
      p.render();
    }
  }
}
