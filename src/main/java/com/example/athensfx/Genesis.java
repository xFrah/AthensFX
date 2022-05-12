package com.example.athensfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Genesis extends Application {

    static ArrayList<Population> populations = new ArrayList<>();
    public static int selectedPopulationIndex;
    public static Population selectedPopulation = null;
    private static WindowController controller;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Genesis.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        controller = fxmlLoader.getController();
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
        public WindowUpdater(String name) {
            super(name);
        }

        public void run() {
            while (true) {
                if (selectedPopulation != null) {
                    float[] asd = Genesis.selectedPopulation.getInfo();
                    controller.setInfo(asd);
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}