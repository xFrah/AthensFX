package com.athens.athensfx;

import java.util.function.Consumer;

public class Man extends Person {
    static Consumer<Man> single = Man::single;
    static Consumer<Man> old = (man) -> {};
    static Consumer<Man> dead = (man) -> {};
    static Consumer<Man> young = Person::tooYoung;
    Consumer<Man> statusFunc = young;

    public Man(boolean horny, Population pop) {
        super(pop, (horny) ? pop.philanderers: pop.faithfulMen);
    }

    public void update(int i) throws InterruptedException {
        statusFunc.accept(this);
        deathChance(i);
    }

    private Woman getRandomWoman() {
        return Pop.women.get(Pop.r2.nextInt(Pop.women.size()));
    }

    void woo() {
        Woman woman = getRandomWoman(); // !(horny && !woman.horny)
        if (woman.statusFunc == Woman.single) { // second condition is removable
            woman.setPregnant();
        }
    }

    void die(int i) throws InterruptedException {
        Pop.deadMen.put(i);
    }

    void single() {
        woo();
        tooOld();
    }

    void setSingle() {
        statusFunc = single;
    }

    void setOld() {
        statusFunc = old;
    }

    public void setDead() {
        statusFunc = dead;
    }

}
