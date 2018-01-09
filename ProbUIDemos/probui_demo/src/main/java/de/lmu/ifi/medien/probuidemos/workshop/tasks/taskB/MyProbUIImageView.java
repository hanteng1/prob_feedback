/*
ProbUI - a probabilistic reinterpretation of bounding boxes
designed to facilitate creating dynamic and adaptive mobile touch GUIs.
Copyright (C) 2017 Daniel Buschek

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package de.lmu.ifi.medien.probuidemos.workshop.tasks.taskB;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;

import de.lmu.ifi.medien.probui.gui.base.ProbUIImageView;
import de.lmu.ifi.medien.probui.pml.PMLRuleListener;

public class MyProbUIImageView extends ProbUIImageView {


    private int index;
    private BitmapDrawable[] images;
    private Paint previewPaint;
    private Bitmap previewImagePrev;
    private Bitmap previewImageNext;
    private Rect canvasRect;


    public MyProbUIImageView(Context context) {
        super(context);
    }

    public MyProbUIImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyProbUIImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setImages(BitmapDrawable[] images) {
        this.images = images;
        this.previewPaint = new Paint();
        this.updateImage();
    }

    public void updateImage() {
        this.setImageDrawable(this.images[this.index]);
        this.canvasRect = new Rect(0, 0, this.getWidth(), this.getHeight());
        this.previewImagePrev = null;
        if (this.index > 0) {
            this.previewImagePrev = this.images[this.index - 1].getBitmap();
        }
        this.previewImageNext = null;
        if (this.index < 2) {
            this.previewImageNext = this.images[this.index + 1].getBitmap();
        }
        this.postInvalidate();
    }

    private void prev() {
        if (this.index > 0) {
            this.index--;
            this.updateImage();
            core.claimDetermination();
        }
    }

    private void next() {
        if (this.index < 2) {
            this.index++;
            this.updateImage();
            core.claimDetermination();
        }
    }


    /* =============================================================================================
    Task B.1 - Please insert your code below:
    ============================================================================================= */

    // Step 1: Override the onProbSetup() method
    public void onProbSetup() {

        // Step 2: Add two behaviours for swipes from the touch centre to the left/right
        this.core.addBehaviour("swipe_left: Od->Lu");
        this.core.addBehaviour("swipe_right: Od->Ru");

        // Step 3: Add a rule to implement that performing a right swipe triggers the prev() method
        this.core.addRule("prev: swipe_right on complete and swipe_right is most_likely",
                new PMLRuleListener() {
                    @Override
                    public void onRuleSatisfied(String event, int subsequentCalls) {
                        prev();
                    }
                });

        // Step 3: Add a rule to implement that performing a left swipe triggers the next() method
        this.core.addRule("next: swipe_left on complete and swipe_left is most_likely",
                new PMLRuleListener() {
                    @Override
                    public void onRuleSatisfied(String event, int subsequentCalls) {
                        next();
                    }
                });
    }

    /* =============================================================================================
    End of Task B.1
    ============================================================================================= */




    /* =============================================================================================
    Task B.2 - Please insert your code below:
    ============================================================================================= */

    public void drawSpecific(Canvas canvas) {

        // Step 1: Get the behaviour probability for both swipes and store them in two variables
        double probSwipeLeft = this.core.getBehaviourProb("swipe_left");
        double probSwipeRight = this.core.getBehaviourProb("swipe_right");

        /* Step 2: Complete the code below to let the preview of the previous/next image
        fade in depending on the probability of the right/left swipe, respectively. */

        // if swipe left more likely and there is another image:
        if (probSwipeLeft > probSwipeRight && this.previewImageNext != null) {
            this.previewPaint.setAlpha((int) (probSwipeLeft * 255));
            canvas.drawBitmap(this.previewImageNext, null, this.canvasRect, this.previewPaint);
        }
        // else (swipe right more likely) and there is a previous image:
        else if (this.previewImagePrev != null) {
            this.previewPaint.setAlpha((int) (probSwipeRight * 255));
            canvas.drawBitmap(this.previewImagePrev, null, this.canvasRect, this.previewPaint);
        }
    }

    /* =============================================================================================
    End of Task B.2
    ============================================================================================= */


}
