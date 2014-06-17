package nl.rubenernst.han.mad.android.puzzle.helpers;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import com.afollestad.silk.fragments.list.SilkListFragment;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import nl.rubenernst.han.mad.android.puzzle.R;

/**
 * Created by rubenernst on 17-06-14.
 */
public class InsetsHelper {
    public static void setInsets(Activity context, View view, boolean includeTop, boolean includeBottom) {
        setInsets(context, view, includeTop, includeBottom, false);
    }

    public static void setInsets(Activity context, View view, boolean includeTop, boolean includeBottom, boolean noTranslucencyTop) {
        int topPadding = 0;
        int bottomPadding = 0;
        int rightPadding = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(context);
            SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
            if (!noTranslucencyTop) topPadding = config.getPixelInsetTop(true);
            bottomPadding = config.getPixelInsetBottom();
            rightPadding = config.getPixelInsetRight();
        }
        view.setPadding(0, includeTop ? topPadding : 0,
                rightPadding, includeBottom ? bottomPadding : 0);
    }
}
