package com.athens.athensfx;

import java.util.function.BiConsumer;

public class Man extends Person {
    private static final BiConsumer<Man, Integer> single = Man::single;
    private static final BiConsumer<Man, Integer> old = Person::deathChance;
    private static final BiConsumer<Man, Integer> dead = (man, i) -> {};
    private static final BiConsumer<Man, Integer> young = Person::tooYoung;
    private BiConsumer<Man, Integer> statusFunc = young;

    Man(boolean horny, Population pop) {
        super(pop, (horny) ? pop.philanderers: pop.faithfulMen);
    }

    void update(int i) {
        statusFunc.accept(this, i);
    }

    private void single(int i) {
        Woman woman = p.womenHolder.getRandomPerson();
        if (woman.isSingle()) { woman.setPregnant(); }
        tooOld(i);
    }

    void die(int i) throws InterruptedException { p.menHolder.dead.put(i); }

    void setSingle() { statusFunc = single; }

    void setOld() { statusFunc = old; }

    void setDead() { statusFunc = dead; }

}
