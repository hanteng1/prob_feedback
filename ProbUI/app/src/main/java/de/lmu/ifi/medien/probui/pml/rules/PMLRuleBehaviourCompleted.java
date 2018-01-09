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

import de.lmu.ifi.medien.probui.behaviours.ProbBehaviour;


public class PMLRuleBehaviourCompleted extends PMLRuleBehaviour {

    public PMLRuleBehaviourCompleted(ProbBehaviour behaviour, String label) {
        super(behaviour, label);
    }

    @Override
    public boolean checkBehaviourRuleSpecific() {

        Log.d("PML RULE", "PMLRuleBehaviourCompleted -> checkBehaviourRuleSpecific:" + super.lastResult.hasBeenCompleted());

        return super.lastResult.hasBeenCompleted();
    }
}