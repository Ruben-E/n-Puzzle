package nl.rubenernst.han.mad.android.puzzle.helpers;

import android.app.Activity;
import android.os.Build;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import nl.rubenernst.han.mad.android.puzzle.R;

/**
 * Created by rubenernst on 17-06-14.
 */
public class TintHelper {
    public static SystemBarTintManager setupTransparentTints(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager;
            tintManager = new SystemBarTintManager(context);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setTintResource(R.color.holo_blue_dark);
        }

        return null;
    }
}
