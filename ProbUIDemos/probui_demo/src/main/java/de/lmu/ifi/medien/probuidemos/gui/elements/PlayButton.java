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
import android.util.Log;
import android.view.View;
import android.widget.Button;

import de.lmu.ifi.medien.probui.gui.ProbInteractor;
import de.lmu.ifi.medien.probui.gui.ProbInteractorCore;
import de.lmu.ifi.medien.probui.observations.ProbObservation;
import de.lmu.ifi.medien.probui.observations.ProbObservationTouch;
import de.lmu.ifi.medien.probui.pml.PMLRuleListener;


public class PlayButton extends Button implements ProbInteractor {


    public static final int ACTION_PAUSE = 0;
    public static final int ACTION_PLAY = 1;
    public static final int ACTION_FWD = 2;
    public static final int ACTION_BWD = 3;
    public static final int ACTION_FSKP = 4;
    public static final int ACTION_BSKP = 5;

    private static int STATE_PRESS_TO_PLAY = 0;
    private static int STATE_PRESS_TO_PAUSE = 1;
    //private static int STATE_FORWARD = 2;
    //private static int STATE_BACKWARD = 3;


    private ProbInteractorCore core;


    private Paint textPaint;

    private Paint mainPaint;

    private Path pathPlay;

    private int state = STATE_PRESS_TO_PLAY;

    private float tx;
    private float fbx;

    private float ta;
    private float fba;


    boolean forceBack = false;

    private int lastAction = -1;
    private int winding;
    private float tx_old;
    private float fbx_old;
    private float ta_old;
    private float fba_old;


    public PlayButton(Context context) {

        super(context);
        sharedConstructor();
    }

    public PlayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructor();
    }

    public PlayButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        sharedConstructor();
    }


    private void sharedConstructor() {
        this.core = new ProbInteractorCore(this);
        this.core.init();


        textPaint = new Paint();
        textPaint.setColor(Color.rgb(0, 0, 0));
        textPaint.setStrokeWidth(2);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(64);

        mainPaint = new Paint();
        mainPaint.setColor(Color.rgb(0, 0, 0));
        mainPaint.setStrokeWidth(2);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);


        this.core.debugDrawOutline = false;
        this.core.debugDraw = false;


        this.setBackground(null);
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

        this.animUpdate();

        if (this.state == STATE_PRESS_TO_PLAY ||
                tx != 0 && (this.getCore().getIndexPosteriorMax() == 1
                        || this.getCore().getIndexPosteriorMax() == 2
                        || this.getCore().getIndexPosteriorMax() == 3
                        || this.getCore().getIndexPosteriorMax() == 4)) {
            canvas.save();

            canvas.save();
            canvas.rotate(fba, this.getView().getWidth() / 2, this.getView().getHeight() / 2);
            canvas.drawPath(pathPlay, mainPaint);
            canvas.restore();

            canvas.translate(fbx, 0);
            canvas.save();
            canvas.rotate(fba, this.getView().getWidth() / 2, this.getView().getHeight() / 2);
            canvas.drawPath(pathPlay, mainPaint);
            canvas.restore();

            // Long drag (skip):
            if ((this.getCore().getIndexPosteriorMax() == 3 || this.getCore().getIndexPosteriorMax() == 4) && tx != 0) {
                canvas.translate(fbx, 0);
                canvas.save();
                canvas.rotate(fba, this.getView().getWidth() / 2, this.getView().getHeight() / 2);
                canvas.drawRect(0, 0, this.getView().getWidth() / 3, this.getView().getHeight(), mainPaint);
                canvas.restore();
            }

            canvas.restore();

        } else {
            canvas.drawRect(0, 0, this.getView().getWidth() / 3, this.getView().getHeight(), mainPaint);
            canvas.drawRect(this.getView().getWidth() * 2 / 3, 0, this.getView().getWidth(), this.getView().getHeight(), mainPaint);
        }


    }

    @Override
    public void onTouchDown(ProbObservationTouch obs) {


    }

    @Override
    public void onTouchMove(ProbObservationTouch obs) {


    }

    @Override
    public void onTouchUp(ProbObservationTouch obs, int numRemainingPointers) {

        this.winding = 0;

        ta = 0;
        tx = 0;
        forceBack = true;

        this.animUpdate();
        this.invalidate();
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


    public int getLastAction() {
        return this.lastAction;
    }

    public int getWinding() {
        return this.winding;
    }


    @Override
    public String[] getDefaultBehaviours() {

        return null;
    }

    @Override
    public void onCoreObserve(ProbObservation obs) {

        Log.d("PML PLAY BUTTON", "######### most likely: " + this.core.getIndexPosteriorMax());
    }

    @Override
    public void onCoreFinaliseBehaviourSetup() {
        double[] prior = {0.978, 0.01, 0.01, 0.001, 0.001};
        this.core.behavioursPrior = prior;
    }

    @Override
    public void onProbSetup() {
        pathPlay = new Path();
        pathPlay.moveTo(0, 0);
        pathPlay.lineTo(this.getView().getWidth(), this.getView().getHeight() / 2);
        pathPlay.lineTo(0, this.getView().getHeight());
        pathPlay.close();

        this.core.addBehaviour("tap: Cd*u.");
        this.core.addBehaviour("drag_ltr: R->Eu.");
        this.core.addBehaviour("drag_rtl: L->Wu.");
        this.core.addBehaviour("drag_ltr_far: R->EEu.");
        this.core.addBehaviour("drag_rtl_far: L->WWu.");

        this.core.addRule("button_triggered: tap on complete and tap is most_likely", new PMLRuleListener() {
            @Override
            public void onRuleSatisfied(String event, int subsequentCalls) {
                Log.d("PML PLAY BUTTON", "############## tap_triggered!");
                core.claimDetermination();
            }
        });


        this.core.addRule("in_center: tap is most_likely", new PMLRuleListener() {
            @Override
            public void onRuleSatisfied(String event, int subsequentCalls) {
                Log.d("PML PLAY BUTTON", "############## no_winding!");
                winding = 0;
                tx = 0;
                ta = 0;
            }
        });


        this.core.addRule("winding_right: drag_ltr is most_likely", new PMLRuleListener() {
            @Override
            public void onRuleSatisfied(String event, int subsequentCalls) {
                Log.d("PML PLAY BUTTON", "############## winding_right!");
                winding = 1;
                tx = getView().getWidth();
                ta = 0;
            }
        });

        this.core.addRule("winding_left: drag_rtl is most_likely", new PMLRuleListener() {
            @Override
            public void onRuleSatisfied(String event, int subsequentCalls) {
                winding = -1;
                tx = -getView().getWidth();
                ta = 180;
            }
        });
        this.core.addRule("cancel: (drag_ltr on complete and drag_ltr is most_likely)" +
                "or (drag_rtl on complete and drag_rtl is most_likely)", new PMLRuleListener() {
            @Override
            public void onRuleSatisfied(String event, int subsequentCalls) {
                winding = 0;
                core.selfExclude();
            }
        });


        this.core.addRule("preview_skip_prev: drag_rtl_far is most_likely", new PMLRuleListener() {
            @Override
            public void onRuleSatisfied(String event, int subsequentCalls) {
                winding = 0;
                tx = -getView().getWidth();
                ta = 180;
            }
        });

        this.core.addRule("preview_skip_next: drag_ltr_far is most_likely", new PMLRuleListener() {
            @Override
            public void onRuleSatisfied(String event, int subsequentCalls) {
                winding = 0;
                tx = getView().getWidth();
                ta = 0;
            }
        });


        this.core.addRule("skip_prev: drag_rtl_far on complete " +
                "and drag_rtl_far is most_likely", new PMLRuleListener() {
            @Override
            public void onRuleSatisfied(String event, int subsequentCalls) {
                core.claimDetermination();
            }
        });

        this.core.addRule("skip_next: drag_ltr_far on complete " +
                "and drag_ltr_far is most_likely", new PMLRuleListener() {
            @Override
            public void onRuleSatisfied(String event, int subsequentCalls) {
                core.claimDetermination();
            }
        });

        this.core.setReady();
    }

    @Override
    public void onExclude() {
        cancel();
    }

    @Override
    public void onSelfExclude() {
        cancel();
    }

    private void cancel() {
        ta = 0;
        tx = 0;
        forceBack = true;
        this.animUpdate();
    }

    @Override
    public void onDetermined() {

        Log.d("PML PLAY BUTTON", "############## onDetermined!");

        if (this.getCore().getIndexPosteriorMax() == 0) {
            if (this.state == STATE_PRESS_TO_PLAY) {
                this.state = STATE_PRESS_TO_PAUSE;
                this.lastAction = ACTION_PLAY;
            } else if (this.state == STATE_PRESS_TO_PAUSE) {
                this.state = STATE_PRESS_TO_PLAY;
                this.lastAction = ACTION_PAUSE;
            }
        }

        if (this.getCore().getIndexPosteriorMax() == 1) {
            this.lastAction = ACTION_FWD;

        } else if (this.getCore().getIndexPosteriorMax() == 2) {
            this.lastAction = ACTION_BWD;
        } else if (this.getCore().getIndexPosteriorMax() == 3) {
            this.lastAction = ACTION_FSKP;
            this.state = STATE_PRESS_TO_PLAY;
        } else if (this.getCore().getIndexPosteriorMax() == 4) {
            this.lastAction = ACTION_BSKP;
            this.state = STATE_PRESS_TO_PLAY;
        }

        this.getView().performClick();
        this.core.undetermine(); // Since a button is undetermined immediately after it is released.
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.core.drawBody(canvas);
    }


    private void animUpdate() {

        if (forceBack || !this.core.isCandidate()) {
            tx = 0;
            ta = 0;
        }

        if (Math.abs(tx - fbx) > 3) {
            fbx += 0.5 * (tx - fbx) + 1;
        } else {
            fbx = tx;
        }
        if (Math.abs(ta - fba) > 3) {
            fba += 0.6 * (ta - fba);
        } else {
            fba = ta;
        }


        if (ta == ta_old && tx == tx_old && fba == fba_old && fbx == fbx_old) {
            forceBack = false;
        } else {
            this.postInvalidate();
        }

        tx_old = tx;
        fbx_old = fbx;
        ta_old = ta;
        fba_old = fba;
    }


}
