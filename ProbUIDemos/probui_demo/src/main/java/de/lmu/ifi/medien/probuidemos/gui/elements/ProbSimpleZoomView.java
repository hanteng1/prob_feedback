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
import android.util.AttributeSet;
import android.view.View;

import de.lmu.ifi.medien.probui.gui.ProbInteractor;
import de.lmu.ifi.medien.probui.gui.ProbInteractorCore;
import de.lmu.ifi.medien.probui.observations.ProbObservation;
import de.lmu.ifi.medien.probui.observations.ProbObservationTouch;
import de.lmu.ifi.medien.probui.pml.PMLRuleListener;

public class ProbSimpleZoomView extends View implements ProbInteractor {

    private static final int ZOOM_MODE_NONE = -1;
    private static final int ZOOM_MODE_IN = 0;
    private static final int ZOOM_MODE_OUT = 1;

    private ProbInteractorCore core;

    private float zoomFactor = 1;
    private float targetZoomFactor = 1;

    private Paint shapePaint;
    private int zoomMode;

    private float scale = getContext().getResources().getDisplayMetrics().density;
    private float shapeSize = 50;


    public ProbSimpleZoomView(Context context) {
        super(context);
        sharedConstructor();
    }

    public ProbSimpleZoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructor();
    }

    public ProbSimpleZoomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        sharedConstructor();
    }

    private void sharedConstructor() {

        this.shapePaint = new Paint();
        this.shapePaint.setStrokeWidth(5);
        this.shapePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.shapePaint.setColor(Color.rgb(0, 207, 165));

        this.core = new ProbInteractorCore(this);
        this.core.init();
        this.core.debugDrawOutline = false;
        this.core.debugDraw = true;
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

        canvas.save();

        // Move to centre -  all subsequent transformations should be centred:
        canvas.translate(this.getWidth() / 2, this.getHeight() / 2);

        // Apply zoom:
        canvas.scale(this.zoomFactor, this.zoomFactor);

        // Draw the shape:
        canvas.drawRect(-this.shapeSize / 2 * this.scale, -this.shapeSize / 2 * this.scale,
                this.shapeSize / 2 * this.scale, this.shapeSize / 2 * this.scale, this.shapePaint);

        canvas.restore();

        this.updateZoom();
    }

    private void updateZoom() {

        float dz = this.targetZoomFactor - this.zoomFactor;
        this.zoomFactor += dz * 0.1f;
        if (Math.abs(this.targetZoomFactor - this.zoomFactor) < 0.01f) {
            this.zoomFactor = this.targetZoomFactor;
        }

        this.invalidate();
    }


    @Override
    public void onTouchDown(ProbObservationTouch obs) {

    }

    @Override
    public void onTouchMove(ProbObservationTouch obs) {

    }

    @Override
    public void onTouchUp(ProbObservationTouch obs, int numRemainingPointers) {

        //if(this.core.isCandidate() && this.zoomMode == ZOOM_MODE_NONE)
        //this.core.undetermine();
        this.core.selfExclude();
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

        /*
        this.core.addBehaviour("rubbing_in: O[w=60,h=60]->C->R->C");
        this.core.addRule("zoom_in: rubbing_in on complete and rubbing_in is most_likely", new PMLRuleListener() {
            @Override
            public void onRuleSatisfied(String event, int subsequentCalls) {
                //Log.d("PML EVENT TYPES TEST", "############################## tadaaaa rubbing out!");
                zoomMode = ZOOM_MODE_IN;
                core.claimDetermination();
            }

            @Override
            public void onRuleDissatisfied(String event, int subsequentCalls) {
            }
        });

        this.core.addBehaviour("rubbing_out: O[w=60,h=60]->C->L->C");
        this.core.addRule("zoom_out: rubbing_out on complete and rubbing_out is most_likely", new PMLRuleListener() {
            @Override
            public void onRuleSatisfied(String event, int subsequentCalls) {
                //Log.d("PML EVENT TYPES TEST", "############################## tadaaaa in!");
                zoomMode = ZOOM_MODE_OUT;
                core.claimDetermination();
            }

            @Override
            public void onRuleDissatisfied(String event, int subsequentCalls) {
            }
        });
        */



        this.core.addBehaviour("rubbing_in: O[w=60,h=60]<->NE");
        this.core.addRule("zoom_in: rubbing_in on complete and rubbing_in is most_likely", new PMLRuleListener() {
            @Override
            public void onRuleSatisfied(String event, int subsequentCalls) {
                //Log.d("PML EVENT TYPES TEST", "############################## tadaaaa rubbing out!");
                zoomMode = ZOOM_MODE_IN;
                core.claimDetermination();
            }
        });

        this.core.addBehaviour("rubbing_out: O[w=60,h=60]<->NW");
        this.core.addRule("zoom_out: rubbing_out on complete and rubbing_out is most_likely", new PMLRuleListener() {
            @Override
            public void onRuleSatisfied(String event, int subsequentCalls) {
                //Log.d("PML EVENT TYPES TEST", "############################## tadaaaa in!");
                zoomMode = ZOOM_MODE_OUT;
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

        if (zoomMode == ZOOM_MODE_IN) {
            this.zoomIn();
        } else {
            this.zoomOut();
        }

        this.zoomMode = ZOOM_MODE_NONE;
        this.core.undetermine();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.core.drawBody(canvas);
    }


    private void zoomIn() {
        this.targetZoomFactor *= 1.35;
        this.targetZoomFactor = Math.min(5, this.targetZoomFactor);
    }

    private void zoomOut() {
        this.targetZoomFactor *= 0.65;
        this.targetZoomFactor = Math.max(0.1f, this.targetZoomFactor);
    }

}
