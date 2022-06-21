package com.athens.athensfx;

import java.util.concurrent.atomic.AtomicInteger;

/** This is the person class, the parent class to Man and Woman. */
public abstract class Person {

    private int age = 0;
    private final int deathAge;
    protected final Population p;
    private final AtomicInteger group;

    /** This is the person constructor, it is called when a new population is created or when a new person is born.
    * In the constructor we also set the age of death right away. */
    public Person(Population pop, AtomicInteger group) {
        this.p = pop;
        this.group = group;
        this.deathAge = GrimReaper.getDeathAge();
        increment();
    }

    abstract void update(int i) throws InterruptedException;
    abstract void die(int i) ;
    abstract void setSingle();
    abstract void setOld();
    abstract void setDead();

    /** This method basically makes the person die if it's their time... Life is tough. */
    boolean hasToDie(int i) {
        if (age++ >= deathAge) {
            setDead();
            decrement();
            die(i);
            return true;
        }
        return false;
    }

    void nothing(int i) {}

    /** This method makes the person "inactive" if their age is too old to have a child. */
    void tooOld(int i) {
        if (!hasToDie(i) && age > 45) {setOld();}
    }

    /** This method makes the person "active" if their age is old enough to have a child.  */
    void tooYoung(int i) {
        if (!hasToDie(i) && age == 20) {setSingle();}
    }

    void increment() { group.incrementAndGet(); }
    void decrement() { group.decrementAndGet(); }

}