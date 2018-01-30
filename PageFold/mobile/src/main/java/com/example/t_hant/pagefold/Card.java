package com.example.t_hant.pagefold;

import com.example.t_hant.pagefold.utils.MLog;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import static com.example.t_hant.pagefold.FlipRenderer.checkError;
import static com.example.t_hant.pagefold.utils.TextureUtils.d2r;
import static com.example.t_hant.pagefold.utils.TextureUtils.isValidTexture;
import static com.example.t_hant.pagefold.utils.TextureUtils.toFloatBuffer;
import static com.example.t_hant.pagefold.utils.TextureUtils.toShortBuffer;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static javax.microedition.khronos.opengles.GL10.GL_BACK;
import static javax.microedition.khronos.opengles.GL10.GL_BLEND;
import static javax.microedition.khronos.opengles.GL10.GL_CCW;
import static javax.microedition.khronos.opengles.GL10.GL_CLAMP_TO_EDGE;
import static javax.microedition.khronos.opengles.GL10.GL_CULL_FACE;
import static javax.microedition.khronos.opengles.GL10.GL_DEPTH_TEST;
import static javax.microedition.khronos.opengles.GL10.GL_FLOAT;
import static javax.microedition.khronos.opengles.GL10.GL_LIGHTING;
import static javax.microedition.khronos.opengles.GL10.GL_ONE;
import static javax.microedition.khronos.opengles.GL10.GL_ONE_MINUS_SRC_ALPHA;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_2D;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_COORD_ARRAY;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_WRAP_S;
import static javax.microedition.khronos.opengles.GL10.GL_TEXTURE_WRAP_T;
import static javax.microedition.khronos.opengles.GL10.GL_TRIANGLES;
import static javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_SHORT;
import static javax.microedition.khronos.opengles.GL10.GL_VERTEX_ARRAY;

/**
 * Created by t_hant on 1/21/2018.
 */

public class Card {

    public static final int AXIS_TOP = 0;
    public static final int AXIS_BOTTOM = 1;

    /**
     * The indices of x,y,z for vertices (0, 0), (0, 1), (1, 1), (1, 0)
     */
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

    private float cardVertices[];

    private short[] indices = {0, 1, 2, 0, 2, 3};

    private FloatBuffer vertexBuffer;

    private ShortBuffer indexBuffer;

    private float textureCoordinates[];

    private FloatBuffer textureBuffer;

    private Texture texture;

    private float angle = 0f;

    private int axis = AXIS_TOP;

    private int translateY = 0;
    private float inFoldingTranslateY = 0;


    private boolean orientationVertical = true;

    private boolean dirty = false;

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public float[] getCardVertices() {
        return cardVertices;
    }

    public short[] getIndices() {
        return indices;
    }

    public ShortBuffer getIndexBuffer() {
        return indexBuffer;
    }

    public void setCardVertices(float[] cardVertices) {
        this.cardVertices = cardVertices;
        this.dirty = true;
    }

    public void setTextureCoordinates(float[] textureCoordinates) {
        this.textureCoordinates = textureCoordinates;
        this.dirty = true;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void setAxis(int axis) {
        this.axis = axis;
    }

    public void setTranslateY(int translateY)
    {
        this.translateY = translateY;
    }

    public void setOrientation(boolean orientationVertical) {
        this.orientationVertical = orientationVertical;
    }

    public void draw(GL10 gl) {
        if (dirty) {
            updateVertices();
        }

        if (cardVertices == null) {
            return;
        }

        gl.glFrontFace(GL_CCW);

        gl.glEnable(GL_CULL_FACE);
        gl.glCullFace(GL_BACK);

        gl.glEnableClientState(GL_VERTEX_ARRAY);

        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        gl.glColor4f(1f, 1.0f, 1f, 1.0f);

        if (isValidTexture(texture)) {
            gl.glEnable(GL_TEXTURE_2D);
            gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
            gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            gl.glTexCoordPointer(2, GL_FLOAT, 0, textureBuffer);
            gl.glBindTexture(GL_TEXTURE_2D, texture.getId()[0]);
        }

        if (MLog.ENABLE_DEBUG) {
            checkError(gl);
        }

        //draw folded page
        gl.glPushMatrix();



        if (orientationVertical) {

            float foldingDepth;
            if (angle >= 0) {

                foldingDepth = (cardVertices[Y_00] - cardVertices[Y_01]) * (float)sin(d2r(angle));
                inFoldingTranslateY = (cardVertices[Y_00] - cardVertices[Y_01]) * (1.0f - (float)cos(d2r(angle)));

//                if (axis == AXIS_TOP) {
//                    //this is bottom card
//                    //gl.glTranslatef(0, translateY + inFoldingTranslateY, -foldingDepth);
//                    //gl.glTranslatef(0, cardVertices[Y_00], 0f); //translate the coordinates for rotation
//                    //gl.glRotatef(-angle, 1f, 0f, 0f);
//                    //gl.glTranslatef(0, -cardVertices[Y_00], 0f);
//
//                } else {
//                    //this is top card
//                   // gl.glTranslatef(0, translateY + inFoldingTranslateY, -foldingDepth);
//                    //gl.glTranslatef(0, cardVertices[Y_11], 0f);
//                    //gl.glRotatef(angle, 1f, 0f, 0f);
//                    //gl.glTranslatef(0, -cardVertices[Y_11], 0f);
//
//                }

                gl.glTranslatef(0, translateY, 0);

            }
        } else {
            if (angle > 0) {
                if (axis == AXIS_TOP) {
                    gl.glTranslatef(cardVertices[X_00], 0, 0f);
                    gl.glRotatef(-angle, 0f, 1f, 0f);
                    gl.glTranslatef(-cardVertices[X_00], 0, 0f);
                } else {
                    gl.glTranslatef(cardVertices[X_11], 0, 0f);
                    gl.glRotatef(angle, 0f, 1f, 0f);
                    gl.glTranslatef(-cardVertices[X_11], 0, 0f);
                }
            }
        }

        gl.glVertexPointer(3, GL_FLOAT, 0, vertexBuffer);
        gl.glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_SHORT, indexBuffer);

        if (MLog.ENABLE_DEBUG) {
            checkError(gl);
        }

        gl.glPopMatrix();

        if (isValidTexture(texture)) {
            gl.glDisableClientState(GL_TEXTURE_COORD_ARRAY);
            gl.glDisable(GL_TEXTURE_2D);
        }

        //draw shadow, while cardvertices remain unchanged
        float w, h, z;
        float[] shadowVertices;

        if (angle > 0) {
            float alpha = 1f * (90f - angle) / 90f;  //decreasing

            if (axis == AXIS_TOP) {
                if (orientationVertical) {
                    h = (cardVertices[Y_00] - cardVertices[Y_01]) * (1.0f - (float)cos(d2r(angle)));
                    z = (cardVertices[Y_00] - cardVertices[Y_01]) * (float)sin(d2r(angle));
                    shadowVertices = new float[]{
                            cardVertices[X_00], cardVertices[Y_01] + h, z,
                            cardVertices[X_01], cardVertices[Y_01], 0f,
                            cardVertices[X_11], cardVertices[Y_11], 0f,
                            cardVertices[X_10], cardVertices[Y_01] + h, z
                    };
                } else { //horizontal
                    w = (cardVertices[X_10] - cardVertices[X_00]) * (1.0f - (float)cos(d2r(angle)));
                    z = (cardVertices[X_10] - cardVertices[X_00]) * (float)sin(d2r(angle));
                    shadowVertices = new float[]{
                            cardVertices[X_10] - w, cardVertices[Y_00], z,
                            cardVertices[X_11] - w, cardVertices[Y_01], z,
                            cardVertices[X_11], cardVertices[Y_11], 0f,
                            cardVertices[X_10], cardVertices[Y_10], 0f
                    };
                }
            } else {
                if (orientationVertical) {
                    h = (cardVertices[Y_00] - cardVertices[Y_01]) * (1f - (float)cos(d2r(angle)));
                    z = (cardVertices[Y_00] - cardVertices[Y_01]) * (float)sin(d2r(angle));
                    shadowVertices = new float[]{
                            cardVertices[X_00], cardVertices[Y_00], 0f,
                            cardVertices[X_01], cardVertices[Y_00] - h, z,
                            cardVertices[X_11], cardVertices[Y_00] - h, z,
                            cardVertices[X_10], cardVertices[Y_00], 0f
                    };
                } else { //horizontal
                    w = (cardVertices[X_10] - cardVertices[X_00]) * (1f - (float)cos(d2r(angle)));
                    z = (cardVertices[X_10] - cardVertices[X_00]) * (float)sin(d2r(angle));
                    shadowVertices = new float[]{
                            cardVertices[X_00], cardVertices[Y_00], 0f,
                            cardVertices[X_01], cardVertices[Y_01], 0f,
                            cardVertices[X_00] + w, cardVertices[Y_11], z,
                            cardVertices[X_01] + w, cardVertices[Y_10], z
                    };
                }
            }

            gl.glDisable(GL_LIGHTING);
            gl.glDisable(GL_DEPTH_TEST);
            gl.glDisable(GL_TEXTURE_2D);
            gl.glEnable(GL_BLEND);
            gl.glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
            gl.glColor4f(0f, 0.0f, 0f, alpha);
            gl.glVertexPointer(3, GL_FLOAT, 0, toFloatBuffer(shadowVertices));
            gl.glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_SHORT, indexBuffer);

            gl.glEnable(GL_TEXTURE_2D);
            gl.glEnable(GL_DEPTH_TEST);
            gl.glEnable(GL_LIGHTING);
        }

        if (MLog.ENABLE_DEBUG) {
            checkError(gl);
        }

        gl.glDisable(GL_BLEND);
        gl.glDisableClientState(GL_VERTEX_ARRAY);
        gl.glDisable(GL_CULL_FACE);
    }

    private void updateVertices() {
        vertexBuffer = toFloatBuffer(cardVertices);
        indexBuffer = toShortBuffer(indices);
        textureBuffer = toFloatBuffer(textureCoordinates);
    }

}
