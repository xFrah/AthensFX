package com.athens.athensfx;

import java.util.Arrays;
import java.util.LinkedList;

class Stopper {
    final Population p;
    final LinkedList<Float> cacheMen = new LinkedList<>();
    final LinkedList<Float> cacheWomen = new LinkedList<>();
    float threshold = 0.008f;
    boolean done = false;

    Stopper(Population p) {
        this.p = p;
        Float[] asd = {999f, 1f, 999f, 1f, 999f, 1f, 999f, 1f, 999f, 1f, 999f, 1f, 999f, 1f, 999f};
        cacheMen.addAll(Arrays.asList(asd));
        cacheWomen.addAll(Arrays.asList(asd));
    }

    boolean update(float mR, float wR) {
        if (done) return false;
        cacheMen.removeFirst();
        cacheWomen.removeFirst();
        cacheMen.add(mR);
        cacheWomen.add(wR);
        if (isStable(cacheMen) && isStable(cacheWomen)) {
            done = true;
            return true;
        }
        return false;
    }

    boolean isStable(LinkedList<Float> list) {
        float sum = 0;
        float min = list.getFirst();
        float max = list.getFirst();
        for (Float f: list) {
            if (f > max) max = f;
            else if (f < min) min = f;
            sum += f;
        }
        return findMaxDeviation(max, min, sum / list.size()) < threshold;
    }

    float findMaxDeviation(float max, float min, float average) {
        float upperD = max - average;
        float lowerD = average - min;
        return Math.max(upperD, lowerD);
    }
}