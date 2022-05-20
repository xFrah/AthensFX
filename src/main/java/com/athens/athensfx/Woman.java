package com.athens.athensfx;

import java.util.function.Consumer;

public class Woman extends Person {
    static Consumer<Woman> single = Woman::tooOld;
    static Consumer<Woman> old = (woman) -> {};
    static Consumer<Woman> dead = (woman) -> {};
    static Consumer<Woman> pregnant = Woman::giveBirth;
    static Consumer<Woman> young = Person::tooYoung;
    private Consumer<Woman> statusFunc = young;

    public Woman(boolean horny, Population pop) {
        super(pop, (horny) ? pop.fastWomen: pop.coyWomen);
    }

    public void update(int i) throws InterruptedException {
        statusFunc.accept(this);
        deathChance(i);
    }

    void giveBirth() {
        try {
            setSingle();
            if (!Pop.canBirth) return;
            if (seedOfLife.nextBoolean()) {
                birth(new Man(Pop.manConvenience, Pop));
            } else {
                birth(new Woman(Pop.womanConvenience, Pop));
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean isSingle() { return statusFunc == single; }

    private void birth(Man m) throws InterruptedException { Pop.newbornMen.put(m); }

    private void birth(Woman w) throws InterruptedException { Pop.newbornWomen.put(w); }

    void die(int i) throws InterruptedException { Pop.deadWomen.put(i); }

    public void setSingle() { statusFunc = single; }

    void setOld() { statusFunc = old; }

    void setPregnant() { statusFunc = pregnant; }

    void setDead() { statusFunc = dead; }
}
