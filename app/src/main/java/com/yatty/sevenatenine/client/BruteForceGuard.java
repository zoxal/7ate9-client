package com.yatty.sevenatenine.client;

import java.util.ArrayList;
import java.util.List;

public class BruteForceGuard {
    private static final long PUNISHMENT_DURATION = 3000;
    private static final long MISTAKE_COMBO = 4;
    private static final long MISTAKE_COMBO_TIME = 500;


    private static List<Long> mistakes = new ArrayList<>(20);

    public static void recordMistake() {
        mistakes.add(System.currentTimeMillis());
    }

    public static boolean isImprisoned() {
        return true;
    }
}
