package entity;

/**
 * Base class for liquid materials.
 */
public abstract class Fluid extends Material {
  protected Fluid(int x, int y) {
    super(x, y);
  }

  protected Fluid(int x, int y, float density) {
    super(x, y, density);
  }
}
