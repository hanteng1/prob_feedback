/*
ProbUI - a probabilistic reinterpretation of bounding boxes
designed to facilitate creating dynamic and adaptive mobile touch GUIs.
Copyright (C) 2017  Daniel Buschek

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package de.lmu.ifi.medien.probuidemos.gui.elements;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;

import de.lmu.ifi.medien.probuidemos.R;
import de.lmu.ifi.medien.probui.gui.ProbInteractor;
import de.lmu.ifi.medien.probui.gui.ProbInteractorCore;
import de.lmu.ifi.medien.probui.observations.ProbObservation;
import de.lmu.ifi.medien.probui.observations.ProbObservationTouch;
import de.lmu.ifi.medien.probui.pml.PMLRuleListener;

public class ProbCheckBox extends CheckBox implements ProbInteractor {

    public static final int DIRECTION_RIGHT = 0;
    public static final int DIRECTION_LEFT = 1;

    private ProbInteractorCore core;


    private int direction;


    // Drawing stuff:
    private float scale = getContext().getResources().getDisplayMetrics().density;
    private Paint textPaint, bgPaint, togglePaint;
    private int activeColor = Color.rgb(0, 207, 165);
    private int inactiveColor = Color.LTGRAY;
    private int switchToOnDetermination;


    public ProbCheckBox(Context context) {
        super(context);
        sharedConstructor();
    }

    public ProbCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.ProbCheckBox);
        this.direction = arr.getInteger(R.styleable.ProbCheckBox_direction, 0);
        sharedConstructor();
    }

    public ProbCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.ProbCheckBox);
        this.direction = arr.getInteger(R.styleable.ProbCheckBox_direction, 0);
        sharedConstructor();
    }


    private void sharedConstructor() {

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


        this.setHeight((int) (this.textPaint.getTextSize() * 1.5f));
        this.setWidth((int) (this.textPaint.getTextSize() * 1.5f));


        this.core = new ProbInteractorCore(this);
        this.core.init();
        this.core.debugDrawOutline = false;
        this.core.debugDraw = false;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        this.core.drawBody(canvas);
    }


    @Override
    public ProbInteractorCore getCore() {
        return this.core;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void drawSpecific(Canvas canvas) {


        // Draw the box:
        // this.bgPaint.setStyle(this.isChecked() ? Paint.Style.FILL_AND_STROKE : Paint.Style.STROKE);

        /*
        float endX = 0;
        float endY = this.getHeight() / 2;
        float startX = this.getHeight();
        float startY = this.getHeight() / 2;

        float toggleSize = this.getHeight() * .5f;
        float toggleOffsetXEnd = 0;
        float toggleOffsetYEnd = -toggleSize / 2;
        float toggleOffsetXStart = -toggleSize;
        float toggleOffsetYStart = -toggleSize / 2;

        if (this.direction == DIRECTION_LEFT) {
            endX = this.getHeight() / 2;
            endY = 0;
            startX = this.getHeight() / 2;
            startY = this.getHeight();
            toggleOffsetXEnd = -toggleSize / 2;
            toggleOffsetYEnd = 0;
            toggleOffsetXStart = -toggleSize / 2;
            toggleOffsetYStart = -toggleSize / 2;
        }
        */
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
            canvas.drawCircle(startX + toggleOffsetXStart + toggleSize / 2, startY + toggleOffsetYStart + toggleSize / 2, toggleSize / 2, this.togglePaint);
            //canvas.drawRect(startX + toggleOffsetXStart, startY + toggleOffsetYStart,
            //        startX + toggleOffsetXStart + toggleSize, startY + toggleOffsetYStart + toggleSize, this.togglePaint);
        } else {
            this.togglePaint.setColor(this.inactiveColor);
            canvas.drawCircle(endX + toggleOffsetXEnd + toggleSize / 2, endY + toggleOffsetYEnd + toggleSize / 2, toggleSize / 2, this.togglePaint);
            //canvas.drawRect(endX + toggleOffsetXEnd, endY + toggleOffsetYEnd,
            //        endX + toggleOffsetXEnd + toggleSize, endY + toggleOffsetYEnd + toggleSize, this.togglePaint);
        }


        // Draw the label:
        canvas.drawText(this.getText().toString(), this.getHeight() + 10, this.textPaint.getTextSize(), this.textPaint);

    }

    @Override
    public void onTouchDown(ProbObservationTouch obs) {

    }

    @Override
    public void onTouchMove(ProbObservationTouch obs) {

    }

    @Override
    public void onTouchUp(ProbObservationTouch obs, int numRemainingPointers) {

        core.selfExclude(0);
        core.undetermine();
    }

    @Override
    public void onTouchDownPost(ProbObservationTouch obs) {

    }

    @Override
    public void onTouchMovePost(ProbObservationTouch obs) {

    }

    @Override
    public void onTouchUpPost(ProbObservationTouch obs, int numRemainingPointers) {

    }

    @Override
    public String[] getDefaultBehaviours() {
        return null;
    }

    @Override
    public void onCoreObserve(ProbObservation obs) {

    }

    @Override
    public void onCoreFinaliseBehaviourSetup() {

    }

    @Override
    public void onProbSetup() {


        this.core.addBehaviour("right: W[s=2]->E[s=2]");
        this.core.addBehaviour("down: N[s=2]->S[s=2]");
        this.core.addBehaviour("left: E[s=2]->W[s=2]");
        this.core.addBehaviour("up: S[s=2]->N[s=2]");


        String rule = "on: (down is complete and down is most_likely) or ";
        if (this.direction == DIRECTION_RIGHT)
            rule += "(right is complete and right is most_likely)";
        else if (this.direction == DIRECTION_LEFT)
            rule += "(left is complete and left is most_likely)";
        this.core.addRule(rule, new PMLRuleListener() {
            @Override
            public void onRuleSatisfied(String event, int subsequentCalls) {
                switchOn();
                core.claimDetermination();
            }
        });


        rule = "off: (up is complete and up is most_likely) or ";
        if (this.direction == DIRECTION_RIGHT)
            rule += "(left is complete and left is most_likely)";
        else if (this.direction == DIRECTION_LEFT)
            rule += "(right is complete and right is most_likely)";
        this.core.addRule(rule, new PMLRuleListener() {
            @Override
            public void onRuleSatisfied(String event, int subsequentCalls) {
                switchOff();
                core.claimDetermination();
            }
        });

        this.core.setReady();

        this.setMinimumHeight((int) (this.textPaint.getTextSize() * 1.5f));
        this.setMinimumWidth((int) (this.getMinimumHeight() + 10 + this.textPaint.measureText(this.getText().toString()) * 1.1f));
    }

    private void switchOff() {
        this.switchToOnDetermination = 0;
    }

    private void switchOn() {
        this.switchToOnDetermination = 1;
    }

    private void switchCheckedState() {
        this.setChecked(!this.isChecked());
    }

    @Override
    public void onExclude() {

    }

    @Override
    public void onSelfExclude() {

    }

    @Override
    public void onDetermined() {

        this.setChecked(this.switchToOnDetermination == 1);
        this.core.undetermine();
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
