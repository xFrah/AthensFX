package com.athens.athensfx;

import java.util.function.BiConsumer;

public class Woman extends Person {
    private static final BiConsumer<Woman, Integer> single = Person::tooOld;
    private static final BiConsumer<Woman, Integer> old = Person::deathChance;
    private static final BiConsumer<Woman, Integer> dead = Person::nothing;
    private static final BiConsumer<Woman, Integer> young = Person::tooYoung;
    private BiConsumer<Woman, Integer> statusFunc = young;

    Woman(boolean horny, Population pop) {
        // This is the constructor for woman.
        super(pop, (horny) ? pop.fastWomen: pop.coyWomen);
    }

    void update(int i) {
        // todo comment this and explain what biConsumer is... actually explain the whole thing
        statusFunc.accept(this, i);
    }


    void giveBirth(int i) {
        // This method creates a new human being. It is called by
        setSingle();
        if (p.womenHolder.randomSex()) { // menHolder?
            p.menHolder.newSoul(new Man(p.manConvenience, p));
        } else {
            p.womenHolder.newSoul(new Woman(p.womanConvenience, p));
        }
    }

    public boolean isSingle() { return statusFunc == single; }

    public void setSingle() { statusFunc = single; }

    void die(int i) { p.womenHolder.dead.add(i); }

    void setOld() { statusFunc = old; }

    void setDead() { statusFunc = dead; }
}
