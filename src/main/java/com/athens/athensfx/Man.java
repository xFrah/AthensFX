package com.athens.athensfx;

import java.util.function.BiConsumer;

public class Man extends Person {
    private static final BiConsumer<Man, Integer> single = Man::single;
    private static final BiConsumer<Man, Integer> old = Person::hasToDie;
    private static final BiConsumer<Man, Integer> dead = Person::nothing;
    private static final BiConsumer<Man, Integer> young = Person::tooYoung;
    private BiConsumer<Man, Integer> statusFunc = young;

    Man(boolean horny, Population pop) {
        super(pop, (horny) ? pop.philanderers: pop.faithfulMen);
    }

    void update(int i) {
        statusFunc.accept(this, i);
    }

    private void single(int i) {
        if (p.min > i || i > p.max) return;
        Woman woman = p.womenHolder.alive.get(i);
        if (woman.isSingle()) { woman.giveBirth(i); }
        tooOld(i);
    }

    void die(int i) { p.menHolder.dead.add(i); }

    void setSingle() { statusFunc = single; }

    void setOld() { statusFunc = old; }

    void setDead() { statusFunc = dead; }

}
