class
Punkt {
  Vec3D position;
  color col;
  
  public
  Punkt(Vec3D position, color col) {
    this.position = position;
    this.col = col;
  }
  
  public
  Punkt(Vec3D position) {
    new Punkt(position, color(128, 128, 128));
  }
  
  public
  Punkt() {
    new Punkt(new Vec3D(random(0, width), random(0, height), 0), color(128, 128, 128));
  }
}
