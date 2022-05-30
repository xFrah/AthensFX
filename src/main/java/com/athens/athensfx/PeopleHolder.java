package com.athens.athensfx;

import javafx.scene.chart.XYChart;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class PeopleHolder <S extends Person> {
    private final Population.PopulationUpdaterLock updaterPool;
    final LinkedBlockingQueue<S> newborns = new LinkedBlockingQueue<>();
    final LinkedBlockingQueue<Integer> dead = new LinkedBlockingQueue<>();
    final XYChart.Series<Number,Number> series = new XYChart.Series<>();
    final ArrayList<S> alive = new ArrayList<>();
    private boolean started = false;

    public PeopleHolder(Population.PopulationUpdaterLock pool) { this.updaterPool = pool; }

    void exchangeSouls() {
        newborns.drainTo(alive);
    }

    void startThreads() {
        if (!started) {
            started = true;
            new PeopleUpdater().start();
            new EquivalentExchange().start();
        } else {
            System.out.println("fuck off");
        }
    }

    class PeopleUpdater extends Thread {

        public void run() {
            try {
                while (updaterPool.running) {
                    for (int i = 0; i < alive.size(); i++) {
                        alive.get(i).update(i);
                    }
                    exchangeSouls();
                    synchronized (updaterPool) {
                        updaterPool.finished++;
                        updaterPool.wait();
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class EquivalentExchange extends Thread {

        public void run() {
            try {
                while (updaterPool.running) { // put an interrupt instead
                    alive.set(dead.take(), newborns.take());
                } // efficiency?
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
