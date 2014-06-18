package nl.rubenernst.han.mad.android.puzzle.helpers;

import android.content.Context;
import android.content.res.TypedArray;
import nl.rubenernst.han.mad.android.puzzle.R;

/**
 * Created by rubenernst on 18-06-14.
 */
public class ResourcesHelper {
    public static int findIdForResourceByIdInArray(Context context, int arrayId, int value) {
        TypedArray colors = context.getResources().obtainTypedArray(arrayId);
        if (colors != null) {
            for (int i = 0; i < colors.length(); i++) {
                int resourceId = colors.getResourceId(i, -1);
                if (resourceId > -1) {
                    if (resourceId == value) {
                        return i;
                    }
                }
            }

            colors.recycle();
        }

        return -1;
    }
}
