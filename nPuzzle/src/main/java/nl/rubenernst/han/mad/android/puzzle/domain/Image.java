package nl.rubenernst.han.mad.android.puzzle.domain;

import android.graphics.Bitmap;

/**
 * Created by rubenernst on 18-03-14.
 */
public class Image {
    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
