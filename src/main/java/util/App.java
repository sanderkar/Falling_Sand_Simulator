package util;

import core.Simulator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
  @Override
  public void start(Stage stage) {
    Simulator simulator = new Simulator();

    stage.setTitle("Falling Sand Simulator");
    stage.setScene(new Scene(simulator.getView()));
    stage.sizeToScene();
    stage.setOnHidden(event -> simulator.stop());
    stage.show();

    simulator.start();
  }

  public static void main(String[] args) {
    launch(args);
  }
}