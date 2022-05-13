package com.athens.athensfx;

public class Woman extends Person {

    public Woman(boolean horny, Population pop) {
        super(horny, pop, (horny) ? pop.fastWomen: pop.coyWomen);
    }

    public void update(int i) throws InterruptedException {
        switch (state) {
            case DEAD -> {
                //die(i);
                return;
            }
            case PREGNANT -> giveBirth();
            case SINGLE -> tooOld();
            case YOUNG -> tooYoung();
        }
        deathChance(i);
    }

    private void giveBirth() throws InterruptedException {
        state = Status.SINGLE;
        Pop.births.incrementAndGet();
        if (seedOfLife.nextBoolean()) {
            birth(new Man(Pop.manConvenience, Pop));
        } else {
            birth(new Woman(Pop.womanConvenience, Pop));
        }

    }


    private void birth(Man m) throws InterruptedException {
        Pop.newbornMen.put(m);
    }

    private void birth(Woman w) throws InterruptedException {
        Pop.newbornWomen.put(w);
    }

    void die(int i) throws InterruptedException {
        Pop.deadWomen.put(i);
    }

}
