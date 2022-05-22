package com.athens.athensfx;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

class EquivalentExchange<S extends Person> extends Thread {
    private final LinkedBlockingQueue<S> newborns;
    private final ArrayList<S> list;
    private final LinkedBlockingQueue<Integer> dead;
    Population population;

    public EquivalentExchange(PeopleHolder<S> holder, Population p) {
        super("EquivalentExchange-" + p.id);
        this.newborns = holder.newborns;
        this.dead = holder.dead;
        this.list = holder.alive;
        this.population = p;
    }

    public void run() {
        try {
            while (population.running) { // put an interrupt instead
                list.set(dead.take(), newborns.take());
            } // efficiency?
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}