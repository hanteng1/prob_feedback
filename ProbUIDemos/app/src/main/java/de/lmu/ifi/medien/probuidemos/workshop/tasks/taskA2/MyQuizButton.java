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

package de.lmu.ifi.medien.probuidemos.workshop.tasks.taskA2;

import android.content.Context;
import android.util.AttributeSet;


import de.lmu.ifi.medien.probui.observations.ProbObservationTouch;
import de.lmu.ifi.medien.probuidemos.workshop.tasks.taskA.MyProbUIButton;


public class MyQuizButton extends MyProbUIButton {

    public MyQuizButton(Context context) {
        super(context);
    }

    public MyQuizButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyQuizButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /* =============================================================================================
    Task A2 - Please insert your code below:
    ============================================================================================= */
    // Step 1: Complete the method to set the opacity according to the button's probability
    public void updateTransparency() {
        this.setAlpha(0.25f + (float) this.core.getCandidateProb() * 0.75f); // 0.25 minimum opacity
    }

    /* Step 2: Extend the two methods below so that the transparency is updated at each touch down
    and touch move event */
    @Override
    public void onTouchDownPost(ProbObservationTouch obs) {
        super.onTouchDownPost(obs);
        updateTransparency();
    }

    @Override
    public void onTouchMovePost(ProbObservationTouch obs) {
        super.onTouchMovePost(obs);
        updateTransparency();
    }

    /* Step 3: Extend the method below so that the transparency is reset at a touch up event */
    @Override
    public void onTouchUpPost(ProbObservationTouch obs, int numRemainingPointers) {
        super.onTouchUpPost(obs, numRemainingPointers);
        this.setAlpha(1);
    }
    /* =============================================================================================
    End of Task A2
    ============================================================================================= */
}
