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

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.lmu.ifi.medien.probuidemos.R;
import de.lmu.ifi.medien.probui.gui.ProbInteractor;
import de.lmu.ifi.medien.probui.gui.ProbInteractorCore;
import de.lmu.ifi.medien.probui.observations.ProbObservation;
import de.lmu.ifi.medien.probui.observations.ProbObservationTouch;
import de.lmu.ifi.medien.probui.pml.PMLRuleListener;

public class ProbList extends View implements ProbInteractor {


    public static final String[] names = {"J. S. Bach", "B. Bartók", "L. Van Beethoven",
            "F. Chopin", "A. Dvorak", "E. Grieg", "G. F. Händl", "F. Liszt", "W. A. Mozart",
            "C. Orff", "M. Ravel", "A. Salieri", "F. Schubert", "J. Sibelius", "P. Tschaikowsky",
            "G. Verdi", "R. Wagner"};


    private ProbInteractorCore core;


    private List<MyListItem> items;

    private Paint textPaint, bgPaint;
    private Bitmap imgPerson;

    private Bitmap imgPhone;
    private Bitmap imgMail;

    private float scroll;
    private float targetScroll;

    private float lastTouchY;


    private float scale = getContext().getResources().getDisplayMetrics().density;
    private int upItemIndex;
    private int downItemIndex;
    private float scrollAtTouchDown;
    private float maxScroll;
    private int openMode;

    public ProbList(Context context) {
        super(context);
        sharedConstructor();
    }

    public ProbList(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructor();
    }

    public ProbList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        sharedConstructor();
    }

    private void sharedConstructor() {


        this.items = new ArrayList<MyListItem>();

        this.textPaint = new Paint();
        this.textPaint.setStrokeWidth(2);
        this.textPaint.setTextSize((20 * scale + 0.5f));
        this.textPaint.setStyle(Paint.Style.FILL);
        this.textPaint.setColor(Color.BLACK);


        this.bgPaint = new Paint();
        this.bgPaint.setStyle(Paint.Style.FILL);
        this.bgPaint.setColor(Color.LTGRAY);

        this.imgPerson = BitmapFactory.decodeResource(
                getResources(), R.drawable.demo_list_items_person);
        this.imgPerson = Bitmap.createScaledBitmap(imgPerson,
                (int) (MyListItem.BASE_HEIGHT * ProbList.this.scale + 0.5f),
                (int) (MyListItem.BASE_HEIGHT * ProbList.this.scale + 0.5f), false);


        this.imgPhone = BitmapFactory.decodeResource(
                getResources(), R.drawable.demo_list_items_phone);
        this.imgPhone = Bitmap.createScaledBitmap(imgPhone,
                (int) (0.5f * MyListItem.BASE_HEIGHT * ProbList.this.scale + 0.5f),
                (int) (0.5f * MyListItem.BASE_HEIGHT * ProbList.this.scale + 0.5f), false);

        this.imgMail = BitmapFactory.decodeResource(
                getResources(), R.drawable.demo_list_items_mail);
        this.imgMail = Bitmap.createScaledBitmap(imgMail,
                (int) (0.5f * MyListItem.BASE_HEIGHT * ProbList.this.scale + 0.5f),
                (int) (0.5f * MyListItem.BASE_HEIGHT * ProbList.this.scale + 0.5f), false);

        this.core = new ProbInteractorCore(this);
        this.core.init();
        this.core.debugDrawOutline = false;
        this.core.debugDraw = true;

    }


    private void initList() {

        // Create some list entries:
        int numItems = names.length;
        float itemHeight = MyListItem.BASE_HEIGHT * scale + 0.5f;
        for (int i = 0; i < numItems; i++) {
            MyListItem item = new MyListItem(i, names[i]);
            item.setLocationY(itemHeight * i);
            this.items.add(item);
        }
        this.maxScroll = numItems * itemHeight - this.getHeight();
        Log.d("SCROLL DEBUG", "maxScroll: " + this.maxScroll);
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


        this.doScrollStep();

        // Draw the list items:
        for (MyListItem item : this.items) {
            item.draw(canvas);
        }

    }

    @Override
    public void onTouchDown(ProbObservationTouch obs) {

        this.scrollAtTouchDown = this.scroll;
        this.downItemIndex = findTouchedItem((float) obs.getRealFeatures()[0], (float) obs.getRealFeatures()[1]);
        this.items.get(this.downItemIndex).onPressed();
        this.lastTouchY = (float) obs.getRealFeatures()[ProbObservationTouch.FEATURE_Y];

    }

    @Override
    public void onTouchMove(ProbObservationTouch obs) {

        if(this.getCore().getCandidateProb() < 0.9) return;

        float y = (float) obs.getRealFeatures()[ProbObservationTouch.FEATURE_Y];
        this.updateTargetScroll(y);
        this.lastTouchY = y;
        if (findTouchedItem((float) obs.getRealFeatures()[0], (float) obs.getRealFeatures()[1]) != this.downItemIndex) {
            this.items.get(this.downItemIndex).onReleased();
        }
    }


    @Override
    public void onTouchUp(ProbObservationTouch obs, int numRemainingPointers) {


        this.upItemIndex = findTouchedItem((float) obs.getRealFeatures()[0], (float) obs.getRealFeatures()[1]);
        if (this.core.isCandidate() && numRemainingPointers == 0) {
            this.items.get(this.downItemIndex).onReleased();
            if (this.downItemIndex == this.upItemIndex && this.upItemIndex != -1
                    && Math.abs(this.scrollAtTouchDown - this.scroll) < 10) {
                if(this.getCore().getCandidateProb() > 0.9)
                    core.claimDetermination();
            } else {
                core.selfExclude();
            }
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

    private void updateTargetScroll(float y) {

        float dy = y - this.lastTouchY;
        this.targetScroll += dy * 3000; // CD gain
    }

    private void doScrollStep() {

        if (this.targetScroll > 0 && this.targetScroll < 1)
            this.targetScroll = 0;

        if (this.scroll > 0 && this.targetScroll > 0) {
            this.targetScroll *= 0.5f;
        }


        if (this.targetScroll < -this.maxScroll && this.targetScroll > -this.maxScroll - 1)
            this.targetScroll = -this.maxScroll;

        if (this.scroll < -this.maxScroll && this.targetScroll < -this.maxScroll) {
            this.targetScroll = -this.maxScroll - Math.abs(-this.maxScroll - this.targetScroll) * 0.5f;
        }

        float dScroll = (this.targetScroll - this.scroll);
        if (Math.abs(dScroll) < 0.1f) // snap to target
            this.scroll = this.targetScroll;
        else { // scroll towards target
            this.scroll += dScroll * 0.3f;
            this.invalidate();
        }


        Log.d("PROB LIST SCROLL", "scroll: " + this.scroll + ", " + this.targetScroll);

        this.updateListItems();

    }

    private void updateListItems() {

        boolean oneMoved = false;
        for (int i = 0; i < this.items.size(); i++) {
            MyListItem item = this.items.get(i);
            item.setLocationY(this.scroll + (MyListItem.BASE_HEIGHT * i * scale + 0.5f));
            oneMoved = item.doStep() || oneMoved;
        }
        if (oneMoved)
            this.invalidate();
    }


    private int findTouchedItem(float x, float y) {

        x *= this.core.getSurfaceWidth();
        y *= this.core.getSurfaceHeight();

        for (MyListItem item : this.items) {
            if (item.pointInItem(x, y)) {
                if (item.pointInPhone(x, y))
                    this.openMode = 1;
                else if (item.pointInMail(x, y))
                    this.openMode = 2;
                else
                    this.openMode = 0;
                return item.index;
            }
        }
        return -1;
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

        this.initList();

        this.core.addBehaviour("straight: T<->B");
        this.core.addBehaviour("arc_left: L<->B");
        this.core.addBehaviour("arc_right: R<->B");

        this.core.addRule("align_left: arc_right is complete and arc_right is most_likely",
                new PMLRuleListener() {
                    @Override
                    public void onRuleSatisfied(String event, int subsequentCalls) {
                        Log.d("PROB LIST SCROLL", "align left");
                        updateAlignment(MyListItem.ALIGN_LEFT);
                    }
                });

        this.core.addRule("align_right: arc_left is complete and arc_left is most_likely",
                new PMLRuleListener() {
                    @Override
                    public void onRuleSatisfied(String event, int subsequentCalls) {
                        Log.d("PROB LIST SCROLL", "align right");
                        updateAlignment(MyListItem.ALIGN_RIGHT);
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

        if(this.openMode == 1)
        new AlertDialog.Builder(this.getContext())
                .setIcon(android.R.drawable.ic_dialog_dialer)
                .setTitle("Calling")
                .setMessage("Calling...")
                .setNegativeButton("Cancel", null)
                .show();
        else if(this.openMode == 2)
            new AlertDialog.Builder(this.getContext())
                    .setIcon(android.R.drawable.ic_dialog_dialer)
                    .setTitle("Send Mail")
                    .setMessage("Open email app?")
                    .setPositiveButton("Ok", null)
                    .setNegativeButton("Cancel", null)
                    .show();
        this.core.undetermine();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.core.drawBody(canvas);
    }


    private void updateAlignment(int alignment) {

        for (MyListItem item : this.items) {
            item.setAlignment(alignment, false);
        }
    }

    private class MyListItem {

        public static final int ALIGN_LEFT = 0;
        public static final int ALIGN_RIGHT = 1;
        public static final int BASE_HEIGHT = 75;
        private final float textW;

        private int index;
        private String text;
        private int alignment;

        private float tx;
        private float x;
        private float y;

        private float imgPersonX;
        private float imgPersontX;

        private float imgPhoneX;
        private float imgPhonetX;

        private float imgMailX;
        private float imgMailtX;

        private boolean pressed;

        private float height = (BASE_HEIGHT * ProbList.this.scale + 0.5f);


        private MyListItem(int index, String text) {
            this.index = index;
            this.text = text;
            this.textW = ProbList.this.textPaint.measureText(this.text);

            this.setAlignment(ALIGN_LEFT, true);
        }

        public void setAlignment(int alignment, boolean instantMove) {

            this.alignment = alignment;

            if (this.alignment == ALIGN_LEFT) {
                this.imgPersontX = 0;
                this.tx = 0 + ProbList.this.imgPerson.getWidth();

                this.imgMailtX = ProbList.this.getWidth() - ProbList.this.imgMail.getWidth()*1.1f;
                this.imgPhonetX = imgMailtX - ProbList.this.imgPhone.getWidth() * 1.25f;

            } else {

                this.tx = ProbList.this.getWidth() - this.textW - ProbList.this.imgPerson.getWidth();
                this.imgPersontX = ProbList.this.getWidth() - ProbList.this.imgPerson.getWidth();

                this.imgPhonetX = ProbList.this.imgPhone.getWidth() * 0.1f;
                this.imgMailtX = imgPhonetX + ProbList.this.imgPhone.getWidth() * 1.25f;
            }

            if (instantMove) {
                this.x = this.tx;
                this.imgPersonX = this.imgPersontX;
                this.imgPhoneX = this.imgPhonetX;
                this.imgMailX = this.imgMailtX;
            }
        }


        public boolean pointInItem(float x, float y) {
            return 0 < x && x < 0 + ProbList.this.getWidth()
                    && this.y < y && y < this.y + (BASE_HEIGHT * ProbList.this.scale + 0.5f);
        }

        public boolean pointInPhone(float x, float y) {
            return imgPhoneX < x && x < imgPhoneX + ProbList.this.imgPhone.getWidth()
                    && this.y < y && y < this.y + (BASE_HEIGHT * ProbList.this.scale + 0.5f);
        }

        public boolean pointInMail(float x, float y) {
            return imgMailX < x && x < imgMailX + ProbList.this.imgMail.getWidth()
                    && this.y < y && y < this.y + (BASE_HEIGHT * ProbList.this.scale + 0.5f);
        }


        public void onPressed() {
            this.pressed = true;
        }

        public void onReleased() {
            this.pressed = false;
        }

        public void setLocationY(float y) {
            this.y = y;
        }


        public boolean doStep() {

            // Move towards target:
            this.x += (this.tx - this.x) * 0.3f;
            this.imgPersonX += (this.imgPersontX - this.imgPersonX) * 0.3f;
            this.imgPhoneX += (this.imgPhonetX - this.imgPhoneX) * 0.3f;
            this.imgMailX += (this.imgMailtX - this.imgMailX) * 0.3f;

            // Snap to target:
            int snapped = 0;
            if (Math.abs(this.tx - this.x) < 1) {
                this.x = this.tx;
                snapped++;
            }
            if (Math.abs(this.imgPersontX - this.imgPersonX) < 1) {
                this.imgPersonX = this.imgPersontX;
                snapped++;
            }
            if (Math.abs(this.imgPhonetX - this.imgPhoneX) < 1) {
                this.imgPhoneX = this.imgPhonetX;
                snapped++;
            }
            if (Math.abs(this.imgMailtX - this.imgMailX) < 1) {
                this.imgMailX = this.imgMailtX;
                snapped++;
            }

            return snapped < 4;
        }

        public void draw(Canvas canvas) {

            canvas.save();
            canvas.translate(0, this.y);


            // Draw background:
            if (this.pressed && ProbList.this.openMode == 0  && ProbList.this.core.getCandidateProb() > 0.9) {
                canvas.drawRect(0, 0,
                        ProbList.this.getWidth(), this.height,
                        ProbList.this.bgPaint);
            }

            // Draw item border:
            canvas.drawLine(0, 0, ProbList.this.getWidth(), 0, ProbList.this.textPaint);


            // Draw icons:
            canvas.drawBitmap(ProbList.this.imgPerson, this.imgPersonX, this.height / 2 - ProbList.this.imgPerson.getHeight() / 2, null);
            canvas.drawBitmap(ProbList.this.imgPhone, this.imgPhoneX, this.height / 2 - ProbList.this.imgPhone.getHeight() / 2, null);
            canvas.drawBitmap(ProbList.this.imgMail, this.imgMailX, this.height / 2 - ProbList.this.imgMail.getHeight() / 2, null);

            // Draw label:
            canvas.drawText(this.text, this.x,
                    (BASE_HEIGHT * ProbList.this.scale + 0.5f) / 2 + ProbList.this.textPaint.getTextSize() * 0.4f,
                    ProbList.this.textPaint);

            canvas.restore();
        }
    }
}
