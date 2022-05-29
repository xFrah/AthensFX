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

    final int id;
    volatile public boolean running = true;
    volatile public boolean paused = false;
    volatile public boolean growth = true;
    volatile boolean canBirth;
    volatile transient int finished = 0;
    private int iterations = 0;
    volatile public int iterationDelay = 0;
    transient boolean manConvenience = false;
    transient boolean womanConvenience = false;
    volatile transient float lastIterationTimeCompletion;

    final ThreadLocalRandom r2 = ThreadLocalRandom.current(); // todo fix this shit
    final PeopleHolder<Man> menHolder = new PeopleHolder<>();
    final PeopleHolder<Woman> womenHolder = new PeopleHolder<>();
    final AtomicInteger philanderers = new AtomicInteger();
    final AtomicInteger faithfulMen = new AtomicInteger();
    final AtomicInteger fastWomen = new AtomicInteger();
    final AtomicInteger coyWomen = new AtomicInteger();
    public final Object pauseLock = new Object();


    public Population(int a, int b, int c, double ratioMan, double ratioWoman, int startingPopulation, int id) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.id = id;
        setupPopulation(startingPopulation, ratioMan, ratioWoman);
        new PopulationUpdaterPool().start();
    }

    private void setupPopulation(int startingPopulation, double ratioMan, double ratioWoman) { // creates the first people
        menHolder.series.setName("Men ratio");
        womenHolder.series.setName("Women ratios");
        for (int i = 0; i < ((float) startingPopulation/2)*ratioMan; i++) {menHolder.alive.add(new Man(false, this));}
        for (int i = 0; i < ((float) startingPopulation/2)*(1 - ratioMan); i++) {menHolder.alive.add(new Man(true, this));}
        for (int i = 0; i < ((float) startingPopulation/2)*ratioWoman; i++) {womenHolder.alive.add(new Woman(false, this));}
        for (int i = 0; i < ((float) startingPopulation/2)*(1 - ratioWoman); i++) {womenHolder.alive.add(new Woman(true, this));}
    }

    Woman getRandomWoman() {
        return womenHolder.alive.get(r2.nextInt(womenHolder.alive.size()));
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
                womenHolder.newborns.size() + menHolder.newborns.size(), womenHolder.dead.size() + menHolder.dead.size(),
                lastIterationTimeCompletion};
    }

    class PopulationUpdaterPool extends Thread {
        public PopulationUpdaterPool() {
            super("LifeRoutineLock-" + id);
            new LifeUpdater<>(menHolder, Population.this).start();
            new LifeUpdater<>(womenHolder, Population.this).start();
            new EquivalentExchange<>(menHolder, Population.this).start();
            new EquivalentExchange<>(womenHolder, Population.this).start();
        }

        private void updateBirthValues () {
            womanConvenience = var1 * faithfulMen.get() < var2 * faithfulMen.get() + var3 * philanderers.get(); // var3 is there for readability
            manConvenience = var1 * coyWomen.get() + var2 * fastWomen.get() < a * fastWomen.get();
            canBirth = growth || (menHolder.dead.size() + womenHolder.dead.size()) > 0;
        }

        public void run() {
            updateParameters();
            long start = 0;
            try {
                while (running) {
                    updateBirthValues();
                    if (finished == 2) {
                        lastIterationTimeCompletion = System.currentTimeMillis() - start;
                        finished = 0;
                        iterations++;
                        if (paused) {synchronized (pauseLock) { pauseLock.wait();} }
                        TimeUnit.MILLISECONDS.sleep(iterationDelay);
                        synchronized (Population.this) {
                            Population.this.notifyAll();
                            start = System.currentTimeMillis();
                        }
                    }
                }
            } catch (InterruptedException e) {
            throw new RuntimeException(e);
            }
        }
    }
}