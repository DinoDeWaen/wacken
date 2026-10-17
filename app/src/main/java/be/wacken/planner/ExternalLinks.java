package be.wacken.planner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

final class ExternalLinks {
    private ExternalLinks() {
    }

    static void open(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
