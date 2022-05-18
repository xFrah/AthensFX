package com.athens.athensfx;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Person {
    enum Status {PREGNANT, YOUNG, SINGLE, OLD, DEAD}
    private int age = 0;
    final private int deathAge;
    protected Random seedOfLife = new Random();
    volatile Status state = Status.YOUNG;
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

    void deathChance(int i) throws InterruptedException {
        if (age++ >= deathAge) {
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