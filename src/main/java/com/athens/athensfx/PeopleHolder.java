package com.athens.athensfx;

import javafx.scene.chart.XYChart;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class PeopleHolder <S extends Person> {
    protected final LinkedBlockingQueue<S> newborns = new LinkedBlockingQueue<>();
    protected final LinkedBlockingQueue<Integer> dead = new LinkedBlockingQueue<>();
    protected final XYChart.Series<Number,Number> series = new XYChart.Series<>();
    protected final ArrayList<S> alive = new ArrayList<>();

    void exchangeSouls() {
        newborns.drainTo(alive);
    }
}
