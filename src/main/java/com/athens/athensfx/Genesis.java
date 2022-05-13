package com.athens.athensfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Genesis extends Application { // TODO stop all threads if window is closed

    static ArrayList<Population> populations = new ArrayList<>();
    public static int selectedPopulationIndex;
    public static Population selectedPopulation = null;
    private static WindowController controller;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("athens-window.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        controller = fxmlLoader.getController();
        controller.bakeThePie();
        new WindowUpdater("WindowUpdater").start();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void createPopulation(int a, int b, int c) {
        Population p = new Population(a, b, c, 1000, populations.size());
        populations.add(p);
        selectedPopulationIndex = populations.size() - 1;
        selectedPopulation = p;
    }

    class WindowUpdater extends Thread {
        public static int refreshDelay = 100;

        public WindowUpdater(String name) {
            super(name);
        }

        public void run() {
            while (true) {
                if (selectedPopulation != null) {
                    Platform.runLater(() -> controller.setInfo(Genesis.selectedPopulation.getInfo()));
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(refreshDelay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}