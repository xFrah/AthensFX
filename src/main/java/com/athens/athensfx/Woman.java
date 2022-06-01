package com.athens.athensfx;

import java.util.function.Consumer;

public class Woman extends Person {
    private static final Consumer<Woman> single = Woman::tooOld;
    private static final Consumer<Woman> old = (woman) -> {};
    private static final Consumer<Woman> dead = (woman) -> {};
    private static final Consumer<Woman> pregnant = Woman::giveBirth;
    private static final Consumer<Woman> young = Person::tooYoung;
    private Consumer<Woman> statusFunc = young;

    Woman(boolean horny, Population pop) {
        super(pop, (horny) ? pop.fastWomen: pop.coyWomen);
    }

    void update(int i) throws InterruptedException {
        statusFunc.accept(this);
        deathChance(i);
    }

    void giveBirth() {
        try {
            setSingle();
            if (!p.canBirth) return;
            if (p.menHolder.randomSex()) {
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
