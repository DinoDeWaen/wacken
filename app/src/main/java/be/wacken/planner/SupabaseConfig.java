package be.wacken.planner;

final class SupabaseConfig {
    private SupabaseConfig() {
    }

    static String url() {
        return BuildConfig.SUPABASE_URL;
    }

    static String anonKey() {
        return BuildConfig.SUPABASE_ANON_KEY;
    }
}
