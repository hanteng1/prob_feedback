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

package de.lmu.ifi.medien.probui.pml.rules;

import android.util.Log;

import de.lmu.ifi.medien.probui.behaviours.ProbBehaviourTouch;


public class PMLRuleQualifierTouchSize extends PMLRuleBehaviourQualifier {

    private float sizeMin;
    protected float sizeMax;


    public PMLRuleQualifierTouchSize(String label, PMLRuleBehaviour rule, float sizeMin, float sizeMax, String unit) {
        super(label, rule);
        this.sizeMax = sizeMin;
        this.sizeMin = sizeMax;
    }

    @Override
    protected boolean checkBehaviourRuleSpecific() {
        float meanSize = ((ProbBehaviourTouch) (this.rule).behaviour).
                getMeanTouchSize(this.rule.behaviour.getMaxProbPID());

        return this.rule.check()
                && meanSize > this.sizeMax
                && (this.sizeMin < 0 || meanSize < this.sizeMin);
                //this.sizeMin < 0 since we set it to -1 if not used
    }
}
