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

package de.lmu.ifi.medien.probuidemos.workshop.tasks.taskC;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import de.lmu.ifi.medien.probuidemos.R;
import de.lmu.ifi.medien.probui.gui.ProbInteractor;
import de.lmu.ifi.medien.probui.gui.base.ProbUICheckBox;
import de.lmu.ifi.medien.probui.pml.PMLRuleListener;


/**
 * Created by Daniel on 26.08.2016.
 */
public class MyProbUICheckBox extends ProbUICheckBox implements ProbInteractor {

    public static final int DIRECTION_RIGHT = 0;
    public static final int DIRECTION_LEFT = 1;

    private int direction;


    // Drawing stuff:
    private float scale = getContext().getResources().getDisplayMetrics().density;
    private Paint textPaint, bgPaint, togglePaint;
    private int activeColor = Color.rgb(0, 207, 165);
    private int inactiveColor = Color.LTGRAY;
    private int switchToOnDetermination;


    public MyProbUICheckBox(Context context) {
        super(context);
        init();
    }

    public MyProbUICheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.ProbCheckBox);
        this.direction = arr.getInteger(R.styleable.ProbCheckBox_direction, 0);
        init();
    }

    public MyProbUICheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.ProbCheckBox);
        this.direction = arr.getInteger(R.styleable.ProbCheckBox_direction, 0);
        init();
    }


    private void init() {

        this.textPaint = new Paint();
        this.textPaint.setStrokeWidth(2);
        this.textPaint.setTextSize((20 * scale + 0.5f));
        this.textPaint.setStyle(Paint.Style.FILL);
        this.textPaint.setColor(Color.BLACK);

        this.bgPaint = new Paint();
        this.bgPaint.setStrokeWidth(4 * scale + 0.5f);
        this.bgPaint.setStyle(Paint.Style.FILL);
        this.bgPaint.setColor(Color.GRAY);
        this.bgPaint.setStrokeCap(Paint.Cap.ROUND);

        this.togglePaint = new Paint();
        this.togglePaint.setStyle(Paint.Style.FILL);
        this.togglePaint.setColor(this.inactiveColor);

        this.setHeight((int) (this.textPaint.getTextSize() * 1.5f * 1.25f));
        this.setWidth((int) (this.textPaint.getTextSize() * 1.5f * 2f));
    }

    @Override
    public void drawSpecific(Canvas canvas) {

        this.setMinimumHeight((int) (this.textPaint.getTextSize() * 1.5f));
        this.setMinimumWidth((int) (this.getMinimumHeight() + 10
                + this.textPaint.measureText(this.getText().toString()) * 1.1f));

        float startX = this.getHeight() / 2;
        float startY = this.getHeight() * .75f;
        float endX = 0;
        float endY = this.getHeight() * .25f;

        float toggleSize = this.getHeight() * .5f;
        float toggleOffsetXStart = -toggleSize / 2;
        float toggleOffsetYStart = -toggleSize / 2;
        float toggleOffsetXEnd = -toggleSize / 2;
        float toggleOffsetYEnd = -toggleSize / 2;

        if (this.direction == DIRECTION_LEFT) {
            startX = this.getHeight() / 2 - this.getHeight() / 2;
            endX = this.getHeight() / 2;
        }
        canvas.drawLine(startX, startY, endX, endY, this.bgPaint);

        if (this.isChecked()) {
            this.togglePaint.setColor(this.activeColor);
            canvas.drawCircle(startX + toggleOffsetXStart + toggleSize / 2,
                    startY + toggleOffsetYStart + toggleSize / 2, toggleSize / 2, this.togglePaint);
        } else {
            this.togglePaint.setColor(this.inactiveColor);
            canvas.drawCircle(endX + toggleOffsetXEnd + toggleSize / 2,
                    endY + toggleOffsetYEnd + toggleSize / 2, toggleSize / 2, this.togglePaint);
        }

        canvas.drawText(this.getText().toString(), this.getHeight() + 10,
                this.textPaint.getTextSize(), this.textPaint);
    }

    @Override
    public void onDetermined() {
        this.setChecked(this.switchToOnDetermination == 1);
        this.core.undetermine();
    }

    private void switchOff() {
        this.switchToOnDetermination = 0;
        core.claimDetermination();
    }

    private void switchOn() {
        this.switchToOnDetermination = 1;
        core.claimDetermination();
    }


    /* =============================================================================================
    Task C - Please insert your code below:
    ============================================================================================= */

    // Step 1: Override the onProbSetup() method
    public void onProbSetup() {

        // Step 2: Add four behaviours for swipes across the widget in all four directions
        this.core.addBehaviour("right: W->E");
        this.core.addBehaviour("down: N->S");
        this.core.addBehaviour("left: E->W");
        this.core.addBehaviour("up: S->N");

        /* Step 3: Complete the code below so that the switchOn() method
        is called on performing a down OR right swipe */

        String rule = "on: (down is complete and down is most_likely) " +
                "or (right is complete and right is most_likely)";
        this.core.addRule(rule, new PMLRuleListener() {
            @Override
            public void onRuleSatisfied(String event, int subsequentCalls) {
                switchOn();
            }
        });

        /* Step 4: Complete the code below so that the switchOff() method
        is called on performing an up OR left swipe */

        rule = "off: (up is complete and up is most_likely) or " +
                "(left is complete and left is most_likely)";
        this.core.addRule(rule, new PMLRuleListener() {
            @Override
            public void onRuleSatisfied(String event, int subsequentCalls) {
                switchOff();
            }
        });
    }

    /* =============================================================================================
    End of Task C
    ============================================================================================= */

}

