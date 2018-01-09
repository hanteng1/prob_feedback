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
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import de.lmu.ifi.medien.probui.gui.ProbInteractor;
import de.lmu.ifi.medien.probui.gui.ProbInteractorCore;
import de.lmu.ifi.medien.probui.observations.ProbObservation;
import de.lmu.ifi.medien.probui.observations.ProbObservationTouch;


public class AdaptiveSlider extends Button implements ProbInteractor {

    private static final int COLOR_SLIDER_LINE = Color.rgb(80, 80, 80);
    private static final int COLOR_SLIDER_BALL = Color.rgb(140, 180, 255);
    private static final int COLOR_CANDIDATE_SLIDER_BALL = Color.rgb(140, 180, 255);


    /**
     * Core that manages the probabilistic behaviour of this interactor.
     */
    private ProbInteractorCore core;


    protected Paint drawing_slider_paint;
    protected Paint drawing_ball_paint;
    protected Paint drawing_candidate_ball_paint;


    private boolean sliding;

    private int num_points = 50;
    private List<SPoint> points;


    private int slider_pos;
    private int candidate_slider_pos;
    private SBall ball;
    private SBall candidate_ball;
    private boolean bending;

    private float max_bend_out;
    private int last_post_max = -1;

    private float lastTouchUpX;
    private float lastTouchUpY;


    public AdaptiveSlider(Context context) {
        super(context);
        sharedConstructor();
    }

    public AdaptiveSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructor();
    }

    public AdaptiveSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        sharedConstructor();
    }

    private void sharedConstructor() {
        this.core = new ProbInteractorCore(this);
        this.core.init();

        this.core.debugDrawOutline = false;
        this.core.debugDraw = true;

        this.drawing_slider_paint = new Paint();
        this.drawing_slider_paint.setStrokeWidth(7);
        this.drawing_slider_paint.setStyle(Paint.Style.FILL);
        this.drawing_slider_paint.setColor(COLOR_SLIDER_LINE);

        this.drawing_ball_paint = new Paint();
        this.drawing_ball_paint.setStrokeWidth(1);
        this.drawing_ball_paint.setStyle(Paint.Style.FILL);
        this.drawing_ball_paint.setColor(COLOR_SLIDER_BALL);

        this.drawing_candidate_ball_paint = new Paint();
        this.drawing_candidate_ball_paint.setStrokeWidth(10);
        this.drawing_candidate_ball_paint.setStyle(Paint.Style.STROKE);
        this.drawing_candidate_ball_paint.setColor(COLOR_CANDIDATE_SLIDER_BALL);

        this.setBackground(null);


        this.ball = new SBall();
        this.candidate_ball = new SBall();

    }


    private void initPoints() {
        this.points = new ArrayList<SPoint>();
        for (int i = 0; i < this.num_points; i++) {
            float px = i * 1.0f / (this.num_points - 1) * this.getWidth();
            float py = this.getHeight() / 2.0f;
            SPoint p = new SPoint(px, py);
            this.points.add(p);
        }
    }


    public void updateSliderPos(int slider_pos, boolean instant) {
        this.slider_pos = slider_pos;
        this.ball.targetX = this.points.get(this.slider_pos).targetX;
        this.ball.targetY = this.points.get(this.slider_pos).targetY;
        if (instant) {
            this.ball.x = this.ball.targetX;
            this.ball.y = this.ball.targetY;
        }
        this.ball.update();
    }

    public void updateCandidateSliderPos(int slider_pos, boolean instant) {
        this.candidate_slider_pos = slider_pos;
        this.candidate_ball.targetX = this.points.get(this.candidate_slider_pos).targetX;
        this.candidate_ball.targetY = this.points.get(this.candidate_slider_pos).targetY;
        if (instant) {
            this.candidate_ball.x = this.candidate_ball.targetX;
            this.candidate_ball.y = this.candidate_ball.targetY;
        }
        this.candidate_ball.update();
    }


    public int getClosestSliderPosForPoint(float tx, float ty) {

        tx -= this.getX();
        ty -= this.getY();

        double min_dist = 999999;
        int idx_min_dist = -1;
        SPoint p;
        double dx, dy, d;
        for (int i = 0; i < this.num_points; i++) {
            p = this.points.get(i);
            dx = p.x - tx;
            dy = p.y - ty;
            d = Math.sqrt(dx * dx + dy * dy);
            if (d < min_dist) {
                min_dist = d;
                idx_min_dist = i;
            }
        }
        return idx_min_dist;
    }


    public void checkAndPerformSliderMove(float tx, float ty) {

        int new_slider_pos = getClosestSliderPosForPoint(tx, ty);
        if (this.core.isDetermined())
            this.updateSliderPos(new_slider_pos, false);
        if (this.core.isCandidate())
            this.updateCandidateSliderPos(new_slider_pos, false);
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


        for (SPoint p : this.points) {
            p.update();
        }
        this.ball.update();
        this.candidate_ball.update();

        // Determine colour and alpha for the bending line:
        if (this.core.isCandidate() && !this.core.isDetermined()) {
            this.drawing_slider_paint.setColor(COLOR_CANDIDATE_SLIDER_BALL);
            this.drawing_slider_paint.setAlpha((int) (255 * this.core.getCandidateProb()));
            this.drawing_candidate_ball_paint.setAlpha((int) (255 * this.core.getCandidateProb()));
        } else {
            this.drawing_slider_paint.setColor(COLOR_SLIDER_LINE);
            this.drawing_slider_paint.setAlpha(255);
        }

        // Draw points/line:
        SPoint pprev = this.points.get(0);
        SPoint cp = null;
        for (int i = 1; i < this.points.size(); i++) {
            cp = this.points.get(i);
            canvas.drawLine(pprev.x, pprev.y, cp.x, cp.y, this.drawing_slider_paint);
            pprev = cp;
        }


        // Draw candidate slider ball:
        if (this.core.isCandidate() && !this.core.isDetermined()) {
            this.drawing_slider_paint.setAlpha(255);
            this.drawing_slider_paint.setColor(COLOR_SLIDER_LINE);
            canvas.drawLine(0, this.getHeight() / 2, this.getWidth(), this.getHeight() / 2, this.drawing_slider_paint);
            canvas.drawCircle(this.candidate_ball.x, this.candidate_ball.y, this.candidate_ball.size, this.drawing_candidate_ball_paint);
        }

        // Draw slider ball:
        canvas.drawCircle(this.ball.x, this.ball.y, this.ball.size, this.drawing_ball_paint);
    }

    @Override
    public void onTouchDown(ProbObservationTouch obs) {

        float tx = (float) obs.getRealFeatures()[ProbObservationTouch.FEATURE_X] * this.core.getSurfaceWidth();
        float ty = (float) obs.getRealFeatures()[ProbObservationTouch.FEATURE_Y] * this.core.getSurfaceHeight();

        Log.d("SLIDER", "touch DOWN: " + tx + ", " + ty);

        this.sliding = true;
        this.checkAndPerformSliderMove(tx, ty);
    }

    @Override
    public void onTouchMove(ProbObservationTouch obs) {

        float tx = (float) obs.getRealFeatures()[ProbObservationTouch.FEATURE_X] * this.core.getSurfaceWidth();
        float ty = (float) obs.getRealFeatures()[ProbObservationTouch.FEATURE_Y] * this.core.getSurfaceHeight();
        Log.d("SLIDER", "touch MOVE: " + tx + ", " + ty);
        if (this.core.isCandidate()) {
            this.sliding = true;
            this.checkAndPerformSliderMove(tx, ty);
            this.updateBending(tx, ty);
        } else {
            this.unbend(false);
        }
    }

    @Override
    public void onTouchUp(ProbObservationTouch obs, int numRemainingPointers) {

        this.lastTouchUpX = (float) obs.getRealFeatures()[ProbObservationTouch.FEATURE_X] * this.core.getSurfaceWidth();
        this.lastTouchUpY = (float) obs.getRealFeatures()[ProbObservationTouch.FEATURE_Y] * this.core.getSurfaceHeight();
        if (this.core.getCandidateProb() > 0.6)
            this.core.claimDetermination();
        else {
            this.unbend(true);
            this.core.selfExclude();
        }
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

        this.core.addBehaviour("centre: CY");
        this.core.addBehaviour("arc_bottom_left: SL<->SSY");
        this.core.addBehaviour("arc_bottom_right: SR<->SSY");
        this.core.addBehaviour("arc_top_left: NL<->NNY");
        this.core.addBehaviour("arc_top_right: NR<->NNY");
        this.core.setReady();

        this.initPoints();
        this.updateSliderPos(0, true);
        this.max_bend_out = this.core.getSurfaceHeight() * .35f;
    }

    @Override
    public void onExclude() {
        this.unbend(true);
        this.last_post_max = -1;
    }

    @Override
    public void onSelfExclude() {
        this.unbend(false);
        this.last_post_max = -1;
    }

    @Override
    public void onDetermined() {

        this.checkAndPerformSliderMove(this.lastTouchUpX, this.lastTouchUpY);
        this.sliding = false;
        this.core.undetermine();
        this.unbend(false);
        this.last_post_max = -1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.core.drawBody(canvas);
    }


    private void updateBending(float tx, float ty) {

        int post_max = this.core.getIndexPosteriorMax();

        if (this.last_post_max == post_max || this.bending) {
            return;
        }

        Log.d("SLIDER_BENDING", "post_max: " + post_max);

        this.last_post_max = post_max;
        this.bending = post_max != 0;

        float start_angle = 0;
        float bend_vertical_sign = 0;
        float bend_horizontal_sign = 0;
        float bend_angle_rotation_sign = 0;
        float y_offset = 0;
        if (post_max == 0) { // centre
            unbend(false);
            return;
        } else if (post_max == 1) {   // bottom left
            start_angle = -90;
            bend_vertical_sign = 1;
            bend_angle_rotation_sign = 1;
            bend_horizontal_sign = 1;
            y_offset = 0;
        } else if (post_max == 2) { // bottom right
            start_angle = -90;
            bend_vertical_sign = 1;
            bend_angle_rotation_sign = 1;
            bend_horizontal_sign = -1;
            y_offset = 0;
        } else if (post_max == 3) {   // top left
            start_angle = -90;
            bend_vertical_sign = -1;
            bend_angle_rotation_sign = 1;
            bend_horizontal_sign = 1;
            y_offset = -2;
        } else if (post_max == 4) { // top right
            start_angle = -90;
            bend_vertical_sign = -1;
            bend_angle_rotation_sign = 1;
            bend_horizontal_sign = -1;
            y_offset = -2;
        }


        SPoint cp = null;
        float deg = 90;
        if (post_max == 1 || post_max == 3) {
            for (int i = 0; i < this.points.size(); i++) {
                cp = this.points.get(i);
                if (i >= this.candidate_slider_pos) {
                    float r = Math.min(this.max_bend_out, (this.getWidth() - this.points.get(this.candidate_slider_pos).baseX) * 0.5f); // compute radius as remaining space, limited by max "bend allowance"
                    float ratio = (i - this.candidate_slider_pos) * 1.0f / (this.num_points - this.candidate_slider_pos - 1); // compute ratio of index in remaining slider points

                    double a = bend_angle_rotation_sign * Math.toRadians(start_angle + ratio * deg); // compute angle
                    cp.targetX = this.points.get(this.candidate_slider_pos).baseX + bend_horizontal_sign * (float) (r * Math.cos(a)); // draw circle, x shifted to right by first bend point.baseX
                    cp.targetY = r + y_offset * r + this.getHeight() * 0.5f + bend_vertical_sign * (float) (r * Math.sin(a)); // draw circle, y shifted by radius (and vertical slider centre, because line is centered on slider)
                } else {
                    cp.targetX = cp.baseX;
                    cp.targetY = cp.baseY;
                }
            }

        } else if (post_max == 2 || post_max == 4) {
            for (int i = this.points.size() - 1; i >= 0; i--) {
                cp = this.points.get(i);
                if (i <= this.candidate_slider_pos) {
                    float r = Math.min(this.max_bend_out, this.points.get(this.candidate_slider_pos).baseX * 0.5f); // compute radius as remaining space, limited by max "bend allowance"
                    float ratio = (this.candidate_slider_pos - i) * 1.0f / (this.candidate_slider_pos - 1); // compute ratio of index in remaining slider points

                    double a = bend_angle_rotation_sign * Math.toRadians(start_angle + ratio * deg); // compute angle
                    cp.targetX = this.points.get(this.candidate_slider_pos).baseX + bend_horizontal_sign * (float) (r * Math.cos(a)); // draw circle, x shifted to right by first bend point.baseX
                    cp.targetY = r + y_offset * r + this.getHeight() * 0.5f + bend_vertical_sign * (float) (r * Math.sin(a)); // draw circle, y shifted by radius (and vertical slider centre, because line is centered on slider)
                } else {
                    cp.targetX = cp.baseX;
                    cp.targetY = cp.baseY;
                }
            }
        }


    }


    private void unbend(boolean instant) {

        this.bending = false;
        SPoint cp = null;
        for (int i = 0; i < this.num_points; i++) {
            cp = this.points.get(i);
            cp.targetX = cp.baseX;
            cp.targetY = cp.baseY;
            if (instant) {
                cp.x = cp.targetX;
                cp.y = cp.targetY;
            }
        }
        updateSliderPos(this.slider_pos, false);
    }


    private class SBall {

        float x;
        float y;
        float targetX;
        float targetY;
        float size = 50;

        public void update() {

            // Update slider ball:
            // Move:
            float dx = this.targetX - this.x;
            float dy = this.targetY - this.y;
            if (Math.abs(dx * dx + dy * dy) > 20) {
                this.x += sliding ? dx : Math.signum(dx) * 1 + 0.15 * dx;
                this.y += sliding ? dy : Math.signum(dy) * 1 + 0.15 * dy;
                AdaptiveSlider.this.postInvalidate();
            }
            // Snap to target:
            else {
                this.x = this.targetX;
                this.y = this.targetY;
            }
        }
    }


    private class SPoint {

        float baseX;
        float baseY;
        float x;
        float y;

        float targetX;
        float targetY;

        public SPoint(float x, float y) {

            this.x = x;
            this.y = y;
            this.baseX = x;
            this.baseY = y;
            this.targetX = this.x;
            this.targetY = this.y;
        }


        public void update() {

            // Move:
            float dx = this.targetX - this.x;
            float dy = this.targetY - this.y;
            if (Math.abs(dx * dx + dy * dy) > 10) {
                this.x += Math.signum(dx) * 1 + 0.15 * dx;
                this.y += Math.signum(dy) * 1 + 0.15 * dy;
                AdaptiveSlider.this.postInvalidate();
            }
            // Snap to target:
            else {
                this.x = this.targetX;
                this.y = this.targetY;
            }
        }
    }
}
