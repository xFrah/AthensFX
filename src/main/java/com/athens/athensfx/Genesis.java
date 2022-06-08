package com.athens.athensfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.ValueAxis;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Genesis extends Application { // TODO stop all threads if window is closed

    static ArrayList<Population> populations = new ArrayList<>();
    public static int selectedPopulationIndex;
    public static Population selectedPopulation = null;
    private static WindowController controller;

    @Override
    public void start(Stage stage) throws IOException, URISyntaxException {
        FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("athens-window.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        controller = fxmlLoader.getController();
        windowSetup();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void createPopulation(int a, int b, int c, double ratioMan, double ratioWoman) {
        Population p = new Population(a, b, c, ratioMan, ratioWoman, 100000, populations.size());
        populations.add(p);
        selectedPopulationIndex = populations.size() - 1;
        selectedPopulation = p;
    }

    public static void windowSetup() throws URISyntaxException {
        controller.bakeThePie();
        new WindowUpdater().start();
        PhongMaterial earthMaterial = new PhongMaterial();
        earthMaterial.setDiffuseMap(new Image(Genesis.class.getResource("earth-texture.jpg").toURI().toString()));
        controller.earth.setMaterial(earthMaterial);
        controller.earth.setRotationAxis(Rotate.Y_AXIS);
        controller.xAxis = (ValueAxis<Number>) controller.ratioChart.getXAxis();
    }

    static class WindowUpdater extends Thread {
        public static int refreshDelay = 100;

        public WindowUpdater() { super("WindowUpdater"); }

        public void run() {
            try {
                while (true) {
                    if (selectedPopulation != null) {
                        float[] info = selectedPopulation.getInfo();
                        Platform.runLater(() -> controller.setInfo(info));
                    }
                    TimeUnit.MILLISECONDS.sleep(refreshDelay);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
