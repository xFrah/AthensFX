package com.athens.athensfx;

import java.util.ArrayList;

class LifeRoutine<S extends Person> extends Thread {
    private final ArrayList<S> list;
    private final Population population;

    public LifeRoutine(ArrayList<S> list, int s, Population p) {
        super("LifeRoutine-" + s);
        this.list = list;
        this.population = p;
    }

    public void run() {
        try {
            while (population.running) {
                if (population.paused) {
                    synchronized (population.pauseLock) {population.pauseLock.wait();}
                }
                for (int i = 0; i< list.size(); i++) {
                    list.get(i).update(i);
                }
                synchronized (population) {
                    population.finished.incrementAndGet();
                    population.wait();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
