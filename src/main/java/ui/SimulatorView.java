package ui;

import entity.Material;
import entity.Sand;
import entity.Slate;
import entity.Water;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseButton;

public class SimulatorView extends BorderPane {
  private static final double CELL_SIZE = 1;
  private static final double MENU_HEIGHT = 44;
  private static final Color CLEAR_COLOR = Color.BLACK;
  private static final Color UNKNOWN_COLOR = Color.MAGENTA;

  private final int gridWidth;
  private final int gridHeight;
  private final Canvas grid;
  private final GraphicsContext graphics;
  private final HBox materialMenu = new HBox(8);
  private final Map<Class<? extends Material>, Color> colors = new LinkedHashMap<>();
  private final Map<Class<? extends Material>, Button> materialButtons = new LinkedHashMap<>();
  private final Map<Class<? extends Material>, BiFunction<Integer, Integer, ? extends Material>>
      materialFactories = new LinkedHashMap<>();

  private Class<? extends Material> selectedMaterial;
  private BiConsumer<Integer, Integer> materialPlacementHandler;
  private int lastPaintX;
  private int lastPaintY;
  private boolean hasLastPaintCell;

  public SimulatorView(int width, int height) {
    if (width <= 0 || height <= 0) {
      throw new IllegalArgumentException("Grid width and height must be positive");
    }

    this.gridWidth = width;
    this.gridHeight = height;
    double pixelWidth = width * CELL_SIZE;
    double pixelHeight = height * CELL_SIZE;
    grid = new Canvas(pixelWidth, pixelHeight);
    graphics = grid.getGraphicsContext2D();
    setStyle("-fx-background-color: black;");
    setPrefSize(pixelWidth, pixelHeight + MENU_HEIGHT);
    clearGrid();

    grid.setOnMousePressed(event -> {
      if (event.getButton() == MouseButton.PRIMARY) {
        int x = cellXAt(event.getSceneX(), event.getSceneY());
        int y = cellYAt(event.getSceneX(), event.getSceneY());
        paintCell(x, y);
        lastPaintX = x;
        lastPaintY = y;
        hasLastPaintCell = true;
      }
    });
    grid.setOnMouseDragged(event -> {
      if (event.isPrimaryButtonDown()) {
        int x = cellXAt(event.getSceneX(), event.getSceneY());
        int y = cellYAt(event.getSceneX(), event.getSceneY());
        if (hasLastPaintCell) {
          paintLine(lastPaintX, lastPaintY, x, y);
        } else {
          paintCell(x, y);
        }
        lastPaintX = x;
        lastPaintY = y;
        hasLastPaintCell = true;
      }
    });
    grid.setOnMouseReleased(event -> hasLastPaintCell = false);

    setCenter(grid);

    materialMenu.setMinHeight(MENU_HEIGHT);
    materialMenu.setPrefHeight(MENU_HEIGHT);
    materialMenu.setMaxHeight(MENU_HEIGHT);
    materialMenu.setPadding(new Insets(8));
    materialMenu.setStyle(
        "-fx-background-color: #303030;"
            + "-fx-border-color: #505050 transparent transparent transparent;"
            + "-fx-border-width: 1 0 0 0;");
    setBottom(materialMenu);

    registerMaterial("Sand", Sand.class, Color.YELLOW, Sand::new);
    registerMaterial("Water", Water.class, Color.DEEPSKYBLUE, Water::new);
  }

  public void registerMaterial(
      String name,
      Class<? extends Material> materialClass,
      Color color,
      BiFunction<Integer, Integer, ? extends Material> materialFactory) {
    Objects.requireNonNull(name);
    Objects.requireNonNull(materialClass);
    Objects.requireNonNull(color);
    Objects.requireNonNull(materialFactory);

    if (materialButtons.containsKey(materialClass)) {
      throw new IllegalArgumentException("Material is already registered: " + materialClass.getSimpleName());
    }

    colors.put(materialClass, color);
    materialFactories.put(materialClass, materialFactory);

    Button button = new Button(name);
    button.setOnAction(event -> {
      selectedMaterial = materialClass;
    });

    materialButtons.put(materialClass, button);
    materialMenu.getChildren().add(button);

    if (selectedMaterial == null) {
      selectedMaterial = materialClass;
    }
  }

  public Material createSelectedMaterial(int x, int y) {
    if (selectedMaterial == null) {
      return null;
    }
    return materialFactories.get(selectedMaterial).apply(x, y);
  }

  public void setOnMaterialPlacement(BiConsumer<Integer, Integer> handler) {
    materialPlacementHandler = Objects.requireNonNull(handler);
  }

  public Class<? extends Material> getSelectedMaterial() {
    return selectedMaterial;
  }

  public void setCellMaterial(int x, int y, Material material) {
    if (x < 0 || x >= gridWidth || y < 0 || y >= gridHeight) {
      throw new IndexOutOfBoundsException("Cell is outside the grid: (" + x + ", " + y + ")");
    }

    Color color = material == null
        ? CLEAR_COLOR
        : colors.getOrDefault(material.getClass(), UNKNOWN_COLOR);
    graphics.setFill(color);
    graphics.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
  }

  private int cellXAt(double sceneX, double sceneY) {
    Point2D point = grid.sceneToLocal(sceneX, sceneY);
    return (int) Math.floor(point.getX() / CELL_SIZE);
  }

  private int cellYAt(double sceneX, double sceneY) {
    Point2D point = grid.sceneToLocal(sceneX, sceneY);
    return (int) Math.floor(point.getY() / CELL_SIZE);
  }

  private void paintCell(int x, int y) {
    if (materialPlacementHandler == null) {
      return;
    }

    if (x >= 0 && x < gridWidth && y >= 0 && y < gridHeight) {
      materialPlacementHandler.accept(x, y);
    }
  }

  private void paintLine(int startX, int startY, int endX, int endY) {
    int deltaX = Math.abs(endX - startX);
    int stepX = startX < endX ? 1 : -1;
    int deltaY = -Math.abs(endY - startY);
    int stepY = startY < endY ? 1 : -1;
    int error = deltaX + deltaY;
    int x = startX;
    int y = startY;

    while (true) {
      paintCell(x, y);
      if (x == endX && y == endY) {
        break;
      }

      int doubledError = 2 * error;
      if (doubledError >= deltaY) {
        error += deltaY;
        x += stepX;
      }
      if (doubledError <= deltaX) {
        error += deltaX;
        y += stepY;
      }
    }
  }

  public void clearGrid() {
    graphics.setFill(CLEAR_COLOR);
    graphics.fillRect(0, 0, grid.getWidth(), grid.getHeight());
  }

  public void displaySlate(Slate slate){
    if (slate.getWidth() != gridWidth || slate.getHeight() != gridHeight) {
      throw new IllegalArgumentException("Slate dimensions must match the view");
    }

    clearGrid();
    for (int y = 0; y < slate.getHeight(); y++) {
      for (int x = 0; x < slate.getWidth(); x++) {
        Material material = slate.getMaterial(x, y);
        if (material != null) {
          setCellMaterial(x, y, material);
        }
      }
    }
  }

}