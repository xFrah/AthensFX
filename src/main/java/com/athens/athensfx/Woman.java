package com.athens.athensfx;

import java.util.function.BiConsumer;

public class Woman extends Person {
    private static final BiConsumer<Woman, Integer> single = Person::tooOld;
    private static final BiConsumer<Woman, Integer> old = Person::hasToDie;
    private static final BiConsumer<Woman, Integer> dead = Person::nothing;
    private static final BiConsumer<Woman, Integer> young = Person::tooYoung;
    private BiConsumer<Woman, Integer> statusFunc = young;

    /** This is the constructor for woman. */
    Woman(boolean horny, Population pop) {
        super(pop, (horny) ? pop.fastWomen: pop.coyWomen);
    }

    /** This method runs the currentStatus function,
    * i.e the BiConsumer that is currently set on statusFunc. */
    void update(int i) {
        statusFunc.accept(this, i);
    }

    /** This method creates a new human being. It is called by setSingle(). */
    void giveBirth(int i) {
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
