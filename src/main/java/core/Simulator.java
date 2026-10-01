package core;

import entity.Material;
import entity.Fluid;
import entity.Solid;
import entity.Slate;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import ui.SimulatorView;
import java.util.concurrent.ThreadLocalRandom;

public class Simulator {
  private static final int DEFAULT_WIDTH = 720;
  private static final int DEFAULT_HEIGHT = 600;

  private final int width;
  private final int height;
  private final SimulatorView view;

  private Slate slate;
  private Timeline timeline;
  private int step;
  private int delay = 5;
  private boolean isRunning = true;

  public Simulator() {
    this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
  }

  public Simulator(int width, int height) {
    if (width <= 0 || height <= 0) {
      width = DEFAULT_WIDTH;
      height = DEFAULT_HEIGHT;
    }

    this.width = width;
    this.height = height;
    slate = new Slate(width, height);
    view = new SimulatorView(width, height);
    view.setOnMaterialPlacement((x, y) -> {
      slate.setMaterial(x, y, view.createSelectedMaterial(x, y));
      view.setCellMaterial(x, y, slate.getMaterial(x, y));
    });
    view.displaySlate(slate);
  }

  public SimulatorView getView() {
    return view;
  }

  public void start() {
    if (timeline == null) {
      createTimeline();
    }
    timeline.play();
  }

  public void stop() {
    if (timeline != null) {
      timeline.stop();
    }
  }

  public void setDelay(int milliseconds) {
    if (milliseconds <= 0) {
      throw new IllegalArgumentException("Delay must be positive");
    }

    boolean wasRunning = timeline != null
        && timeline.getStatus() == Animation.Status.RUNNING;

    delay = milliseconds;
    if (timeline != null) {
      timeline.stop();
      createTimeline();
      if (wasRunning) {
        timeline.play();
      }
    }
  }

  public void resetSimulation() {
    step = 0;
    slate = new Slate(width, height);
    view.displaySlate(slate);
  }

  private void createTimeline() {
    timeline = new Timeline(
        new KeyFrame(Duration.millis(delay), event -> simulateStep()));
    timeline.setCycleCount(Animation.INDEFINITE);
  }

  private void simulateStep() {
    Slate nextSlate = new Slate(slate);
    boolean[][] processed = new boolean[nextSlate.getHeight()][nextSlate.getWidth()];

    for (int y = nextSlate.getHeight() - 2; y >= 0; y--) {
      for (int x = 0; x < nextSlate.getWidth(); x++) {
        if (processed[y][x]) {
          continue;
        }

        Material material = nextSlate.getMaterial(x, y);
        if (material instanceof Solid solid && solid.isPowdery()) {
          processed[y][x] = true;
          movePowderySolid(nextSlate, x, y, solid, processed);
        } else if (material instanceof Fluid) {
          processed[y][x] = true;
          moveFluid(nextSlate, x, y, processed);
        }
      }
    }

    slate = nextSlate;
    step++;
    view.displaySlate(slate);
  }

  private void movePowderySolid(Slate slate, int x, int y, Solid solid, boolean[][] processed) {
    Material below = slate.getMaterial(x, y + 1);
    if (below == null) {
      slate.moveMaterial(x, y, x, y + 1);
      return;
    }

    if (below instanceof Fluid fluid && solid.getDensity() > fluid.getDensity()) {
      slate.swapMaterials(x, y, x, y + 1);
      processed[y + 1][x] = true;
      processed[y][x] = true;
      return;
    }

    boolean leftIsEmpty = x > 0 && slate.getMaterial(x - 1, y + 1) == null;
    boolean rightIsEmpty = x < slate.getWidth() - 1
        && slate.getMaterial(x + 1, y + 1) == null;
    int direction = chooseDirection(leftIsEmpty, rightIsEmpty);
    if (direction != 0) {
      slate.moveMaterial(x, y, x + direction, y + 1);
    }
  }

  private void moveFluid(Slate slate, int x, int y, boolean[][] processed) {
    if (slate.getMaterial(x, y + 1) == null) {
      moveAndMarkProcessed(slate, x, y, x, y + 1, processed);
      return;
    }

    boolean leftDiagonalIsEmpty = x > 0 && slate.getMaterial(x - 1, y + 1) == null;
    boolean rightDiagonalIsEmpty = x < slate.getWidth() - 1
        && slate.getMaterial(x + 1, y + 1) == null;
    int diagonalDirection = chooseDirection(leftDiagonalIsEmpty, rightDiagonalIsEmpty);
    if (diagonalDirection != 0) {
      moveAndMarkProcessed(slate, x, y, x + diagonalDirection, y + 1, processed);
      return;
    }

    boolean leftIsEmpty = x > 0 && slate.getMaterial(x - 1, y) == null;
    boolean rightIsEmpty = x < slate.getWidth() - 1 && slate.getMaterial(x + 1, y) == null;
    int horizontalDirection = chooseDirection(leftIsEmpty, rightIsEmpty);
    if (horizontalDirection != 0) {
      moveAndMarkProcessed(slate, x, y, x + horizontalDirection, y, processed);
    }
  }

  private int chooseDirection(boolean leftIsAvailable, boolean rightIsAvailable) {
    if (leftIsAvailable && rightIsAvailable) {
      return ThreadLocalRandom.current().nextBoolean() ? -1 : 1;
    }
    if (leftIsAvailable) {
      return -1;
    }
    return rightIsAvailable ? 1 : 0;
  }

  private void moveAndMarkProcessed(
      Slate slate, int fromX, int fromY, int toX, int toY, boolean[][] processed) {
    slate.moveMaterial(fromX, fromY, toX, toY);
    processed[toY][toX] = true;
  }
}