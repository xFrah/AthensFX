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
    volatile boolean canBirth;
    private int iterations = 0;
    transient boolean manConvenience = false;
    transient boolean womanConvenience = false;
    volatile transient float lastIterationTimeCompletion;

    final Stopper stopper = new Stopper(this);
    private final PopulationUpdaterLock pool = new PopulationUpdaterLock();
    final PeopleHolder<Man> menHolder = new PeopleHolder<>(pool);
    final PeopleHolder<Woman> womenHolder = new PeopleHolder<>(pool);
    final AtomicInteger philanderers = new AtomicInteger();
    final AtomicInteger faithfulMen = new AtomicInteger();
    final AtomicInteger fastWomen = new AtomicInteger();
    final AtomicInteger coyWomen = new AtomicInteger();
    final Object pauseLock = new Object();


    public Population(int a, int b, int c, double ratioMan, double ratioWoman, int startingPopulation, int id) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.id = id;
        setupPopulation(startingPopulation, ratioMan, ratioWoman);
        pool.start();
    }

    private void setupPopulation(int startingPopulation, double ratioMan, double ratioWoman) { // creates the first people
        menHolder.series.setName("Men ratio");
        womenHolder.series.setName("Women ratios");
        for (int i = 0; i < ((float) startingPopulation/2)*ratioMan; i++) {menHolder.alive.add(new Man(false, this));}
        for (int i = 0; i < ((float) startingPopulation/2)*(1 - ratioMan); i++) {menHolder.alive.add(new Man(true, this));}
        for (int i = 0; i < ((float) startingPopulation/2)*ratioWoman; i++) {womenHolder.alive.add(new Woman(false, this));}
        for (int i = 0; i < ((float) startingPopulation/2)*(1 - ratioWoman); i++) {womenHolder.alive.add(new Woman(true, this));}
    }

    void updateParameters() {
        var1 = a - (b/2) - c;
        var2 = a - (b/2);
        var3 = a - b;
    }

    public float[] getInfo() {
        return new float[]
                {iterations, menHolder.alive.size(), womenHolder.alive.size(),
                 philanderers.get(), faithfulMen.get(), fastWomen.get(), coyWomen.get(),
                 womenHolder.dead.size() + menHolder.dead.size(), lastIterationTimeCompletion};
    }

    class PopulationUpdaterLock extends Thread {
        volatile int iterationDelay = 0;
        volatile boolean paused = false;
        volatile boolean growth = true;
        volatile boolean running = true;
        volatile transient int finished = 0;
        private final ThreadLocalRandom tlr = ThreadLocalRandom.current();

        private void updateBirthValues () {
            womanConvenience = var1 * faithfulMen.get() < var2 * faithfulMen.get() + var3 * philanderers.get(); // var3 is there for readability
            manConvenience = var1 * coyWomen.get() + var2 * fastWomen.get() < a * fastWomen.get();
            canBirth = growth || (menHolder.dead.size() + womenHolder.dead.size()) > 0;
        }

        private void randomOffset() {
            int sizeMan = menHolder.alive.size();
            int sizeWoman = womenHolder.alive.size();
            int lower = Math.min(sizeWoman, sizeMan);
            int span = lower/4;
            max = tlr.nextInt(span, lower/2);
            min = max - span;

            //System.out.println(iterations + ": " + offset + "(" + (sizeWoman - sizeMan) + " = " + sizeWoman + " - " + sizeMan + "), " + births);
        }

        public void run() {
            updateParameters();
            randomOffset();
            menHolder.startThreads();
            womenHolder.startThreads();
            long start = System.currentTimeMillis();
            try {
                while (running) {
                    updateBirthValues();
                    if (finished == 2) {
                        lastIterationTimeCompletion = System.currentTimeMillis() - start;
                        // System.out.println("(" + min + ", " + max + ") = " + (max - min));
                        finished = 0;
                        iterations++;
                        randomOffset();
                        if (paused) {synchronized (pauseLock) { pauseLock.wait();} }
                        TimeUnit.MILLISECONDS.sleep(iterationDelay);
                        synchronized (this) {
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

    public boolean isGrowing() { return pool.growth; }

    public int getIterationDelay() { return pool.iterationDelay; }

    public boolean isPaused() { return pool.paused; }

    public void setPaused(boolean b) { pool.paused = b; }

    public void setGrowth(boolean b) { pool.growth = b; }

    public void setIterationDelay(int value) { pool.iterationDelay = value; }

}