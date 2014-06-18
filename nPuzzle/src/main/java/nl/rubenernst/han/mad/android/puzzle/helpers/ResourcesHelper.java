package nl.rubenernst.han.mad.android.puzzle.helpers;

import android.content.Context;
import android.content.res.TypedArray;
import nl.rubenernst.han.mad.android.puzzle.R;

/**
 * Created by rubenernst on 18-06-14.
 */
public class ResourcesHelper {
    public static int findIdForResourceByIdInArray(Context context, int arrayId, int value) {
        TypedArray array = context.getResources().obtainTypedArray(arrayId);
        int returnResourceId = -1;
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                int resourceId = array.getResourceId(i, -1);
                if (resourceId > -1) {
                    if (resourceId == value) {
                        returnResourceId = i;
                        break;
                    }
                }
            }

            array.recycle();
        }

        return returnResourceId;
    }
}
