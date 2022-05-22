package com.athens.athensfx;

import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class PeopleHolder <S extends Person> {
    protected LinkedBlockingQueue<S> newborns = new LinkedBlockingQueue<>();
    protected LinkedBlockingQueue<Integer> dead = new LinkedBlockingQueue<>();
    protected XYChart.Series<Number,Number> series = new XYChart.Series<>();
    protected ArrayList<S> alive = new ArrayList<>();

    void exchangeSouls() {
        newborns.drainTo(alive);
    }
}
