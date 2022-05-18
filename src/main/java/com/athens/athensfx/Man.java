package com.athens.athensfx;

public class Man extends Person {

    public Man(boolean horny, Population pop) {
        super(pop, (horny) ? pop.philanderers: pop.faithfulMen);
    }

    public void update(int i) throws InterruptedException {
        switch (state) {
            case DEAD -> {return;}
            case YOUNG -> tooYoung();
            case SINGLE -> {
                woo();
                tooOld();
            }
        }
        deathChance(i);
    }

    private Woman getRandomWoman() {
        return Pop.women.get(Pop.r2.nextInt(Pop.women.size()));
    }

    void woo() {
        Woman woman = getRandomWoman(); // !(horny && !woman.horny)
        if (woman.state == Status.SINGLE) { // second condition is removable
            woman.state = Status.PREGNANT;
        }
    }

    void die(int i) throws InterruptedException {
        Pop.deadMen.put(i);
    }

}
