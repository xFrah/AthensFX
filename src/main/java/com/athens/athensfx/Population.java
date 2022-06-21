package com.athens.athensfx;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Population {
    int a;
    int b;
    int c;
    int var1;
    int var2;
    int var3;


    volatile transient int max = 0;
    volatile transient int min = 0;
    final int id;
    private int iterations = 0;
    transient boolean manConvenience = false;
    transient boolean womanConvenience = false;
    volatile transient float lastIterationTimeCompletion;

    volatile double copulatingRatio = 0.30;

    final Pauser pauser = new Pauser(this);
    private final PopulationUpdaterLock pool = new PopulationUpdaterLock();
    final PeopleHolder<Man> menHolder = new PeopleHolder<>(pool);
    final PeopleHolder<Woman> womenHolder = new PeopleHolder<>(pool);
    final AtomicInteger philanderers = new AtomicInteger();
    final AtomicInteger faithfulMen = new AtomicInteger();
    final AtomicInteger fastWomen = new AtomicInteger();
    final AtomicInteger coyWomen = new AtomicInteger();
    final Object pauseLock = new Object();


    public Population(int a, int b, int c, double ratioMan, double ratioWoman, int startingPopulation, int id) {
        // This is the population constructor. It is called by createPopulation in Genesis and takes in all the parameters
        // the user set up during the creation of the population.
        this.a = a;
        this.b = b;
        this.c = c;
        this.id = id;
        setupPopulation(startingPopulation, ratioMan, ratioWoman);
        pool.start();
    }

    private void setupPopulation(int startingPopulation, double ratioMan, double ratioWoman) {
        // This method is called by the constructor, and it crates the first people according to the settings the user
        // chose for the simulation.
        menHolder.series.setName("Men ratio");
        womenHolder.series.setName("Women ratio");
        for (int i = 0; i < ((float) startingPopulation/2)*ratioMan; i++) {menHolder.alive.add(new Man(false, this));}
        for (int i = 0; i < ((float) startingPopulation/2)*(1 - ratioMan); i++) {menHolder.alive.add(new Man(true, this));}
        for (int i = 0; i < ((float) startingPopulation/2)*ratioWoman; i++) {womenHolder.alive.add(new Woman(false, this));}
        for (int i = 0; i < ((float) startingPopulation/2)*(1 - ratioWoman); i++) {womenHolder.alive.add(new Woman(true, this));}
    }

    public void stop() {
        pool.running = false;
    }

    void updateParameters() {
        // This method updates the three variables that are actually used to compute Men and Women convenience.
        // We just compute and store these directly for readability and ease of maintenance.
        var1 = a - (b/2) - c;
        var2 = a - (b/2);
        var3 = a - b;
    }

    public float[] getInfo() {
        // This method gives all the info about the population. It is called by populationReload in WindowController
        // and is used to update the values that are shown on the charts.
        return new float[]
                {iterations, menHolder.alive.size(), womenHolder.alive.size(),
                 philanderers.get(), faithfulMen.get(), fastWomen.get(), coyWomen.get(),
                 womenHolder.dead.size() + menHolder.dead.size(), lastIterationTimeCompletion};
    }

    public void setCopulatingRatio(double copulatingRatio) {
        this.copulatingRatio = copulatingRatio;
    }

    class PopulationUpdaterLock extends Thread {
        // todo what does this class do
        volatile int iterationDelay = 0;
        volatile boolean paused = false;
        volatile boolean running = true;
        volatile transient int finished = 0; // todo write the different values of finished and what they are for
        private final ThreadLocalRandom tlr = ThreadLocalRandom.current();

        PopulationUpdaterLock () {
            super("PopulationUpdaterLock");
        }


        private void updateBirthValues () {
            // This computes how the men and women should become when they are born.
            // The percentages of the people from one sex influence the ones from the other sex, so they will reach an
            // equilibrium only if a,b, and c are suited to keep the population going.
            // (the three variables are actually what impacts the person's gain the most)
            // This method also decides whether a new person can be born or not,
            // according to the growth toggle switch setting.
            womanConvenience = var1 * faithfulMen.get() < var2 * faithfulMen.get() + var3 * philanderers.get();
            manConvenience = var1 * coyWomen.get() + var2 * fastWomen.get() < a * fastWomen.get();
        }

        private void randomInterval() {
            int sizeMan = menHolder.alive.size();
            int sizeWoman = womenHolder.alive.size();
            int lower = Math.min(sizeWoman, sizeMan);
            int span = (int) ((lower/2) * copulatingRatio);
            max = tlr.nextInt(span, lower/2);
            min = max - span;
        }

        public void run() {
            updateParameters();
            randomInterval();
            menHolder.startThreads();
            womenHolder.startThreads();
            long start = System.currentTimeMillis();
            try {
                while (running) {
                    updateBirthValues(); // every iteration update the birth values.
                    if (finished == 2) {
                        lastIterationTimeCompletion = System.currentTimeMillis() - start;
                        // System.out.println("(" + min + ", " + max + ") = " + (max - min));
                        finished = 0;
                        iterations++;
                        randomInterval();
                        // if the population is paused, make the thread wait until it's resumed.
                        if (paused) {synchronized (pauseLock) { pauseLock.wait();} }
                        TimeUnit.MILLISECONDS.sleep(iterationDelay); // sleep for "delay" milliseconds
                        synchronized (this) {
                            // todo explain what this does
                            start = System.currentTimeMillis();
                            notifyAll();
                        }
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // getters and setters to increase readability and get stuff to show on the window
    public int getIterationDelay() { return pool.iterationDelay; }

    public boolean isPaused() { return pool.paused; }

    public void setPaused(boolean b) { pool.paused = b; }

    public void setIterationDelay(int value) { pool.iterationDelay = value; }

}