package com.athens.athensfx;

import java.util.Arrays;
import java.util.LinkedList;

class Stopper {
    // This class pauses the population execution when the percentages aren't changing much
    // for a prolonged period of time.
    final Population p;
    final LinkedList<Float> cacheMen = new LinkedList<>();
    final LinkedList<Float> cacheWomen = new LinkedList<>();
    float threshold = 0.008f;
    boolean done = false;

    Stopper(Population p) {
        this.p = p;
        // we initialize the array with random values so as not to mess up the computation in the first iterations
        // (that aren't going to give a result anyway)
        Float[] arrayInitialized = {999f, 1f, 999f, 1f, 999f, 1f, 999f, 1f, 999f, 1f, 999f, 1f, 999f, 1f, 999f};
        cacheMen.addAll(Arrays.asList(arrayInitialized));
        cacheWomen.addAll(Arrays.asList(arrayInitialized));
    }

    boolean update(float mR, float wR) {
        // This method updates the two linkedList "caches" that store the past 15 values for women and men ratio.
        // It also checks if the ratios are stable and returns true if they are.
        // This method is called by setInfo on WindowController.
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
        // This method checks if the past 15 ratios are stable, i.e. if the past 15 ratios differ
        // at most by the threshold field. Basically, if the maximum or the minimum differ by more than the
        // threshold from the average of the past 15 iterations, then it's stable.
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
        // this method is only used into isStable and is pretty self-explanatory.
        float upperD = max - average;
        float lowerD = average - min;
        return Math.max(upperD, lowerD);
    }
}