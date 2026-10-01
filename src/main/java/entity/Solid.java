package entity;


/**
 * The abstract class Solid which inherits the material superclass.
 * Solid is the superclass for the solid materials.
 * It gets the x and y values from the superclass material.
 */
public abstract class Solid extends Material {
  private final boolean powdery;

  protected Solid(int x, int y) {
    this(x, y, false);
  }

  protected Solid(int x, int y, boolean powdery) {
    super(x, y);
    this.powdery = powdery;
  }

  protected Solid(int x, int y, boolean powdery, float density) {
    super(x, y, density);
    this.powdery = powdery;
  }

  public boolean isPowdery() {
    return powdery;
  }
}
