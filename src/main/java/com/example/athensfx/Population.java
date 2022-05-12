package com.example.athensfx;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Population {
    final int id;
    volatile public boolean running = true;
    final int a;
    final int b;
    final int c;
    // LinkedBlockingQueue<LifeRoutineLock.PopulationState> stateLog = new LinkedBlockingQueue<>();
    protected LinkedBlockingQueue<Man> newbornMen = new LinkedBlockingQueue<>();
    protected LinkedBlockingQueue<Woman> newbornWomen = new LinkedBlockingQueue<>();
    protected LinkedBlockingQueue<Integer> deadMen = new LinkedBlockingQueue<>();
    protected LinkedBlockingQueue<Integer> deadWomen = new LinkedBlockingQueue<>();
    protected volatile ArrayList<Woman> women = new ArrayList<>();
    protected volatile ArrayList<Man> men = new ArrayList<>();
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


    public Population(int a, int b, int c, int startingPopulation, int id) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.id = id;
        setupPopulation(startingPopulation);
        new LifeRoutineLock().start();
    }

    private void setupPopulation(int startingPopulation) { // creates the first people
        Random r = new Random();
        for (int i = 0; i < startingPopulation/2; i++) {
            women.add(new Woman(r.nextBoolean(), this));
            men.add(new Man(r.nextBoolean(), this));
        }
    }

    public float[] getInfo() {
        return new float[]
                {iterations, menRatio, womenRatio, men.size(), women.size(),
                philanderers.get(), faithfulMen.get(), fastWomen.get(), coyWomen.get(),
                newbornWomen.size() + newbornWomen.size(), deadWomen.size() + deadMen.size()};
    }

    class LifeRoutineLock extends Thread {
        public void run() {
            new LifeRoutineMen().start();
            new LifeRoutineWomen().start();
            new EquivalentExchangeMan().start();
            new EquivalentExchangeWoman().start();
            int var1 = a - (b/2) - c;
            int var2 = a - (b/2);
            int var3 = a - b;
            while (running) {
                menRatio = (float) faithfulMen.get() / (float) (men.size()); // the convenience values are calculated here
                womenRatio = (float) coyWomen.get() / (float) (women.size()); // these are accessed by the objects in parallel
                womanConvenience = var1*menRatio < var2*menRatio + var3*(1 - menRatio); // var3 is there for readability
                manConvenience = var1*womenRatio + var2*(1 - womenRatio) < a * (1 - womenRatio);
                if (threadManDone && threadWomanDone) {
                    synchronized (Population.this) {
                        threadManDone = false;
                        threadWomanDone = false;
                        analyze(menRatio, womenRatio); // debug function
                        exchangeSouls();
                        Population.this.notifyAll();
                    }
                }
            }
        }


        void analyze(float menRatio, float womenRatio) {

            System.out.println("\n---- iteration " + iterations++ + " ----" +
                    "\n- Population: " + (men.size() + women.size()) + "(" + men.size() + ", " + women.size() + ")" +
                    "\n- Normals(M, F): " + faithfulMen + ", " + coyWomen + " = " + (faithfulMen.get() + coyWomen.get()) +
                    "\n- Hornies(M, F): " + philanderers + ", " + fastWomen + " = " + (philanderers.get() + fastWomen.get()) +
                    "\n- Ratio: " + menRatio + ", " + womenRatio +
                    "\n- Dead: " + (deadMen.size() + deadWomen.size()));
        }

        void exchangeSouls() {
            // men.addAll(newbornMen);
            // women.addAll(newbornWomen);
            // System.out.println(newbornMen.size());
            // System.out.println(newbornWomen.size());
            // System.out.println((int) (((float) (deadMen.size() + deadWomen.size())/(float) totalDead + 0.00001)*100) + "%");
            newbornMen.drainTo(men);
            newbornWomen.drainTo(women);
        }
    }

    class LifeRoutineWomen extends Thread {
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