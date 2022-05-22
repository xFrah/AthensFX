package com.athens.athensfx;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Population {
    final int id;
    volatile public boolean running = true;
    volatile public boolean paused = false;
    public final Object pauseLock = new Object();
    ThreadLocalRandom r2 = ThreadLocalRandom.current();
    int a;
    int b;
    int c;
    int var1;
    int var2;
    int var3;
    public boolean canBirth;
    protected volatile int finished = 0;
    PeopleHolder<Man> menHolder = new PeopleHolder<>();
    PeopleHolder<Woman> womenHolder = new PeopleHolder<>();
    volatile boolean manConvenience = false;
    volatile boolean womanConvenience = false;
    AtomicInteger philanderers = new AtomicInteger();
    AtomicInteger faithfulMen = new AtomicInteger();
    AtomicInteger fastWomen = new AtomicInteger();
    AtomicInteger coyWomen = new AtomicInteger();
    protected volatile int iterations = 0;
    public boolean growth = true;
    public int iterationDelay = 0;


    public Population(int a, int b, int c, double ratioMan, double ratioWoman, int startingPopulation, int id) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.id = id;
        setupPopulation(startingPopulation, ratioMan, ratioWoman);
        new PopulationUpdaterPool(id).start();
        new LifeUpdater<>(menHolder, this).start();
        new LifeUpdater<>(womenHolder, this).start();
        new EquivalentExchange<>(menHolder, this).start();
        new EquivalentExchange<>(womenHolder, this).start();
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

    public float[] getInfo() { // todo let setInfo access directly these info
        return new float[]
                {iterations, menHolder.alive.size(), womenHolder.alive.size(),
                philanderers.get(), faithfulMen.get(), fastWomen.get(), coyWomen.get(),
                womenHolder.newborns.size() + menHolder.newborns.size(), womenHolder.dead.size() + menHolder.dead.size()};
    }

    class PopulationUpdaterPool extends Thread {
        public PopulationUpdaterPool(int s) {
            super("LifeRoutineLock-" + s);
        }

        private void updateBirthValues () {
            womanConvenience = var1 * faithfulMen.get() < var2 * faithfulMen.get() + var3 * philanderers.get(); // var3 is there for readability
            manConvenience = var1 * coyWomen.get() + var2 * fastWomen.get() < a * fastWomen.get();
            canBirth = (menHolder.dead.size() + womenHolder.dead.size()) > 0 || growth;
        }

        public void run() {
            updateParameters();
            try {
                while (running) {
                    updateBirthValues();
                    if (finished == 2) {
                        if (paused) {synchronized (pauseLock) {pauseLock.wait();}}
                        synchronized (Population.this) {
                            finished = 0;
                            iterations++;
                            menHolder.exchangeSouls();
                            womenHolder.exchangeSouls();
                            TimeUnit.MILLISECONDS.sleep(iterationDelay);
                            Population.this.notifyAll();
                        }
                    }
                }
            } catch (InterruptedException e) {
            throw new RuntimeException(e);
            }
        }
    }
}