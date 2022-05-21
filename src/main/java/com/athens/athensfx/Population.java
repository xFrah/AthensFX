package com.athens.athensfx;

import javafx.scene.chart.XYChart;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
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
    protected LinkedBlockingQueue<Man> newbornMen = new LinkedBlockingQueue<>();
    protected LinkedBlockingQueue<Woman> newbornWomen = new LinkedBlockingQueue<>();
    protected LinkedBlockingQueue<Integer> deadMen = new LinkedBlockingQueue<>();
    protected LinkedBlockingQueue<Integer> deadWomen = new LinkedBlockingQueue<>();
    protected ArrayList<Woman> women = new ArrayList<>();
    protected ArrayList<Man> men = new ArrayList<>();
    protected volatile int finished = 0;
    protected volatile boolean manConvenience = false;
    protected volatile boolean womanConvenience = false;
    AtomicInteger philanderers = new AtomicInteger();
    AtomicInteger faithfulMen = new AtomicInteger();
    AtomicInteger fastWomen = new AtomicInteger();
    AtomicInteger coyWomen = new AtomicInteger();
    protected volatile int iterations = 0;
    protected XYChart.Series<Number,Number> seriesMen = new XYChart.Series<>();
    protected XYChart.Series<Number,Number> seriesWomen = new XYChart.Series<>();
    public boolean growth = true;
    public int iterationDelay = 0;


    public Population(int a, int b, int c, double ratioMan, double ratioWoman, int startingPopulation, int id) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.id = id;
        setupPopulation(startingPopulation, ratioMan, ratioWoman);
        new LifeRoutineLock(id).start();
        new LifeUpdater<>(men, id, this).start();
        new LifeUpdater<>(women, id, this).start();
        new EquivalentExchange<>(men, newbornMen, deadMen, id, this).start();
        new EquivalentExchange<>(women, newbornWomen, deadWomen, id, this).start();
    }

    private void setupPopulation(int startingPopulation, double ratioMan, double ratioWoman) { // creates the first people

        seriesMen.setName("Men ratio");
        seriesWomen.setName("Women ratios");

        for (int i = 0; i < ((float) startingPopulation/2)*ratioMan; i++) {
            men.add(new Man(false, this));
        }
        for (int i = 0; i < ((float) startingPopulation/2)*(1 - ratioMan); i++) {
            men.add(new Man(true, this));
        }
        for (int i = 0; i < ((float) startingPopulation/2)*ratioWoman; i++) {
            women.add(new Woman(false, this));
        }
        for (int i = 0; i < ((float) startingPopulation/2)*(1 - ratioWoman); i++) {
            women.add(new Woman(true, this));
        }
    }

    void updateParameters() {
        var1 = a - (b/2) - c;
        var2 = a - (b/2);
        var3 = a - b;
    }

    public float[] getInfo() {
        return new float[]
                {iterations, men.size(), women.size(),
                philanderers.get(), faithfulMen.get(), fastWomen.get(), coyWomen.get(),
                newbornWomen.size() + newbornWomen.size(), deadWomen.size() + deadMen.size()};
    }

    class LifeRoutineLock extends Thread {
        public LifeRoutineLock(int s) {
            super("LifeRoutineLock-" + s);
        }

        public void run() {
            updateParameters();
            try {
                while (running) {
                    womanConvenience = var1 * faithfulMen.get() < var2 * faithfulMen.get() + var3 * philanderers.get(); // var3 is there for readability
                    manConvenience = var1 * coyWomen.get() + var2 * fastWomen.get() < a * fastWomen.get();
                    canBirth = (deadMen.size() + deadWomen.size()) > 0 || growth;
                    if (finished == 2) {
                        if (paused) {synchronized (pauseLock) {pauseLock.wait();}}
                        synchronized (Population.this) {
                            finished = 0;
                            iterations++;
                            exchangeSouls();
                            TimeUnit.MILLISECONDS.sleep(iterationDelay);
                            Population.this.notifyAll();
                        }
                    }
                }
            } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        }

        void exchangeSouls() {
            newbornMen.drainTo(men);
            newbornWomen.drainTo(women);
        }
    }
}