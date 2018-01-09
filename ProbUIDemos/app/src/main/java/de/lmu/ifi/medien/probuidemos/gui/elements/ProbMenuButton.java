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
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.lmu.ifi.medien.probuidemos.R;
import de.lmu.ifi.medien.probui.gui.ProbInteractor;
import de.lmu.ifi.medien.probui.gui.ProbInteractorCore;
import de.lmu.ifi.medien.probui.observations.ProbObservation;
import de.lmu.ifi.medien.probui.observations.ProbObservationTouch;
import de.lmu.ifi.medien.probui.pml.PMLRuleListener;

public class ProbMenuButton extends Button implements ProbInteractor {

    private ProbInteractorCore core;

    private List<MyMenuItem> items;
    private RelativeLayout itemContainer;
    private boolean menuOpen;
    private int openStyle;

    private boolean unlayouted = true;
    private boolean allReachedTargets;

    public ProbMenuButton(Context context) {
        super(context);
        sharedConstructor();
    }

    public ProbMenuButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructor();
    }

    public ProbMenuButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        sharedConstructor();
    }

    private void sharedConstructor() {
        this.core = new ProbInteractorCore(this);
        this.core.init();
        this.core.debugDrawOutline = false;
        this.core.debugDraw = false;
    }

    public void initMenu() {
        this.itemContainer = (RelativeLayout) this.getRootView().findViewById(R.id.menu_item_container);
        this.items = new ArrayList<MyMenuItem>();
        this.items.add(new MyMenuItem(0, this.itemContainer, "Item 1"));
        this.items.add(new MyMenuItem(1, this.itemContainer, "Item 2"));
        this.items.add(new MyMenuItem(2, this.itemContainer, "Item 3"));
        this.items.add(new MyMenuItem(3, this.itemContainer, "Item 4"));
    }


    private void layoutItemsRegular() {
        float scale = getContext().getResources().getDisplayMetrics().density;
        float scaledItemSize = (MyMenuItem.SIZE * scale + 0.5f);
        int index = 0;
        for (MyMenuItem item : this.items) {
            float x = this.itemContainer.getWidth() - scaledItemSize;
            float y = this.itemContainer.getHeight() - (index + 1.8f) * scaledItemSize * 1.25f;
            item.moveTo(x, y);
            index++;
        }
        this.unlayouted = false;
    }


    private void layoutItemsArcLeft() {
        float scale = getContext().getResources().getDisplayMetrics().density;
        float scaledItemSize = (MyMenuItem.SIZE * scale + 0.5f);
        int index = 0;
        float rx = this.itemContainer.getWidth() - scaledItemSize;
        float ry = rx;//this.itemContainer.getHeight() - scaledItemSize;
        float baseX = rx;
        float baseY = this.itemContainer.getHeight() - scaledItemSize;
        float a = 0;
        float x, y;
        for (MyMenuItem item : this.items) {
            a += Math.toRadians(20);
            x = (float) Math.cos(a) * rx;
            y = baseY - (float) Math.sin(a) * ry;
            Log.d("PROBMENU", index + ": " + x + ", " + y);
            item.moveTo(x, y);
            index++;
        }
        this.unlayouted = false;
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

        if (!this.allReachedTargets) {
            this.invalidate();
        }

        this.allReachedTargets = true;
        for (MyMenuItem item : this.items) {
            this.allReachedTargets = item.move() && this.allReachedTargets;
        }

        this.updateButtonVisuals();
    }

    @Override
    public void onTouchDown(ProbObservationTouch obs) {

    }

    @Override
    public void onTouchMove(ProbObservationTouch obs) {

    }

    @Override
    public void onTouchUp(ProbObservationTouch obs, int numRemainingPointers) {
        if (this.core.isCandidate() && numRemainingPointers == 0)
            core.selfExclude();
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

        this.core.addBehaviour("tap: Cd*u");
        this.core.addBehaviour("slide_left: L->NWu");
        this.core.addRule("regular: tap on complete", new PMLRuleListener() {
            @Override
            public void onRuleSatisfied(String event, int subsequentCalls) {
                if(openStyle != 1) {
                    openStyle = 0;
                    Log.d("PROBMENU", "regular -> " + openStyle);
                    if(core.getCandidateProb() > 0.9)
                        core.claimDetermination();
                }
            }
        });
        this.core.addRule("arc_left: slide_left on complete", new PMLRuleListener() {
            @Override
            public void onRuleSatisfied(String event, int subsequentCalls) {
                openStyle = 1;
                Log.d("PROBMENU", "arc_left -> " + openStyle);
                if(core.getCandidateProb() > 0.9)
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

        if (this.unlayouted) {
            for (MyMenuItem item : this.items) {
                item.jumpTo(this.getX(), this.getY());
            }
        }
        this.invalidate();

        if (openStyle == 0) {
            layoutItemsRegular();
        } else if (openStyle == 1) {
            layoutItemsArcLeft();
        }
        if (openStyle == 0 || !this.menuOpen)
            this.switchOpen();
        this.getView().performClick();
        this.core.undetermine();
    }

    private void switchOpen() {
        //this.layoutItemsRegular();
        //this.layoutItemsArcLeft();
        this.menuOpen = !this.menuOpen;
        Log.d("PROBMENU", "switchOpen() --> menuOpen is now: " + this.menuOpen);
        for (MyMenuItem item : this.items) {
            item.setVisibility(this.menuOpen);
            if (!this.menuOpen) {
                item.moveTo(this.getX(), this.getY());
            }
        }
        if (!this.menuOpen) {
            this.openStyle = 0;
        }

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.core.drawBody(canvas);
    }


    private void updateButtonVisuals() {
        if (this.core.getCandidateProb() > 0.5) {
            this.setBackgroundResource(R.drawable.ts_round_button_style_pressed);
        } else {
            this.setBackgroundResource(R.drawable.ts_round_button_style_default);
        }
    }


    private void onClickMenuItem(int index) {
        Log.d("PROBMENU", "Clicked on menu item with index: " + index);
        if (this.menuOpen)
            this.switchOpen();
        Toast.makeText(getContext().getApplicationContext(), "Item " + (index + 1) + " clicked!",
                Toast.LENGTH_SHORT).show();
    }

    private class MyMenuItem {

        final static int SIZE = 75;

        final int index;
        String label;
        ProbMenuItem button;
        float x;
        float y;
        float tx;
        float ty;
        RelativeLayout.LayoutParams lp;

        public MyMenuItem(final int index, RelativeLayout container, String label) {
            this.index = index;
            this.label = label;
            this.button = new ProbMenuItem(container.getContext().getApplicationContext());
            this.button.setTextColor(Color.BLACK);
            this.button.setText(this.label);
            this.button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProbMenuButton.this.onClickMenuItem(index);
                }
            });
            this.setVisibility(false);
            container.addView(this.button);

            float scale = getContext().getResources().getDisplayMetrics().density;
            this.lp = new RelativeLayout.LayoutParams((int) (SIZE * scale + 0.5), (int) (SIZE * scale + 0.5));
            this.lp.width = (int) (SIZE * scale + 0.5);
            this.lp.height = (int) (SIZE * scale + 0.5);
            this.button.setLayoutParams(lp);
        }

        public void moveTo(float tx, float ty) {
            this.tx = tx;
            this.ty = ty;
        }

        public void jumpTo(float tx, float ty) {

            float oldX = this.x;
            float oldY = this.y;

            this.tx = tx;
            this.ty = ty;
            this.x = tx;
            this.y = ty;

            // Update interactor location:
            this.lp.setMargins((int) x, (int) y, 0, 0);
            this.button.setLayoutParams(lp);

            // Update location of the interactor's behaviour(s):
            this.button.getCore().move((this.x - oldX) / this.button.getCore().getSurfaceWidth(),
                    (this.y - oldY) / this.button.getCore().getSurfaceHeight());
        }


        public boolean move() {


            boolean reachedTarget = false;

            float oldX = this.x;
            float oldY = this.y;

            // Approach target:
            this.x += (this.tx - this.x) * 0.1f;
            this.y += (this.ty - this.y) * 0.1f;

            // Snap to target:
            if (Math.abs(tx - this.x) < 5 && Math.abs(ty - this.y) < 5) {
                this.x = tx;
                this.y = ty;
                reachedTarget = true;
            }

            // Update interactor location:
            this.lp.setMargins((int) x, (int) y, 0, 0);
            this.button.setLayoutParams(lp);

            // Update location of the interactor's behaviour(s):
            this.button.getCore().move((this.x - oldX) / this.button.getCore().getSurfaceWidth(),
                    (this.y - oldY) / this.button.getCore().getSurfaceHeight());

            return reachedTarget;
        }

        public void setVisibility(boolean visible) {
            this.button.setVisibility(visible ? Button.VISIBLE : Button.INVISIBLE);
        }
    }


}
