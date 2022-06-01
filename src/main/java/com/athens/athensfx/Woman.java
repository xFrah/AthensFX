package com.athens.athensfx;

import java.util.function.BiConsumer;

public class Woman extends Person {
    private static final BiConsumer<Woman, Integer> single = Person::tooOld;
    private static final BiConsumer<Woman, Integer> old = Person::deathChance;
    private static final BiConsumer<Woman, Integer> dead = (woman, i) -> {};
    private static final BiConsumer<Woman, Integer> pregnant = Woman::giveBirth;
    private static final BiConsumer<Woman, Integer> young = Person::tooYoung;
    private BiConsumer<Woman, Integer> statusFunc = young;

    Woman(boolean horny, Population pop) {
        super(pop, (horny) ? pop.fastWomen: pop.coyWomen);
    }

    void update(int i) {
        statusFunc.accept(this, i);
    }

    void giveBirth(int i) {
        try {
            setSingle();
            if (!p.canBirth) return;
            if (p.womenHolder.randomSex()) { // menHolder?
                p.menHolder.newborns.put(new Man(p.manConvenience, p));
            } else {
                p.womenHolder.newborns.put(new Woman(p.womanConvenience, p));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean isSingle() { return statusFunc == single; }

    public void setPregnant() { statusFunc = pregnant; }

    public void setSingle() { statusFunc = single; }

    void die(int i) throws InterruptedException { p.womenHolder.dead.put(i); }

    void setOld() { statusFunc = old; }

    void setDead() { statusFunc = dead; }
}
