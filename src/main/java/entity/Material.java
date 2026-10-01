package entity;

/**
 * The common elements of all materials in the falling sand simulator.
 * The other materials will inherit from this class.
 * @TODO add the abstract material class
 */
public abstract class Material {
  private int x;
  private int y;
  private final float density;

  public Material(int x, int y) {
    this(x, y, 1.0f);
  }

  public Material(int x, int y, float density) {
    if (!Float.isFinite(density) || density <= 0) {
      throw new IllegalArgumentException("Material density must be finite and positive");
    }
    this.x = x;
    this.y = y;
    this.density = density;
  }

  public int getX(){
    return x;
  }

  public int getY(){
    return y;
  }

  public float getDensity() {
    return density;
  }

  public void moveTo(int x, int y) {
    this.x = x;
    this.y = y;
  }

}
