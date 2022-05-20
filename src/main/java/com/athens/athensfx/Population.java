package com.athens.athensfx;

import javafx.scene.chart.LineChart;
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
    protected AtomicInteger finished = new AtomicInteger();
    protected volatile boolean manConvenience = false;
    protected volatile boolean womanConvenience = false;
    AtomicInteger philanderers = new AtomicInteger();
    AtomicInteger faithfulMen = new AtomicInteger();
    AtomicInteger fastWomen = new AtomicInteger();
    AtomicInteger coyWomen = new AtomicInteger();
    protected volatile int iterations = 0;
    protected volatile float menRatio;
    protected volatile float womenRatio;
    protected volatile XYChart.Series<Number,Number> seriesMen = new XYChart.Series<>();
    protected volatile XYChart.Series<Number,Number> seriesWomen = new XYChart.Series<>();
    public boolean growth = true;
    AtomicInteger births = new AtomicInteger();
    AtomicInteger deaths = new AtomicInteger();
    public int iterationDelay = 0;


    public Population(int a, int b, int c, double ratioMan, double ratioWoman, int startingPopulation, int id) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.id = id;
        setupPopulation(startingPopulation, ratioMan, ratioWoman);
        new LifeRoutineLock(id).start();
        new LifeRoutine<>(men, id, this).start();
        new LifeRoutine<>(women, id, this).start();
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
        seriesMen.getData().add(new LineChart.Data<>(seriesMen.getData().size(), menRatio));
        seriesWomen.getData().add(new LineChart.Data<>(seriesWomen.getData().size(), womenRatio));
        return new float[]
                {iterations, menRatio, womenRatio, men.size(), women.size(),
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
                while (running) { // todo casting to float could be a fucking bad idea
                    menRatio = (float) faithfulMen.get() / (float) (men.size()); // the convenience values are calculated here
                    womenRatio = (float) coyWomen.get() / (float) (women.size()); // these are accessed by the objects in parallel
                    womanConvenience = var1 * menRatio < var2 * menRatio + var3 * (1 - menRatio); // var3 is there for readability
                    manConvenience = var1 * womenRatio + var2 * (1 - womenRatio) < a * (1 - womenRatio);
                    canBirth = (deadMen.size() + deadWomen.size()) > 0 || growth;
                    // analyze(menRatio, womenRatio); // debug
                    if (finished.get() == 2) {
                        if (paused) {synchronized (pauseLock) {pauseLock.wait();}}
                        synchronized (Population.this) {
                            finished.set(0);
                            iterations++;
                            //analyze(menRatio, womenRatio); // debug function
                            //births.set(0); // debug
                            //deaths.set(0); // debug
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

        void analyze(float menRatio, float womenRatio) {

            System.out.println("\n---- iteration " + iterations + " ----" +
                    "\n- Population: " + (men.size() + women.size()) + "(" + men.size() + ", " + women.size() + ")" +
                    "\n- Normals(M, F): " + faithfulMen + ", " + coyWomen + " = " + (faithfulMen.get() + coyWomen.get()) +
                    "\n- Hornies(M, F): " + philanderers + ", " + fastWomen + " = " + (philanderers.get() + fastWomen.get()) +
                    "\n- Ratio: " + menRatio + ", " + womenRatio +
                    "\n- Dead: " + (deaths.get()) +
                    "\n- DeathQueue: " + (deadMen.size() + deadWomen.size()) +
                    "\n- Growth: " + growth +
                    "\n- Births: " + (births.get()) +
                    "\n- BirthQueue: " + (newbornWomen.size() + newbornMen.size()) +
                    "\n- canGiveBirth: " + canBirth);
        }

        void exchangeSouls() {
            newbornMen.drainTo(men);
            newbornWomen.drainTo(women);
        }
    }
}