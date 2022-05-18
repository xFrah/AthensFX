package com.athens.athensfx;

import java.util.ArrayList;

class LifeRoutine<S extends Person> extends Thread { // todo change to avoid duplicated code, just make a field containing the list to update
    private final ArrayList<S> list;
    final Population population;

    public LifeRoutine(ArrayList<S> list, int s, Population p) {
        super("LifeRoutine-" + s);
        this.list = list;
        this.population = p;
    }

    public void run() {
        while (population.running) {
            for (int i = 0; i < list.size(); i++) {
                try {
                    list.get(i).update(i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            synchronized (population) {
                population.finished.incrementAndGet();
                try {
                    population.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
