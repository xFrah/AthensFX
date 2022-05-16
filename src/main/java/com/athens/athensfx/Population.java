package com.athens.athensfx;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Population {
    final int id;
    volatile public boolean running = true;
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
    protected volatile boolean threadManDone = false;
    protected volatile boolean threadWomanDone = false;
    protected volatile boolean manConvenience = false;
    protected volatile boolean womanConvenience = false;
    AtomicInteger philanderers = new AtomicInteger();
    AtomicInteger faithfulMen = new AtomicInteger();
    AtomicInteger fastWomen = new AtomicInteger();
    AtomicInteger coyWomen = new AtomicInteger();
    protected volatile int iterations = 0;
    protected volatile float menRatio;
    protected volatile float womenRatio;
    protected XYChart.Series<Number,Number> seriesMen = new XYChart.Series();
    protected XYChart.Series<Number,Number> seriesWomen = new XYChart.Series();
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
        new LifeRoutineMen(id).start();
        new LifeRoutineWomen(id).start();
        new EquivalentExchangeMan(id).start();
        new EquivalentExchangeWoman(id).start();
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
            while (running) {
                menRatio = (float) faithfulMen.get() / (float) (men.size()); // the convenience values are calculated here
                womenRatio = (float) coyWomen.get() / (float) (women.size()); // these are accessed by the objects in parallel
                womanConvenience = var1*menRatio < var2*menRatio + var3*(1 - menRatio); // var3 is there for readability
                manConvenience = var1*womenRatio + var2*(1 - womenRatio) < a * (1 - womenRatio);
                canBirth = (deadMen.size() + deadWomen.size()) > 0 || growth;
                // analyze(menRatio, womenRatio); // debug
                if (threadManDone && threadWomanDone) {
                    synchronized (Population.this) {
                        threadManDone = false;
                        threadWomanDone = false;
                        iterations++;
                        //analyze(menRatio, womenRatio); // debug function
                        //births.set(0); // debug
                        //deaths.set(0); // debug
                        exchangeSouls();
                        try {
                            TimeUnit.MILLISECONDS.sleep(iterationDelay);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        Population.this.notifyAll();
                    }
                }
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

    class LifeRoutineWomen extends Thread {
        public LifeRoutineWomen(int s) {super("LifeRoutineWomen-" + s);}

        public void run() {
            while (running) {
                for (int i = 0; i < women.size(); i++) {
                    try {
                        women.get(i).update(i);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                synchronized (Population.this) {
                    threadWomanDone = true;
                    try {
                        Population.this.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

    }

    class LifeRoutineMen extends Thread {
        public LifeRoutineMen(int s) {super("LifeRoutineMen-" + s);}


        public void run() {
            while (running) {
                for (int i = 0; i < men.size(); i++) {
                    try {
                        men.get(i).update(i);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                synchronized (Population.this) {
                    threadManDone = true;
                    try {
                        Population.this.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    class EquivalentExchangeMan extends Thread {
        public EquivalentExchangeMan(int s) {super("EquivalentExchangeMan-" + s);}

        public void run() {
            while (running) {
                try {
                    men.set(deadMen.take(), newbornMen.take());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    class EquivalentExchangeWoman extends Thread {
        public EquivalentExchangeWoman(int s) {super("EquivalentExchangeWoman-" + s);}

        public void run() {
            while (running) {
                try {
                    women.set(deadWomen.take(), newbornWomen.take());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}