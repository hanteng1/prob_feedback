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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

import de.lmu.ifi.medien.probuidemos.R;
import de.lmu.ifi.medien.probui.gui.ProbUIContainerRelative;

public class MySwatchContainer extends ProbUIContainerRelative {

    private static final int MAX_TRACES = 100;
    private List<MyProbUISwatch> swatches;
    private int color;

    private float last_x;
    private float last_y;

    private List<Trace> traces;
    private Paint trace_paint;

    private class Trace {
        int color;
        float last_x, last_y, x, y;

        public Trace(float x, float y, float last_x, float last_y, int color) {
            this.x = x;
            this.y = y;
            this.last_x = last_x;
            this.last_y = last_y;
            this.color = color;
        }
    }

    public MySwatchContainer(Context context) {
        super(context);
        init();
    }

    public MySwatchContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MySwatchContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        this.traces = new ArrayList<Trace>();
        this.trace_paint = new Paint();
        this.trace_paint.setStrokeCap(Paint.Cap.ROUND);
        this.trace_paint.setStrokeJoin(Paint.Join.ROUND);
    }

    public void initSwatches() {
        this.swatches = new ArrayList<MyProbUISwatch>();
        this.swatches.add((MyProbUISwatch) findViewById(R.id.swatch1));
        this.swatches.add((MyProbUISwatch) findViewById(R.id.swatch2));
        this.swatches.add((MyProbUISwatch) findViewById(R.id.swatch3));
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            this.updateColor();
            this.traces.add(new Trace(ev.getX(), ev.getY(), this.last_x, this.last_y, this.color));
        } else if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            this.traces.clear();
        }
        if (this.traces.size() > MAX_TRACES)
            this.traces.remove(0);
        this.last_x = ev.getX();
        this.last_y = ev.getY();
        this.invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Trace trace;
        for (int i = 1; i < this.traces.size(); i++) {
            trace = this.traces.get(i);
            this.trace_paint.setColor(trace.color);
            float dx = trace.x - trace.last_x;
            float dy = trace.y - trace.last_y;
            float base = this.swatches.get(0).getWidth();
            float r = (float) Math.min(base * 0.35f,
                    base * 0.15f + (10 / Math.sqrt(dx * dx + dy * dy)) * base * 0.35f);
            this.trace_paint.setStrokeWidth(2 * r);
            canvas.drawLine(trace.last_x, trace.last_y, trace.x, trace.y, this.trace_paint);
        }
    }


    /* =============================================================================================
    Task D.2 - Please insert your code below:
    ============================================================================================= */

    // Step 3: Complete the code below to mix the colour based on the swatches' probabilities
    private void updateColor() {
        double red = 0;
        double green = 0;
        double blue = 0;
        for (MyProbUISwatch swatch : this.swatches) {
            double prob = swatch.getCore().getCandidateProb();
            red += prob * swatch.getRed();
            green += prob * swatch.getGreen();
            blue += prob * swatch.getBlue();
        }
        this.color = Color.rgb((int) red, (int) green, (int) blue);
    }

    /* =============================================================================================
    End of Task D.2
    ============================================================================================= */


}
