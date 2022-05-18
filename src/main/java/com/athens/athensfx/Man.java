package com.athens.athensfx;

public class Man extends Person {

    public Man(boolean horny, Population pop) {
        super(horny, pop, (horny) ? pop.philanderers: pop.faithfulMen);
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

    // todo why does this take 18% of the total computation? nextInt takes 3% maybe because it is an atomic operation on the same seedOfLife object shared across all people
    private Woman getRandomWoman() {
        return Pop.women.get(Pop.r2.nextInt(Pop.women.size()));
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
