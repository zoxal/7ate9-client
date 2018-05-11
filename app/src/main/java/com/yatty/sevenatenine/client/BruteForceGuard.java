package com.yatty.sevenatenine.client;

import java.util.ArrayList;
import java.util.List;

public class BruteForceGuard {
    public static final String TAG = BruteForceGuard.class.getSimpleName();
    private static final long PUNISHMENT_DURATION = 2000;
    private static final long MISTAKE_COMBO = 5;
    private static final long MISTAKE_COMBO_TIME = 2000;
    // time between two mistakes that can be forgiven
    private static final long MISTAKE_FORGIVENESS_TIME = 500;
    private static long lastMistakeTime = 0;

    private static List<Long> mistakes = new ArrayList<>(20);

    public static void recordMistake() {
        mistakes.add(System.currentTimeMillis());
        if (mistakes.size() > 1 &&
                (mistakes.get(mistakes.size()-1) - mistakes.get(mistakes.size()-2)) > MISTAKE_FORGIVENESS_TIME) {
            mistakes.clear();
        } else {
            removeRottenMistakes();
        }
    }

    public static boolean isImprisoned() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastMistakeTime) < PUNISHMENT_DURATION) {
            return true;
        } else {
            removeRottenMistakes();
            if (mistakes.size() >= MISTAKE_COMBO) {
                lastMistakeTime = mistakes.get(mistakes.size() - 1);
                return true;
            } else {
                return false;
            }
        }
    }

    public static void forgive() {
        lastMistakeTime = 0;
    }

    private static void removeRottenMistakes() {
        int mistakesRemoved = 0;
        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < mistakes.size(); i++) {
            long mistakeTime = mistakes.get(i - mistakesRemoved);
            if ((currentTime - mistakeTime) >= MISTAKE_COMBO_TIME) {
                mistakes.remove(i);
                mistakesRemoved++;
            }
        }
    }
}
