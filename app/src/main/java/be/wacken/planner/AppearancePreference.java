package be.wacken.planner;

/** Device-local presentation policy; contains no Android or business dependencies. */
enum AppearancePreference {
    SYSTEM("system"), DARK("dark"), LIGHT("light");

    private final String storedValue;
    AppearancePreference(String storedValue) { this.storedValue=storedValue; }
    String storedValue() { return storedValue; }

    static AppearancePreference parse(String stored) {
        for(var mode:values())if(mode.storedValue.equals(stored))return mode;
        return SYSTEM;
    }

    AppearancePreference resolve(boolean deviceDark) {
        return this==SYSTEM?(deviceDark?DARK:LIGHT):this;
    }

    AppearancePreference toggle(boolean deviceDark) {
        return resolve(deviceDark)==DARK?LIGHT:DARK;
    }

    String toggleLabel(boolean deviceDark) {
        return resolve(deviceDark)==DARK?"Switch to light":"Switch to dark";
    }

    Palette palette(boolean deviceDark) {
        return resolve(deviceDark)==DARK?DARK_PALETTE:LIGHT_PALETTE;
    }

    private static final Palette DARK_PALETTE=new Palette(true,0xff101418,0xff1a2228,0xff24303a,
            0xfff3f4ec,0xffb4c1c8,0xffffd25a,0xff191710,0xffffabb2,0xff81949f);
    private static final Palette LIGHT_PALETTE=new Palette(false,0xfff4efe3,0xfffffdf5,0xffe7dfd0,
            0xff20241f,0xff51574e,0xff3b553c,0xffffffff,0xff9c2733,0xff6c7368);

    record Palette(boolean dark,int background,int panel,int raised,int text,int muted,
                   int accent,int onAccent,int error,int line) { }
}
