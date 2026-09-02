package apm23.taczammoboxtweak;

/** Pure tier rules shared by gameplay and client mixins so CI can verify them deterministically. */
public final class AmmoBoxTierRules {
    public static final int IRON_LEVEL = 0;
    public static final int GOLD_LEVEL = 1;
    public static final int DIAMOND_LEVEL = 2;
    public static final int DIAMOND_LEVEL_2 = 3;

    public static final int IRON_CAPACITY = 1500;
    public static final int GOLD_CAPACITY = 4000;
    public static final int DIAMOND_CAPACITY = 6000;
    public static final int DIAMOND_LEVEL_2_CAPACITY = 10000;

    public static final int DIAMOND_MODEL_OPEN = 4;
    public static final int DIAMOND_MODEL_FILLED = 5;

    private AmmoBoxTierRules() {
    }

    public static int capacityForLevel(int level) {
        return switch (level) {
            case IRON_LEVEL -> IRON_CAPACITY;
            case GOLD_LEVEL -> GOLD_CAPACITY;
            case DIAMOND_LEVEL -> DIAMOND_CAPACITY;
            default -> DIAMOND_LEVEL_2_CAPACITY;
        };
    }

    public static boolean isDiamondLevel2(int level) {
        return level >= DIAMOND_LEVEL_2;
    }

    public static int diamondModelState(boolean open) {
        return open ? DIAMOND_MODEL_OPEN : DIAMOND_MODEL_FILLED;
    }
}
