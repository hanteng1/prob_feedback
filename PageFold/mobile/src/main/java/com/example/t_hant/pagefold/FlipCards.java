package com.example.t_hant.pagefold;

import android.view.MotionEvent;
import android.view.View;

import com.example.t_hant.pagefold.utils.MLog;
import com.example.t_hant.pagefold.utils.TextureUtils;
import com.example.t_hant.pagefold.utils.UI;

import junit.framework.Assert;

import java.nio.channels.FileLock;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by t_hant on 1/21/2018.
 */

public class FlipCards {

    private static final float ACCELERATION = 0.65f;
    private static final float MOVEMENT_RATE = 1.5f;
    private static final int MAX_TIP_ANGLE = 60;
    private static final int MAX_TOUCH_MOVE_ANGLE = 15;
    private static final float MIN_MOVEMENT = 4f;

    private static final int STATE_INIT = 0;
    private static final int STATE_TOUCH = 1;
    private static final int STATE_AUTO_ROTATE = 2;

    //stacked cards
    //private ViewDualCards frontCards;
    //private ViewDualCards backCards;

    //this class manages cards and handle touch events, and place the render command

    //folded cards
    //should be an list storing all cards
    private ArrayList<ViewDualCards> foldingCards;
    private int numCards = 5;

    private float accumulatedAngle = 0f;
    private boolean forward = true;
    private int animatedFrame = 0;
    private int state = STATE_INIT;

    private boolean orientationVertical = true;
    private float lastPosition = -1;

    private FlipViewController controller;

    private volatile boolean visible = false;

    private volatile boolean firstDrawFinished = false;

    private int maxIndex = 0;

    private int lastPageIndex;

    public FlipCards(FlipViewController controller, boolean orientationVertical) {
        this.controller = controller;

        //frontCards = new ViewDualCards(orientationVertical);
        //backCards = new ViewDualCards(orientationVertical);

        foldingCards = new ArrayList<ViewDualCards>();

        for(int itrc = 0; itrc < numCards; itrc++)
        {
            ViewDualCards tempCard = new ViewDualCards(orientationVertical);
            foldingCards.add(tempCard);
        }

        this.orientationVertical = orientationVertical;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isFirstDrawFinished() {
        return firstDrawFinished;
    }

    public void setFirstDrawFinished(boolean firstDrawFinished) {
        this.firstDrawFinished = firstDrawFinished;
    }

    boolean refreshPageView(View view) {
        boolean match = false;
//        if (frontCards.getView() == view) {
//            frontCards.resetWithIndex(frontCards.getIndex());
//            match = true;
//        }
//        if (backCards.getView() == view) {
//            backCards.resetWithIndex(backCards.getIndex());
//            match = true;
//        }

        for(int itrc = 0; itrc < numCards; itrc++)
        {
            if(foldingCards.get(itrc).getView() == view)
            {
                foldingCards.get(itrc).resetWithIndex(foldingCards.get(itrc).getIndex());
                match = true;
            }
        }

        return match;
    }

    boolean refreshPage(int pageIndex) {
        boolean match = false;
//        if (frontCards.getIndex() == pageIndex) {
//            frontCards.resetWithIndex(pageIndex);
//            match = true;
//        }
//        if (backCards.getIndex() == pageIndex) {
//            backCards.resetWithIndex(pageIndex);
//            match = true;
//        }
        for(int itrc = 0; itrc < numCards; itrc++)
        {
            if(foldingCards.get(itrc).getIndex() == pageIndex)
            {
                foldingCards.get(itrc).resetWithIndex(pageIndex);
                match = true;
            }
        }

        return match;
    }

    void refreshAllPages() {
//        frontCards.resetWithIndex(frontCards.getIndex());
//        backCards.resetWithIndex(backCards.getIndex());

        for(int itrc = 0; itrc < numCards; itrc++)
        {
            foldingCards.get(itrc).resetWithIndex(foldingCards.get(itrc).getIndex());
        }
    }

    public void reloadTexture(int frontIndex, View frontView, int backIndex, View backView) {
        synchronized (this) {
//            boolean frontChanged = frontCards.loadView(frontIndex, frontView, controller.getAnimationBitmapFormat());
//            boolean backChanged = backCards.loadView(backIndex, backView, controller.getAnimationBitmapFormat());
//
//            //MLog.d("front card index: " + frontCards.getIndex() + ", back card index: " + backCards.getIndex());
//
//            if (MLog.ENABLE_DEBUG) {
//                MLog.d("reloading texture: %s and %s; old views: %s, %s, front changed %s, back changed %s",
//                                frontView, backView, frontCards.getView(), backCards.getView(), frontChanged,
//                                backChanged);
//            }
//
//            if (MLog.ENABLE_DEBUG) {
//                MLog.d("reloadTexture: activeIndex %d, front %d, back %d, angle %.1f",
//                        getPageIndexFromAngle(accumulatedAngle), frontIndex, backIndex, accumulatedAngle);
//            }

        }
    }

    public void reloadTexture(ArrayList<View> views)
    {
        synchronized (this) {
            for(int itrc = 0; itrc < foldingCards.size(); itrc++)
            {
                boolean changed = foldingCards.get(itrc).loadView(itrc, views.get(itrc), controller.getAnimationBitmapFormat());
            }
        }
    }


    synchronized void resetSelection(int selection, int maxIndex) {
        UI.assertInMainThread();

        //stop flip animation when selection is manually changed

        this.maxIndex = maxIndex;
        setVisible(false);
        setState(STATE_INIT);
        accumulatedAngle = selection * 180;
        //frontCards.resetWithIndex(selection);
       // backCards.resetWithIndex(selection + 1 < maxIndex ? selection + 1 : -1);

        for(int itrc = 0; itrc < foldingCards.size(); itrc++)
        {
            foldingCards.get(itrc).resetWithIndex(itrc);
        }

        controller.postHideFlipAnimation();
    }

    public synchronized void draw(FlipRenderer renderer, GL10 gl) {
        //frontCards.buildTexture(renderer, gl);
        //backCards.buildTexture(renderer, gl);

//        if (!TextureUtils.isValidTexture(frontCards.getTexture()) &&
//                !TextureUtils.isValidTexture(backCards.getTexture())) {
//            return;
//        }

        for(int itrc = 0; itrc < foldingCards.size(); itrc++)
        {
            foldingCards.get(itrc).buildTexture(renderer, gl);

            if(!TextureUtils.isValidTexture(foldingCards.get(itrc).getTexture()))
            {
                return;
            }
        }

        if (!visible) {
            return;
        }

        switch (state) {
            case STATE_INIT:
            case STATE_TOUCH:
                break;
            case STATE_AUTO_ROTATE:
                {
//                    animatedFrame++;
//                    float delta = (forward ? ACCELERATION : -ACCELERATION) * animatedFrame % 180;
//
//                    float oldAngle = accumulatedAngle;
//
//                    accumulatedAngle += delta;
//
//                    //MLog.d("accumulatedAngle: " + Float.toString(accumulatedAngle));
//
//                    if (oldAngle < 0) { //bouncing back after flip backward and over the first page
//                        Assert.assertTrue(forward);
//                        if (accumulatedAngle >= 0) {
//                            accumulatedAngle = 0;
//                            setState(STATE_INIT);
//                        }
//                    } else {
//                        if (frontCards.getIndex() == maxIndex - 1 && oldAngle > frontCards.getIndex()
//                                * 180) { //bouncing back after flip forward and over the last page
//                            Assert.assertTrue(!forward);
//                            if (accumulatedAngle <= frontCards.getIndex() * 180) {
//                                setState(STATE_INIT);
//                                accumulatedAngle = frontCards.getIndex() * 180;
//                            }
//                        } else {
//                            if (forward) {
//                                Assert.assertTrue(
//                                        "index of backCards should not be -1 when automatically flipping forward",
//                                        backCards.getIndex() != -1);
//                                if (accumulatedAngle >= backCards.getIndex() * 180) { //moved to the next page
//                                    accumulatedAngle = backCards.getIndex() * 180;
//                                    setState(STATE_INIT);
//                                    controller.postFlippedToView(backCards.getIndex());
//
//                                    swapCards();
//                                    backCards.resetWithIndex(frontCards.getIndex() + 1);
//                                }
//                            } else { //backward
//                                if (accumulatedAngle <= frontCards.getIndex() * 180) { //firstCards restored
//                                    accumulatedAngle = frontCards.getIndex() * 180;
//                                    setState(STATE_INIT);
//                                }
//                            }
//                        }
//                    } //ends of `if (oldAngle < 0) {} else {}`
//
//                    if (state == STATE_INIT) {
//                        controller.postHideFlipAnimation();
//                    } else {
//                        controller.getSurfaceView().requestRender();
//                    }
            }
            break;
            default:
                MLog.e("Invalid state: " + state);
                break;
        } //end of switch

        //only when touch event happends, or when animation happens

        float angle = getDisplayAngle();
        //MLog.d("angle: " + Float.toString(angle));

        if (angle < 0) {
            //for now.. don't consider this situation

//            frontCards.getTopCard().setAxis(Card.AXIS_BOTTOM);
//            frontCards.getTopCard().setAngle(-angle);
//            frontCards.getTopCard().draw(gl);
//
//            frontCards.getBottomCard().setAngle(0);
//            frontCards.getBottomCard().draw(gl);

            //no need to draw backCards here
        }
        else {
//
//            if (angle < 90) { //render front view over back view
//                frontCards.getTopCard().setAngle(0);
//                frontCards.getTopCard().draw(gl);
//
//                backCards.getBottomCard().setAngle(0);
//                backCards.getBottomCard().draw(gl);
//
//                frontCards.getBottomCard().setAxis(Card.AXIS_TOP);
//                frontCards.getBottomCard().setAngle(angle);
//                frontCards.getBottomCard().draw(gl);
//            } else { //render back view first
//                frontCards.getTopCard().setAngle(0);
//                frontCards.getTopCard().draw(gl);
//
//                backCards.getTopCard().setAxis(Card.AXIS_BOTTOM);
//                backCards.getTopCard().setAngle(180 - angle);
//                backCards.getTopCard().draw(gl);
//
//                backCards.getBottomCard().setAngle(0);
//                backCards.getBottomCard().draw(gl);
//            }
//

//            //for quick demo
//            //frontCards.getTopCard().setAxis(Card.AXIS_BOTTOM);
//            frontCards.getTopCard().setAngle(angle);
//            frontCards.getTopCard().draw(gl);
//
//            //frontCards.getBottomCard().setAxis(Card.AXIS_TOP);
//            frontCards.getBottomCard().setAngle(angle);
//            frontCards.getBottomCard().draw(gl);
//
//            //backCards.getTopCard().setAxis(Card.AXIS_BOTTOM);
//            backCards.getTopCard().setAngle(angle);
//            backCards.getTopCard().draw(gl);
//
//            //backCards.getBottomCard().setAxis(Card.AXIS_TOP);
//            backCards.getBottomCard().setAngle(angle);
//            backCards.getBottomCard().draw(gl);


            for(int itrc = 0; itrc < foldingCards.size(); itrc++)
            {
                foldingCards.get(itrc).getTopCard().setAngle(angle);
                foldingCards.get(itrc).getTopCard().draw(gl);
                foldingCards.get(itrc).getBottomCard().setAngle(angle);
                foldingCards.get(itrc).getBottomCard().draw(gl);
            }

        }

//        if ((frontCards.getView() == null || TextureUtils.isValidTexture(frontCards.getTexture())) &&
//                (backCards.getView() == null || TextureUtils.isValidTexture(backCards.getTexture())))
//        {
//            firstDrawFinished = true;
//        }

        int firstdrawjustifycount = 0;
        for(int itrc = 0; itrc < foldingCards.size(); itrc++)
        {
            if(foldingCards.get(itrc).getView() == null || TextureUtils.isValidTexture(foldingCards.get(itrc).getTexture()))
            {
                firstdrawjustifycount++;
            }
        }
        if(firstdrawjustifycount == foldingCards.size())
        {
            firstDrawFinished = true;
        }


    }

    public void invalidateTexture() {
        //frontCards.abandonTexture();
        //backCards.abandonTexture();
        for(int itrc = 0; itrc < foldingCards.size(); itrc++)
        {
            foldingCards.get(itrc).abandonTexture();
        }
    }


    //original handle touch events
    /////////////////////////////////////////////////////////////////////////////////////////////////////
//    public synchronized boolean handleTouchEvent(MotionEvent event, boolean isOnTouchEvent) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                // remember page we started on...
//                lastPageIndex = getPageIndexFromAngle(accumulatedAngle);
//                lastPosition = orientationVertical ? event.getY() : event.getX();
//                return isOnTouchEvent;
//            case MotionEvent.ACTION_MOVE:
//                float delta = orientationVertical ? (lastPosition - event.getY()) : (lastPosition - event.getX());
//
//                if (Math.abs(delta) > controller.getTouchSlop()) {
//                    setState(STATE_TOUCH);  //take control whenever a touch move happens
//                    forward = delta > 0;  //swipe upwards
//                }
//
//                if (state == STATE_TOUCH) {
//                    if (Math.abs(delta) > MIN_MOVEMENT) //ignore small movements
//                    {
//                        forward = delta > 0;  //true or false
//                    }
//
//                    controller.showFlipAnimation(); //request render, only for switching inFlipAnimation to true
//                    //after this,, visible = true
//
//                    float angleDelta;
//                    if (orientationVertical) {
//                        angleDelta = 180 * delta / controller.getContentHeight() * MOVEMENT_RATE;
//                    } else {
//                        angleDelta = 180 * delta / controller.getContentWidth() * MOVEMENT_RATE;
//                    }
//
//                    //convert from touch position delta to angle delta and use it to update accumulatedAngle
//                    //MLog.d("angledelta: " +  Float.toString(angleDelta) + ",  contentheight: " + Float.toString(controller.getContentHeight()));
//
//                    if (Math.abs(angleDelta) > MAX_TOUCH_MOVE_ANGLE) //prevent large delta when moving too fast
//                    {
//                        angleDelta = Math.signum(angleDelta) * MAX_TOUCH_MOVE_ANGLE;
//                    }
//
//                    // do not flip more than one page with one touch...
//                    //accumulatedAngle at most add 180 per touch event
//                    if (Math.abs(getPageIndexFromAngle(accumulatedAngle + angleDelta) - lastPageIndex) <= 1) {
//                        accumulatedAngle += angleDelta;
//                    }
//
//                    //Bounce the page for the first and the last page
//                    if (frontCards.getIndex() == maxIndex - 1) { //the last page
//                        if (accumulatedAngle > frontCards.getIndex() * 180) {
//                            accumulatedAngle = Math.min(accumulatedAngle, controller.isOverFlipEnabled() ? (frontCards.getIndex() * 180 + MAX_TIP_ANGLE) : (frontCards.getIndex() * 180));
//                        }
//                    }
//
//                    //accumulatedAngle at most reduced to a certian degree
//                    if (accumulatedAngle < 0) {
//                        accumulatedAngle = Math.max(accumulatedAngle, controller.isOverFlipEnabled() ? -MAX_TIP_ANGLE : 0);
//                    }
//
//                    //anglepageindex points to the page to show up
//                    //swipe up -> a front page
//                    //swipe down -> a back page
//                    int anglePageIndex = getPageIndexFromAngle(accumulatedAngle);
//
//                    //MLog.d("accumulatedAngle: " + Float.toString(accumulatedAngle));
//
//                    if (accumulatedAngle >= 0) {
//                        if (anglePageIndex != frontCards.getIndex()) {
//                            if (anglePageIndex == frontCards.getIndex() - 1) { //moved to previous page
//                                swapCards(); //frontCards becomes the backCards
//                                frontCards.resetWithIndex(backCards.getIndex() - 1);  //reset is clean up, but just associated with a id
//                                controller.flippedToView(anglePageIndex, false);  //feels like this is an important step
//                            } else if (anglePageIndex == frontCards.getIndex() + 1) { //moved to next page
//                                //seems like this is barely called
//                                swapCards();
//                                backCards.resetWithIndex(frontCards.getIndex() + 1);
//                                controller.flippedToView(anglePageIndex, false);
//                            } else {
//                                throw new RuntimeException(MLog.format(
//                                        "Inconsistent states: anglePageIndex: %d, accumulatedAngle %.1f, frontCards %d, backCards %d",
//                                        anglePageIndex, accumulatedAngle, frontCards.getIndex(), backCards.getIndex()));
//                            }
//                        }
//                    }
//
//                    lastPosition = orientationVertical ? event.getY() : event.getX();
//
//                    controller.getSurfaceView().requestRender();  //request render
//                    return true;
//                }
//
//                return isOnTouchEvent;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                if (state == STATE_TOUCH) {
//                    if (accumulatedAngle < 0) {
//                        forward = true;
//                    } else if (accumulatedAngle >= frontCards.getIndex() * 180
//                            && frontCards.getIndex() == maxIndex - 1) {
//                        forward = false;
//                    }
//
//                    setState(STATE_AUTO_ROTATE);
//                    controller.getSurfaceView().requestRender();
//                }
//                return isOnTouchEvent;
//        }
//
//        return false;
//    }


    //try new handle touch events
    public synchronized boolean handleTouchEvent(MotionEvent event, boolean isOnTouchEvent) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // remember page we started on...
                lastPageIndex = getPageIndexFromAngle(accumulatedAngle);
                lastPosition = orientationVertical ? event.getY() : event.getX();
                return isOnTouchEvent;
            case MotionEvent.ACTION_MOVE:
                float delta = orientationVertical ? (lastPosition - event.getY()) : (lastPosition - event.getX());

                if (Math.abs(delta) > controller.getTouchSlop()) {
                    setState(STATE_TOUCH);  //take control whenever a touch move happens
                    forward = delta > 0;  //swipe upwards
                }

                if (state == STATE_TOUCH) {
                    if (Math.abs(delta) > MIN_MOVEMENT) //ignore small movements
                    {
                        forward = delta > 0;  //true or false
                    }

                    controller.showFlipAnimation(); //request render, only for switching inFlipAnimation to true
                    //after this,, visible = true

                    float angleDelta;
                    if (orientationVertical) {
                        angleDelta = 180 * delta / controller.getContentHeight() * MOVEMENT_RATE;
                    } else {
                        angleDelta = 180 * delta / controller.getContentWidth() * MOVEMENT_RATE;
                    }

                    //convert from touch position delta to angle delta and use it to update accumulatedAngle
                    //MLog.d("angledelta: " +  Float.toString(angleDelta) + ",  contentheight: " + Float.toString(controller.getContentHeight()));

                    if (Math.abs(angleDelta) > MAX_TOUCH_MOVE_ANGLE) //prevent large delta when moving too fast
                    {
                        angleDelta = Math.signum(angleDelta) * MAX_TOUCH_MOVE_ANGLE;
                    }

                    // do not flip more than one page with one touch...
                    //accumulatedAngle at most add 180 per touch event
                    if (Math.abs(getPageIndexFromAngle(accumulatedAngle + angleDelta) - lastPageIndex) <= 1) {
                        accumulatedAngle += angleDelta;
                    }

                    //Bounce the page for the first and the last page
//                    if (frontCards.getIndex() == maxIndex - 1) { //the last page
//                        if (accumulatedAngle > frontCards.getIndex() * 180) {
//                            accumulatedAngle = Math.min(accumulatedAngle, controller.isOverFlipEnabled() ? (frontCards.getIndex() * 180 + MAX_TIP_ANGLE) : (frontCards.getIndex() * 180));
//                        }
//                    }

                    //accumulatedAngle at most reduced to a certain degree
                    if (accumulatedAngle < 0) {
                        //accumulatedAngle = Math.max(accumulatedAngle, controller.isOverFlipEnabled() ? -MAX_TIP_ANGLE : 0);
                        accumulatedAngle = 0;
                    }

                    //anglepageindex points to the page to show up
                    //swipe up -> a front page
                    //swipe down -> a back page
                    int anglePageIndex = getPageIndexFromAngle(accumulatedAngle);

                    //MLog.d("accumulatedAngle: " + Float.toString(accumulatedAngle));

                    //dont swap anything for now
//                    if (accumulatedAngle >= 0) {
//                        if (anglePageIndex != frontCards.getIndex()) {
//                            if (anglePageIndex == frontCards.getIndex() - 1) { //moved to previous page
//                                swapCards(); //frontCards becomes the backCards
//                                frontCards.resetWithIndex(backCards.getIndex() - 1);  //reset is clean up, but just associated with a id
//                                controller.flippedToView(anglePageIndex, false);  //feels like this is an important step
//                            } else if (anglePageIndex == frontCards.getIndex() + 1) { //moved to next page
//                                //seems like this is barely called
//                                swapCards();
//                                backCards.resetWithIndex(frontCards.getIndex() + 1);
//                                controller.flippedToView(anglePageIndex, false);
//                            } else {
//                                throw new RuntimeException(MLog.format(
//                                        "Inconsistent states: anglePageIndex: %d, accumulatedAngle %.1f, frontCards %d, backCards %d",
//                                        anglePageIndex, accumulatedAngle, frontCards.getIndex(), backCards.getIndex()));
//                            }
//                        }
//                    }

                    lastPosition = orientationVertical ? event.getY() : event.getX();


                    //re-calculate the vertices

//                    frontCards.setTranslateY(419);
//                    frontCards.calculateVertices(getDisplayAngle());
//                    backCards.calculateVertices(getDisplayAngle());

                    float accumulatedTranslateY = 0;
                    for(int itrc = 0; itrc < foldingCards.size(); itrc++)
                    {
                        float translateY = 0;
                        ViewDualCards cards = foldingCards.get(itrc);
                        if(itrc == 0)
                        {
                            translateY = controller.getContentHeight() - cards.getView().getHeight();
                            accumulatedTranslateY += translateY;
                        }else
                        {
                            accumulatedTranslateY += (-cards.getView().getHeight());
                            accumulatedTranslateY += foldingCards.get(itrc - 1).getShrinkedLength();
                            translateY = accumulatedTranslateY;
                        }

                        cards.setTranslateY(translateY);
                        cards.calculateVertices(getDisplayAngle());
                    }

                    controller.getSurfaceView().requestRender();  //request render
                    return true;
                }

                return isOnTouchEvent;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (state == STATE_TOUCH) {
//                    if (accumulatedAngle < 0) {
//                        forward = true;
//                    } else if (accumulatedAngle >= frontCards.getIndex() * 180
//                            && frontCards.getIndex() == maxIndex - 1) {
//                        forward = false;
//                    }

                    setState(STATE_AUTO_ROTATE);
                    controller.getSurfaceView().requestRender();
                }
                return isOnTouchEvent;
        }

        return false;
    }




    private void swapCards() {
//        ViewDualCards tmp = frontCards;
//        frontCards = backCards;
//        backCards = tmp;
    }

    private void setState(int state) {
        if (this.state != state) {                        /*
			if (AphidLog.ENABLE_DEBUG)
				AphidLog.i("setState: from %d, to %d; angle %.1f", this.state, state, angle);
			*/
            this.state = state;
            animatedFrame = 0;
        }
    }

    private int getPageIndexFromAngle(float angle) {
        return ((int) angle) / 180;
    }

    private float getDisplayAngle() {
        return accumulatedAngle % 180;
    }

}
