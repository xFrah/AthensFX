package com.athens.athensfx;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Person {
    enum Status {PREGNANT, YOUNG, SINGLE, OLD, DEAD}
    private int age = 0;
    final boolean horny;
    final private int deathAge;
    final protected static Random seedOfLife = new Random();
    protected volatile Status state = Status.YOUNG;
    final Population Pop;
    final AtomicInteger group;

    public Person(boolean horny, Population pop, AtomicInteger group) {
        this.Pop = pop;
        this.horny = horny;
        this.group = group;
        this.deathAge = GrimReaper.deathAge();
        increment();
    }

    abstract void update(int i) throws InterruptedException;
    abstract void die(int i) throws InterruptedException;

    void deathChance(int i) throws InterruptedException {
        if (age++ >= deathAge) { // calls decrement too many times if he dies again
            state = Status.DEAD;
            decrement();
            die(i);
        }
    }

    void tooOld() {if (age > 45) {this.state = Status.OLD;}}
    void tooYoung() {if (age == 20) {state = Status.SINGLE;}} // set to 30 and see what happens

    void increment() {group.incrementAndGet();}
    void decrement() {group.decrementAndGet();}

}