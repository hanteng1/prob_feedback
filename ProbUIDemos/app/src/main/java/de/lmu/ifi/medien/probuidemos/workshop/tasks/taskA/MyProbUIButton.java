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

package de.lmu.ifi.medien.probuidemos.workshop.tasks.taskA;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Toast;

import de.lmu.ifi.medien.probui.gui.base.ProbUIButton;
import de.lmu.ifi.medien.probui.pml.PMLRuleListener;


public class MyProbUIButton extends ProbUIButton {


    public MyProbUIButton(Context context) {
        super(context);
    }

    public MyProbUIButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyProbUIButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /* =============================================================================================
    Task A - Please insert your code below:
    ============================================================================================= */

    // Step 1: Override the onProbSetup() method
    public void onProbSetup() {

        // Step 2: Add a behaviour for a simple tap on the button
        this.core.addBehaviour("tap: Cd*u");

        // Step 3: Add a rule that triggers the button on completing the tap
        this.core.addRule("activate: tap on complete and tap is most_likely", new PMLRuleListener() {
            @Override
            public void onRuleSatisfied(String event, int subsequentCalls) {
                Toast.makeText(getContext(), "Hello World!", Toast.LENGTH_SHORT).show();
            }
        });
    }


/* =============================================================================================
    End of Task A
    ============================================================================================= */


}
