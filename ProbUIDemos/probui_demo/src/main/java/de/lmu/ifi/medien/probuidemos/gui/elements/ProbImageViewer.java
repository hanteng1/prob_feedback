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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import de.lmu.ifi.medien.probuidemos.R;
import de.lmu.ifi.medien.probui.gui.ProbInteractor;
import de.lmu.ifi.medien.probui.gui.ProbInteractorCore;
import de.lmu.ifi.medien.probui.observations.ProbObservation;
import de.lmu.ifi.medien.probui.observations.ProbObservationTouch;
import de.lmu.ifi.medien.probui.pml.PMLRuleListener;

public class ProbImageViewer extends View implements ProbInteractor {

    private ProbInteractorCore core;
    private Bitmap img;
    private float[] cursorX;
    private float[] cursorY;
    private float refPointX;
    private float refPointY;
    private boolean oneHandControlsActive;
    private boolean panningActive;

    private Paint ctrlPaint;

    private float zoomFactor = 1;
    private float initialControlLength;
    private float rotationAngle = 0;
    private float initialControlAngle;


    private float currentZoomFactor = 1;
    private float currentRotationAngle = 0;


    private float panningX;
    private float panningY;

    private float currentPanningX;
    private float currentPanningY;

    private boolean twoFingerControlsActive;
    private int zoomDirection;


    public ProbImageViewer(Context context) {
        super(context);
        sharedConstructor();
    }

    public ProbImageViewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructor();
    }

    public ProbImageViewer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        sharedConstructor();
    }

    private void sharedConstructor() {

        this.ctrlPaint = new Paint();
        this.ctrlPaint.setStrokeWidth(14);
        this.ctrlPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.ctrlPaint.setColor(Color.argb(200, 100, 100, 100));

        this.core = new ProbInteractorCore(this);
        this.core.init();
        this.core.debugDrawOutline = false;
        this.core.debugDraw = false;

        this.cursorX = new float[5];
        this.cursorY = new float[5];
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

        // Apply panning:
        canvas.translate(this.currentPanningX, this.currentPanningY);


        // Move to centre -  all subsequent transformations should be centred:
        canvas.translate(this.getWidth() / 2, this.getHeight() / 2);

        // Apply rotation:
        canvas.rotate((float) Math.toDegrees(this.currentRotationAngle));

        // Apply zoom:
        canvas.scale(this.currentZoomFactor, this.currentZoomFactor);

        // Draw the image:
        canvas.drawBitmap(this.img, -this.img.getWidth() / 2, -this.img.getHeight() / 2, null);

        canvas.restore();


        if (this.oneHandControlsActive) {

            // Draw line:
            canvas.drawLine(this.refPointX, this.refPointY, this.cursorX[0], this.cursorY[0], this.ctrlPaint);

            // Draw ref circle:
            canvas.drawCircle(this.refPointX, this.refPointY, this.getWidth() / 20, this.ctrlPaint);

            // Draw cursor circle:
            canvas.drawCircle(this.cursorX[0], this.cursorY[0], this.getWidth() / 20, this.ctrlPaint);
        }

    }


    @Override
    public void onTouchDown(ProbObservationTouch obs) {
        updateCursor(obs);
    }

    @Override
    public void onTouchMove(ProbObservationTouch obs) {
    }
    
    @Override
    public void onTouchUp(ProbObservationTouch obs, int numRemainingPointers) {
        if (this.core.isCandidate() && numRemainingPointers == 0
                && (this.panningActive || this.oneHandControlsActive || this.twoFingerControlsActive)) {
            this.core.claimDetermination();
        } else if (this.core.isCandidate() && numRemainingPointers == 0) {
            this.core.selfExclude(300);
        }
    }


    @Override
    public void onTouchDownPost(ProbObservationTouch obs) {
    }

    @Override
    public void onTouchMovePost(ProbObservationTouch obs) {

        if (this.panningActive) {
            updatePanning(obs);
        }

        updateCursor(obs);

        if (this.oneHandControlsActive) {
            updateZoomFactor();
            updateRotationAngle();
        }

        if (this.twoFingerControlsActive) {
            updateReferenceForTwoFingerControls();
            updateZoomFactor();
            updateRotationAngle();
        }
    }

    @Override
    public void onTouchUpPost(ProbObservationTouch obs, int numRemainingPointers) {

    }

    private void updateReferenceForTwoFingerControls() {
        this.refPointX = this.cursorX[1];
        this.refPointY = this.cursorY[1];
    }

    private void updatePanning(ProbObservationTouch obs) {
        float dx = (float) obs.getRealFeatures()[ProbObservationTouch.FEATURE_X] * this.core.getSurfaceWidth() - this.cursorX[0];
        float dy = (float) obs.getRealFeatures()[ProbObservationTouch.FEATURE_Y] * this.core.getSurfaceHeight() - this.cursorY[0];
        this.currentPanningX += dx;
        this.currentPanningY += dy;
        Log.d("PROB IMAGE VIEWER", "panning: " + this.panningX + ", " + this.panningY);
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

        this.initImage();

        this.core.addBehaviour("tatap: Cdu->Bd");
        this.core.addRule("one_hand_controls: tatap on complete in <300 ms using 1 fingers",
                new PMLRuleListener() {
                    @Override
                    public void onRuleSatisfied(String event, int subsequentCalls) {
                        Log.d("PROB IMAGE VIEWER", "one_hand_controls");
                        ProbImageViewer.this.core.resetSelfExcluded();
                        ProbImageViewer.this.cancelPanning();
                        ProbImageViewer.this.cancelTwoFingerControls();
                        ProbImageViewer.this.launchOneHandControls();
                    }
                });


        this.core.addBehaviour("double_tap: Cdudu");
        this.core.addRule("reset_view: double_tap on complete in <300 ms using 1 fingers",
                new PMLRuleListener() {
                    @Override
                    public void onRuleSatisfied(String event, int subsequentCalls) {
                        ProbImageViewer.this.resetView();
                    }
                });

        this.core.addBehaviour("centre: Cdm");
        this.core.addRule("panning: centre on complete using 1 fingers",
                new PMLRuleListener() {
                    @Override
                    public void onRuleSatisfied(String event, int subsequentCalls) {
                        Log.d("PROB IMAGE VIEWER", "panning");
                        if (!ProbImageViewer.this.oneHandControlsActive) {
                            ProbImageViewer.this.cancelOneHandControls();
                            ProbImageViewer.this.cancelTwoFingerControls();
                            ProbImageViewer.this.launchPanning();
                        }
                    }
                });


        this.core.addRule("two_finger_control: centre on complete using 2 fingers",
                new PMLRuleListener() {
                    @Override
                    public void onRuleSatisfied(String event, int subsequentCalls) {
                        Log.d("PROB IMAGE VIEWER", "two finger zoom");
                        //if (!ProbImageViewer.this.twoFingerControlsActive) {
                        ProbImageViewer.this.cancelOneHandControls();
                        ProbImageViewer.this.cancelPanning();
                        ProbImageViewer.this.launchTwoFingerControls();
                        //}
                    }
                });

        this.core.setReady();
    }

    private void resetView() {
        this.currentZoomFactor = 1;
        this.currentRotationAngle = 0;
        this.currentPanningX = 0;
        this.currentPanningY = 0;
        this.applyTransformations();
    }

    private void cancelTwoFingerControls() {
        this.twoFingerControlsActive = false;
    }

    private void launchTwoFingerControls() {

        Log.d("PROB IMAGE VIEWER", "launchTwoFingerControls()");

        this.twoFingerControlsActive = true;
        this.zoomDirection = 1;

        this.updateReferenceForTwoFingerControls();
        this.initialControlLength = getFingerDistance();
        this.initialControlAngle = getCursorAngleToReference();

        Log.d("PROB IMAGE VIEWER", "initialControlLength: " + this.initialControlLength);

    }

    private float getFingerDistance() {
        float dx = this.cursorX[0] - this.cursorX[1];
        float dy = this.cursorY[0] - this.cursorY[1];
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private void cancelPanning() {
        this.panningActive = false;
    }

    private void launchPanning() {
        this.panningActive = true;
        this.currentPanningX = this.panningX;
        this.currentPanningY = this.panningY;
    }


    private void updateCursor(ProbObservationTouch obs) {
        this.cursorX[obs.getNominalFeatures()[1]] = (float) obs.getRealFeatures()[ProbObservationTouch.FEATURE_X] * this.core.getSurfaceWidth();
        this.cursorY[obs.getNominalFeatures()[1]] = (float) obs.getRealFeatures()[ProbObservationTouch.FEATURE_Y] * this.core.getSurfaceHeight();
    }

    private float getCursorDistanceToReference() {
        float dx = this.cursorX[0] - this.refPointX;
        float dy = this.cursorY[0] - this.refPointY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }


    public float getCursorAngleToReference() {
        float dx = this.cursorX[0] - this.refPointX;
        float dy = this.cursorY[0] - this.refPointY;
        return (float) Math.atan2(dy, dx);
    }

    private void updateZoomFactor() {
        this.currentZoomFactor = this.zoomFactor + this.zoomDirection * (getCursorDistanceToReference() - this.initialControlLength) / (this.getWidth() * 0.1f);
        this.currentZoomFactor = Math.max(0.15f, this.currentZoomFactor);
    }


    private void updateRotationAngle() {
        this.currentRotationAngle = this.rotationAngle + (getCursorAngleToReference() - this.initialControlAngle);
        if (Math.abs(Math.toDegrees(this.currentRotationAngle)) < 10) {
            this.currentRotationAngle = 0;
        }
    }

    private void launchOneHandControls() {

        this.oneHandControlsActive = true;
        this.refPointX = this.getWidth() / 2f;
        this.refPointY = this.getHeight() / 2f;

        this.zoomDirection = -1;

        this.initialControlLength = getCursorDistanceToReference();
        this.initialControlAngle = getCursorAngleToReference();
    }

    private void cancelOneHandControls() {
        this.oneHandControlsActive = false;
    }

    private void initImage() {

        Log.d("PROB IMAGE VIEWER", "prob image viewer: size: " + this.getWidth() + ", " + this.getHeight());

        this.img = BitmapFactory.decodeResource(
                getResources(), R.drawable.confirm_demo_image_s2);
        float scaleFactor = this.getWidth() * 1.0f / this.img.getWidth();
        this.img = Bitmap.createScaledBitmap(img, this.getWidth(), (int) (scaleFactor * this.img.getHeight()), false);
    }


    private void applyTransformations() {
        this.zoomFactor = this.currentZoomFactor;
        this.rotationAngle = this.currentRotationAngle;
        this.panningX = this.currentPanningX;
        this.panningY = this.currentPanningY;
    }


    private void stopTranforming() {
        this.cancelOneHandControls();
        this.cancelPanning();
        this.cancelTwoFingerControls();
    }

    @Override
    public void onExclude() {
        this.stopTranforming();
        // cancel image transformations
    }

    @Override
    public void onSelfExclude() {
        this.stopTranforming();
        // cancel image transformations
    }

    @Override
    public void onDetermined() {
        this.applyTransformations();
        this.stopTranforming();
        // apply image transformations, i.e. make them permanent
        this.core.undetermine();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.core.drawBody(canvas);
    }

}
