package com.example.t_hant.pagefold;

import android.graphics.Bitmap;
import android.view.View;

import com.example.t_hant.pagefold.utils.MLog;
import com.example.t_hant.pagefold.utils.TextureUtils;
import com.example.t_hant.pagefold.utils.UI;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.opengles.GL10;

import static com.example.t_hant.pagefold.FlipRenderer.checkError;
import static com.example.t_hant.pagefold.utils.TextureUtils.d2r;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by t_hant on 1/21/2018.
 */

public class ViewDualCards {
    private int index = -1;
    private WeakReference<View> viewRef;
    private Texture texture;

    private Bitmap screenshot;

    private Card topCard = new Card();
    private Card bottomCard = new Card();

    private boolean orientationVertical = true;

    private float translateY;

    private float shrinkedLength = 0;

    public static final int X_00 = 0;
    public static final int Y_00 = 1;
    public static final int Z_00 = 2;

    public static final int X_01 = 3;
    public static final int Y_01 = 4;
    public static final int Z_01 = 5;

    public static final int X_11 = 6;
    public static final int Y_11 = 7;
    public static final int Z_11 = 8;

    public static final int X_10 = 9;
    public static final int Y_10 = 10;
    public static final int Z_10 = 11;

    public ViewDualCards(boolean orientationVertical) {
        topCard.setOrientation(orientationVertical);
        bottomCard.setOrientation(orientationVertical);
        this.orientationVertical = orientationVertical;
    }

    public int getIndex() {
        return index;
    }

    public View getView() {
        return viewRef != null ? viewRef.get() : null;
    }

    synchronized void resetWithIndex(int index) {
        this.index = index;
        viewRef = null;
        recycleScreenshot();
        recycleTexture();
    }

    synchronized boolean loadView(int index, View view, Bitmap.Config format) {
        UI.assertInMainThread();

        if (this.index == index
                && getView() == view
                && (screenshot != null || TextureUtils.isValidTexture(texture))
                ) {
            return false;
        }

        this.index = index;
        viewRef = null;
        recycleTexture();
        if (view != null) {
            viewRef = new WeakReference<View>(view);
            recycleScreenshot();
            screenshot = GrabIt.takeScreenshot(view, format);
        } else {
            recycleScreenshot();
        }

        return true;
    }

    public Texture getTexture() {
        return texture;
    }

    public Bitmap getScreenshot() {
        return screenshot;
    }

    public Card getTopCard() {
        return topCard;
    }

    public Card getBottomCard() {
        return bottomCard;
    }

    public void setTranslateY(float translateY)
    {
        this.translateY = translateY;
        getTopCard().setTranslateY(translateY);
        getBottomCard().setTranslateY(translateY);
    }

    public float getShrinkedLength()
    {
        return shrinkedLength;
    }

    public synchronized void buildTexture(FlipRenderer renderer, GL10 gl) {
        if (screenshot != null) {
            if (texture != null) {
                texture.destroy(gl);
            }
            texture = Texture.createTexture(screenshot, renderer, gl);
            recycleScreenshot();

            topCard.setTexture(texture);
            bottomCard.setTexture(texture);

            final float viewHeight = texture.getContentHeight();
            final float viewWidth = texture.getContentWidth();
            final float textureHeight = texture.getHeight();
            final float textureWidth = texture.getWidth();

            //MLog.d("index: " + index + ", viewheight " + viewHeight + " , viewwidth " + viewWidth  + ", textureheight " + textureHeight + " , texturewidth " + textureWidth);

            if (orientationVertical) {
                topCard.setCardVertices(new float[]{0f, viewHeight, 0f, // top left
                        0f, viewHeight / 2.0f, 0f, // bottom left
                        viewWidth, viewHeight / 2.0f, 0f, // bottom right
                        viewWidth, viewHeight, 0f // top right
                });

                topCard.setTextureCoordinates(new float[]{0f, 0f, 0f,
                        viewHeight / 2f / textureHeight,
                        viewWidth / textureWidth,
                        viewHeight / 2f / textureHeight,
                        viewWidth / textureWidth, 0f});

                bottomCard.setCardVertices(new float[]{0f, viewHeight / 2f, 0f, // top left
                        0f, 0f, 0f, // bottom left
                        viewWidth, 0f, 0f, // bottom right
                        viewWidth, viewHeight / 2f, 0f // top right
                });

                bottomCard.setTextureCoordinates(new float[]{0f,
                        viewHeight / 2f / textureHeight, 0f,
                        viewHeight / textureHeight,
                        viewWidth / textureWidth,
                        viewHeight / textureHeight,
                        viewWidth / textureWidth,
                        viewHeight / 2f / textureHeight});



            } else {
                topCard.setCardVertices(new float[]{0f, viewHeight, 0f, // top left
                        0f, 0f, 0f, // bottom left
                        viewWidth / 2f, 0f, 0f, // bottom right
                        viewWidth / 2f, viewHeight, 0f // top right
                });

                topCard.setTextureCoordinates(new float[]{0f, 0f, 0f,
                        viewHeight / textureHeight,
                        viewWidth / 2f / textureWidth,
                        viewHeight / textureHeight,
                        viewWidth / 2f / textureWidth, 0f});

                bottomCard.setCardVertices(new float[]{viewWidth / 2f,
                        viewHeight, 0f, // top left
                        viewWidth / 2f, 0f, 0f, // bottom left
                        viewWidth, 0f, 0f, // bottom right
                        viewWidth, viewHeight, 0f // top right
                });

                bottomCard.setTextureCoordinates(new float[]{
                        viewWidth / 2f / textureWidth, 0f,
                        viewWidth / 2f / textureWidth,
                        viewHeight / textureHeight, viewWidth / textureWidth,
                        viewHeight / textureHeight, viewWidth / textureWidth,
                        0f});
            }

            checkError(gl);
        }
    }

    //update the vertices based on display angle
    public synchronized void calculateVertices(float angle)
    {
        //assume vertical rotation

        if(texture == null)
        {
            return;
        }

        final float viewHeight = texture.getContentHeight();
        final float viewWidth = texture.getContentWidth();

        float foldingDepth = (viewHeight / 2.0f) * (float)sin(d2r(angle));
        float foldingShrink = (viewHeight / 2.0f) * (1.0f - (float)cos(d2r(angle)));

        if (orientationVertical) {
            topCard.setCardVertices(new float[]{0f, viewHeight - foldingShrink, 0f, // top left
                    0f, viewHeight / 2.0f, -foldingDepth, // bottom left
                    viewWidth, viewHeight / 2.0f, -foldingDepth, // bottom right
                    viewWidth, viewHeight - foldingShrink, 0f // top right
            });

            bottomCard.setCardVertices(new float[]{0f, viewHeight / 2f, -foldingDepth, // top left
                    0f, foldingShrink, 0f, // bottom left
                    viewWidth, foldingShrink, 0f, // bottom right
                    viewWidth, viewHeight / 2f, -foldingDepth // top right
            });

            shrinkedLength = 2 * foldingShrink;

        }
    }


    public synchronized void abandonTexture() {
        texture = null;
    }

    @Override
    public String toString() {
        return "ViewDualCards: (" + index + ", view: " + getView() + ")";
    }

    private void recycleScreenshot() {
        UI.recycleBitmap(screenshot);
        screenshot = null;
    }

    private void recycleTexture() {
        if (texture != null) {
            texture.postDestroy();
            texture = null;
        }
    }
}
