package nl.rubenernst.han.mad.android.puzzle.helpers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;

/**
 * Created by rubenernst on 17-04-14.
 */
public class BitmapGameHelper {
    public static ArrayList<Bitmap> spliceBitmap(Bitmap image, int columns, int pieceWidth) {
        ArrayList<Bitmap> tiles = new ArrayList<Bitmap>();

        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < columns; j++) {
                int x = (int) Math.floor(j * pieceWidth);
                int y = (int) Math.floor(i * pieceWidth);

                Bitmap tile = Bitmap.createBitmap(image, x, y, pieceWidth, pieceWidth);
                tiles.add(tile);
            }
        }

        return tiles;
    }

    public static ArrayList<Bitmap> addBorderAroundBitmaps(ArrayList<Bitmap> bitmaps, int size, int color) {
        for (Bitmap bitmap : bitmaps) {
            BitmapGameHelper.addBorderAroundBitmap(bitmap, size, color);
        }

        return bitmaps;
    }

    public static ArrayList<Bitmap> addBorderAroundBitmaps(ArrayList<Bitmap> bitmaps, int size) {
        return BitmapGameHelper.addBorderAroundBitmaps(bitmaps, size, Color.BLACK);
    }

    public static Bitmap addBorderAroundBitmap(Bitmap bitmap, int size, int color) {
        Canvas canvas = new Canvas(bitmap);
        Paint p = new Paint();
        p.setStrokeWidth(size);
        p.setColor(color);

        canvas.drawLine(0, 0, bitmap.getWidth(), 0, p);
        canvas.drawLine(bitmap.getWidth() - size, 0, bitmap.getWidth() - size, bitmap.getHeight(), p);
        canvas.drawLine(bitmap.getWidth(), bitmap.getHeight() - size, 0, bitmap.getHeight() - size, p);
        canvas.drawLine(0, bitmap.getHeight(), 0, 0, p);

        return bitmap;
    }

    public static Bitmap addBorderAroundBitmap(Bitmap bitmap, int size) {
        return BitmapGameHelper.addBorderAroundBitmap(bitmap, size, Color.BLACK);
    }

    public static Bitmap createSolidBitmap(int width, int height, int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);

        return bitmap;
    }
}
