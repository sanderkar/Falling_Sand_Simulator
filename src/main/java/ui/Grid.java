public class Grid {
  int width;
  int height;
  int[] grid;

  // Initialize the grid with the given width and height
  public void initialize(int width, int height) {
    this.width = width;
    this.height = height;
    this.grid = new int[width * height];
  }

  //allows us to clear the grid and reset it to its initial state
  public void clear(){
    this.grid = new int[width * height];
  }

  //set a cell in the grid to a specific color value
  public void set(int x, int y, int color){
    this.grid[y * this.width + x] = color;
  }

  //returns true if the cell is empty
  public boolean isEmpty(int index){
    return this.grid[index] == 0;
  }

  //swaps two particles
  public void swap(int a, int b){
    int temp = this.grid[a];
    this.grid[a] = this.grid[b];
    this.grid[b] = temp;
  }

}
