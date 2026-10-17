package be.wacken.planner;

final class BandDetailLayoutPolicy {
    private static final int NARROW_SCREEN_WIDTH_DP = 520;

    private BandDetailLayoutPolicy() {
    }

    static boolean stacksSections(int screenWidthDp) {
        return screenWidthDp < NARROW_SCREEN_WIDTH_DP;
    }
}
