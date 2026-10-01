package entity;

public class Slate {
  private final Material[][] cells;

  public Slate(int width, int height) {
    if (width <= 0 || height <= 0) {
      throw new IllegalArgumentException("Slate dimensions must be positive");
    }
    cells = new Material[height][width];
  }

  public Slate(Slate previous) {
    cells = new Material[previous.getHeight()][previous.getWidth()];
    for (int y = 0; y < cells.length; y++) {
      System.arraycopy(previous.cells[y], 0, cells[y], 0, cells[y].length);
    }
  }

  public int getWidth() {
    return cells[0].length;
  }

  public int getHeight() {
    return cells.length;
  }

  public Material getMaterial(int x, int y) {
    return cells[y][x];
  }

  public void setMaterial(int x, int y, Material material) {
    cells[y][x] = material;
  }

  public void moveMaterial(int fromX, int fromY, int toX, int toY) {
    Material material = getMaterial(fromX, fromY);
    if (material == null || getMaterial(toX, toY) != null) {
      throw new IllegalStateException("Cannot move material into an occupied cell");
    }

    cells[toY][toX] = material;
    cells[fromY][fromX] = null;
    material.moveTo(toX, toY);
  }

  public void swapMaterials(int firstX, int firstY, int secondX, int secondY) {
    Material first = getMaterial(firstX, firstY);
    Material second = getMaterial(secondX, secondY);
    if (first == null || second == null) {
      throw new IllegalStateException("Cannot swap with an empty cell");
    }

    cells[firstY][firstX] = second;
    cells[secondY][secondX] = first;
    first.moveTo(secondX, secondY);
    second.moveTo(firstX, firstY);
  }
}