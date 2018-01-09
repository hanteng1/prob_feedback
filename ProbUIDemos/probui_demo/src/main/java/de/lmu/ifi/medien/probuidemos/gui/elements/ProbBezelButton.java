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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import de.lmu.ifi.medien.probui.gui.ProbInteractor;
import de.lmu.ifi.medien.probui.gui.ProbInteractorCore;
import de.lmu.ifi.medien.probui.observations.ProbObservation;
import de.lmu.ifi.medien.probui.observations.ProbObservationTouch;
import de.lmu.ifi.medien.probui.pml.PMLRuleListener;

public class ProbBezelButton extends Button implements ProbInteractor {


    public static final int COLOUR_BEZEL_MODE_START = Color.argb(200, 220, 200, 40);
    public static final int COLOUR_BEZEL_MODE_END_CUT = Color.argb(200, 220, 60, 40);
    public static final int COLOUR_BEZEL_MODE_END_PASTE = Color.argb(200, 40, 220, 100);

    /**
     * Core that manages the probabilistic behaviour of this interactor.
     */
    private ProbInteractorCore core;

    private float scale = getContext().getResources().getDisplayMetrics().density;
    private boolean activated;
    private boolean retreating;

    private float tipX, tipY;

    private Path dragPath;
    private Paint dragPaint;


    public ProbBezelButton(Context context) {

        super(context);
        sharedConstructor();
    }

    public ProbBezelButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructor();
    }

    public ProbBezelButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        sharedConstructor();
    }


    private void sharedConstructor() {


        this.dragPath = new Path();

        this.dragPaint = new Paint() {
            {
                setStyle(Style.FILL_AND_STROKE);
                setStrokeCap(Paint.Cap.ROUND);
                setStrokeWidth(2);
                setColor(COLOUR_BEZEL_MODE_START);
                setAntiAlias(true);
            }
        };

        this.core = new ProbInteractorCore(this);
        this.core.init();

        this.core.debugDrawOutline = false;
        this.core.debugDraw = false;
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

        this.retreating = ! this.activated && (this.tipX != 0 || this.tipY != 0);


        if (this.activated || this.retreating) {
            this.dragPaint.setColor(this.getHighlightColor());
            canvas.drawPath(this.dragPath, this.dragPaint);
        }

        if (this.retreating) {
            this.doRetreat();
        }
    }

    private void doRetreat() {

        float dx = 0 - this.tipX;
        float dy = 0 - this.tipY;
        this.tipX += dx*0.1;
        this.tipY += dy*0.1;

        if (Math.sqrt(this.tipX * this.tipX + this.tipY * this.tipY) < 5) {
            this.tipX = 0;
            this.tipY = 0;
        }

        this.updateTip();
    }

    @Override
    public void onTouchDown(ProbObservationTouch obs) {
    }

    @Override
    public void onTouchMove(ProbObservationTouch obs) {

        if (this.activated) {

            this.tipX = (float) (obs.getRealFeatures()[ProbObservationTouch.FEATURE_X]
                    * this.core.getSurfaceWidth() - this.getX());
            this.tipY = (float) (obs.getRealFeatures()[ProbObservationTouch.FEATURE_Y]
                    * this.core.getSurfaceHeight() - this.getY()
                    - BezelledEditText.BEZEL_SELECTION_OFFSET_Y * .5f * this.scale);

            updateTip();
        }
    }

    private void updateTip() {
        this.dragPath.reset();
        this.dragPath.moveTo(this.getWidth(), 0);
        this.dragPath.quadTo(tipX / 3, tipY / 3, tipX, tipY);
        this.dragPath.quadTo(tipX / 3, tipY / 3, this.getWidth(), this.getHeight());
        this.dragPath.close();
        this.invalidate();
    }

    @Override
    public void onTouchUp(ProbObservationTouch obs, int numRemainingPointers) {

        this.core.undetermine();
        this.activated = false;
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


        this.core.addBehaviour("tap: C");
        this.core.addBehaviour("slide_right: C->E");
        this.core.addBehaviour("slide_left: C->W");
        this.core.addRule("bezel: (slide_right on complete and slide_right is most_likely) " +
                "or (slide_left on complete and slide_left is most_likely)", new PMLRuleListener() {
            @Override
            public void onRuleSatisfied(String event, int subsequentCalls) {
                core.claimDetermination();
            }
        });
        this.core.setReady();
    }

    @Override
    public void onExclude() {
    }

    @Override
    public void onSelfExclude() {

    }

    @Override
    public void onDetermined() {
        this.activated = true;
        this.getView().performClick();
        this.core.undetermine();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.core.drawBody(canvas);
    }

}

