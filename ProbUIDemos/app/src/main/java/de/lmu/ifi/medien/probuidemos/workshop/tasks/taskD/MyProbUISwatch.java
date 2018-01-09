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

package de.lmu.ifi.medien.probuidemos.workshop.tasks.taskD;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import de.lmu.ifi.medien.probuidemos.R;
import de.lmu.ifi.medien.probui.gui.base.ProbUIView;


public class MyProbUISwatch extends ProbUIView {

    private int swatch_red;
    private int swatch_green;
    private int swatch_blue;
    private Paint swatch_paint;

    public MyProbUISwatch(Context context) {
        super(context);
    }

    public MyProbUISwatch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MyProbUISwatch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.MyProbUISwatch);
        this.swatch_red = arr.getInteger(R.styleable.MyProbUISwatch_swatch_red, 0);
        this.swatch_green = arr.getInteger(R.styleable.MyProbUISwatch_swatch_green, 0);
        this.swatch_blue = arr.getInteger(R.styleable.MyProbUISwatch_swatch_blue, 0);

        this.swatch_paint = new Paint();
        this.swatch_paint.setColor(Color.rgb(this.swatch_red, this.swatch_green, this.swatch_blue));
    }

    public int getRed() {
        return this.swatch_red;
    }

    public int getGreen() {
        return this.swatch_green;
    }

    public int getBlue() {
        return this.swatch_blue;
    }

    @Override
    public void drawSpecific(Canvas canvas) {
        //this.swatch_paint.setAlpha((int)(this.core.getCandidateProb()*255));
        canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, this.getWidth() / 2, this.swatch_paint);
    }



    /* =============================================================================================
    Task D.1 - Please insert your code below:
    ============================================================================================= */

    // Step 1: Override the onProbSetup() method
    public void onProbSetup() {

        /* Step 2: Add a behaviour that describes touching anywhere in a wide area
        (e.g. eight times the swatch area) around the centre of the swatch. */
        this.core.addBehaviour("around_swatch: C[s=8]");
    }

    /* =============================================================================================
    End of Task D.1
    ============================================================================================= */


}
