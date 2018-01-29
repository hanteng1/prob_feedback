package com.example.t_hant.pagefold;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import com.example.t_hant.pagefold.utils.MLog;

/**
 * Created by t_hant on 1/21/2018.
 */

public class GrabIt {
    private GrabIt() {
    }

    public static Bitmap takeScreenshot(View view, Bitmap.Config config) {
        int width = view.getWidth();
        int height = view.getHeight();

        if (view != null && width > 0 && height > 0) {
            Bitmap bitmap = Bitmap.createBitmap(width, height, config);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);

            //canvas.drawColor(Color.RED, PorterDuff.Mode.DARKEN); //NOTES: debug option

            MLog.d("create bitmap %dx%d, format %s", width, height, config);

            if (MLog.ENABLE_DEBUG) {
                MLog.d("create bitmap %dx%d, format %s", width, height, config);
            }

            return bitmap;
        } else {
            return null;
        }
    }
}
