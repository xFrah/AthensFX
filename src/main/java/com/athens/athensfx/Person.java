package com.athens.athensfx;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Person {

    private int age = 0;
    private final int deathAge;
    protected final Population p;
    private final AtomicInteger group;

    public Person(Population pop, AtomicInteger group) {
        this.p = pop;
        this.group = group;
        this.deathAge = GrimReaper.deathAge();
        increment();
    }

    abstract void update(int i) throws InterruptedException;
    abstract void die(int i) throws InterruptedException;
    abstract void setSingle();
    abstract void setOld();
    abstract void setDead();

    boolean deathChance(int i) {
        if (age++ >= deathAge) {
            setDead();
            decrement();
            try {
                die(i);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return false;
    }

    void tooOld(int i) {
        if (!deathChance(i) && age > 45) {setOld();}
    }

    void tooYoung(int i) {
        if (!deathChance(i) && age == 20) {setSingle();}
    } // set to 30 and see what happens

    void increment() { group.incrementAndGet(); }
    void decrement() { group.decrementAndGet(); }

}