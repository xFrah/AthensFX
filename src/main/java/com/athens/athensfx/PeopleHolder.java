package com.athens.athensfx;

import javafx.scene.chart.XYChart;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class PeopleHolder <S extends Person> {
    private final Population.PopulationUpdaterLock updaterPool;
    final LinkedBlockingQueue<Integer> dead = new LinkedBlockingQueue<>();
    final XYChart.Series<Number,Number> series = new XYChart.Series<>();
    final ArrayList<S> alive = new ArrayList<>();
    private boolean started = false;
    public ThreadLocalRandom tlr;

    public PeopleHolder(Population.PopulationUpdaterLock pool) { this.updaterPool = pool; }

    void startThreads() {
        if (!started) {
            started = true;
            new PeopleUpdater().start();
        } else {
            System.out.println("fuck off");
        }
    }

    synchronized void newSoul (S soul) {
        if (dead.isEmpty()) {
            alive.add(soul);
        } else {
            alive.set(dead.poll(), soul);
        }
    }

    public boolean randomSex () {
        return tlr.nextBoolean();
    }

    class PeopleUpdater extends Thread {
        PeopleUpdater () { tlr = ThreadLocalRandom.current(); } // is this efficient or not?

        public void run() {
            try {
                while (updaterPool.running) {
                    for (int i = 0; i < alive.size(); i++) {
                        alive.get(i).update(i);
                    }
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
}
