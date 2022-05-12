package com.example.athensfx;

public class Man extends Person {

    public Man(boolean horny, Population pop) {
        super(horny, pop, (horny) ? pop.philanderers: pop.faithfulMen);
    }

    public void update(int i) throws InterruptedException {
        switch (state) {
            case DEAD -> {
                //die(i);
                return;
            }
            case YOUNG -> tooYoung();
            case SINGLE -> {
                woo();
                tooOld();
            }
        }
        deathChance(i);
    }

    private Woman getRandomWoman() {
        return Pop.women.get(seedOfLife.nextInt(Pop.women.size()));
    }

    void woo() {
        Woman woman = getRandomWoman();
        if (woman.state == Status.SINGLE && !(horny && !woman.horny)) { // second condition is removable
            woman.state = Status.PREGNANT;
        }
    }

    void die(int i) throws InterruptedException {
        Pop.deadMen.put(i);
    }

}
