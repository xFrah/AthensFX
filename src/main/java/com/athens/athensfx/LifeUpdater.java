package com.athens.athensfx;

import java.util.ArrayList;

class LifeUpdater<S extends Person> extends Thread {
    private final ArrayList<S> list;
    private final Population population;

    public LifeUpdater(PeopleHolder<S> holder, Population p) {
        super("LifeRoutine-" + p.id);
        this.list = holder.alive;
        this.population = p;
    }

    public void run() {
        try {
            while (population.running) {
                for (int i = 0; i< list.size(); i++) {
                    list.get(i).update(i);
                }

                synchronized (population) {
                    population.finished++;
                    population.wait();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
