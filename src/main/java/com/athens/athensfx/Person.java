package com.athens.athensfx;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Person {
    // This is the person class, the parent class to Man and Woman.

    private int age = 0;
    private final int deathAge;
    protected final Population p;
    private final AtomicInteger group; // todo explain this and the methods

    public Person(Population pop, AtomicInteger group) {
        // This is the person constructor, it is called when a new population is created or when a new person is born.
        // In the constructor we also set the age of death right away.
        this.p = pop;
        this.group = group;
        this.deathAge = GrimReaper.deathAge();
        increment();
    }

    abstract void update(int i) throws InterruptedException;
    abstract void die(int i) ;
    abstract void setSingle();
    abstract void setOld();
    abstract void setDead();

    boolean deathChance(int i) {
        // This method basically makes the person die if it's their time... Life is tough.
        if (age++ >= deathAge) {
            setDead();
            decrement();
            die(i);
            return true;
        }
        return false;
    }

    void tooOld(int i) {
        // This method makes the person "inactive" if their age is too old to have a child.
        if (!deathChance(i) && age > 45) {setOld();}
    }

    void tooYoung(int i) {
        // This method makes the person "active" if their age is old enough to have a child.
        if (!deathChance(i) && age == 20) {setSingle();}
    } // set to 30 and see what happens

    void increment() { group.incrementAndGet(); }
    void decrement() { group.decrementAndGet(); }

}