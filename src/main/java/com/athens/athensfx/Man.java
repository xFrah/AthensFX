package com.athens.athensfx;

import java.util.function.Consumer;

public class Man extends Person {
    private static final Consumer<Man> single = Man::single;
    private static final Consumer<Man> old = (man) -> {};
    private static final Consumer<Man> dead = (man) -> {};
    private static final Consumer<Man> young = Person::tooYoung;
    private Consumer<Man> statusFunc = young;

    Man(boolean horny, Population pop) {
        super(pop, (horny) ? pop.philanderers: pop.faithfulMen);
    }

    void update(int i) throws InterruptedException {
        statusFunc.accept(this);
        deathChance(i);
    }

    private void single() {
        Woman woman = p.womenHolder.getRandomPerson();
        if (woman.isSingle()) { woman.setPregnant(); }
        tooOld();
    }

    void die(int i) throws InterruptedException { p.menHolder.dead.put(i); }

    void setSingle() { statusFunc = single; }

    void setOld() { statusFunc = old; }

    void setDead() { statusFunc = dead; }

}
