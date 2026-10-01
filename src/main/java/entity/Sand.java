package entity;

/**
 * The sand class, which inherits from solid, which then inherits from material.
 * Sand takes in the x and y values in its constructor, which it gets from its superclass Solid.
 */
  public class Sand extends Solid {
    public Sand(int x, int y){
      super(x, y, true, 2.65f);
    }

}
