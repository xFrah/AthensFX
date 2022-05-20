package com.athens.athensfx;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public abstract class Person {
    protected static Random seedOfLife = new Random();

    private int age = 0;
    final private int deathAge;
    protected final Population Pop;
    private final AtomicInteger group;

    public Person(Population pop, AtomicInteger group) {
        this.Pop = pop;
        this.group = group;
        this.deathAge = GrimReaper.deathAge();
        increment();
    }

    abstract void update(int i) throws InterruptedException;
    abstract void die(int i) throws InterruptedException;
    abstract void setSingle();
    abstract void setOld();
    abstract void setDead();

    void deathChance(int i) throws InterruptedException {
        if (age++ >= deathAge) {
            setDead();
            decrement();
            die(i);
        }
    }

    void tooOld() { if (age > 45) {setOld();} }
    void tooYoung() { if (age == 20) {setSingle();} } // set to 30 and see what happens

    void increment() { group.incrementAndGet(); }
    void decrement() { group.decrementAndGet(); }

}