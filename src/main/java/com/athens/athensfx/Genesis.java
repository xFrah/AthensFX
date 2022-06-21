package com.athens.athensfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.ValueAxis;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Genesis extends Application {

    /** This is where all the populations are stored. */
    static ArrayList<Population> populations = new ArrayList<>();

    /** Index of the currently selected population (the one showed on the screen). */
    public static int selectedPopulationIndex;

    /** Selected population object. */
    public static Population selectedPopulation = null;

    /** window controller. */
    private static WindowController controller;

    /** This method sets up the window. */
    @Override
    public void start(Stage stage) throws IOException, URISyntaxException {
        FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("athens-window.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("AthensFX v1.1");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
        controller = fxmlLoader.getController();
        windowSetup();
    }

    /** This method does further window setup
    * for example, starts the window updater thread. */
    public static void windowSetup() throws URISyntaxException {
        controller.bakeThePie();
        new WindowUpdater().start();
        PhongMaterial earthMaterial = new PhongMaterial();
        earthMaterial.setDiffuseMap(new Image(Genesis.class.getResource("earth-texture.jpg").toURI().toString()));
        controller.earth.setMaterial(earthMaterial);
        controller.earth.setRotationAxis(Rotate.Y_AXIS);
        controller.xAxis = (ValueAxis<Number>) controller.ratioChart.getXAxis();
        controller.xAxis2 = (ValueAxis<Number>) controller.memoryChart.getXAxis();
        controller.memoryChart.getData().add(controller.memorySeries);
        controller.memorySeries.setName("Memory Used");
    }

    public static void main(String[] args) {
        launch();
    }

    /** This method creates a new population, and it's called by the onCreateNew method in WindowController
    * The onCreateNew method passes all the parameters that the user put in during the configuration. */
    public static void createPopulation(int a, int b, int c, double ratioMan, double ratioWoman, int startingPopulation) {
        Population p = new Population(a, b, c, ratioMan, ratioWoman, startingPopulation, populations.size());
        populations.add(p);
        selectedPopulationIndex = populations.size() - 1;
        selectedPopulation = p;
    }


    /** This is the window updater thread that refreshes the window showing updated info on the population. */
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
