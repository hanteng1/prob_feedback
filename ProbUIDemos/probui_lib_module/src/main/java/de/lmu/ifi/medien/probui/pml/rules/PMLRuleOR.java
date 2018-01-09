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

import java.util.ArrayList;
import java.util.List;


public class PMLRuleOR extends PMLRuleBinary {

    public PMLRuleOR(String label) {
        super(label);
    }

    @Override
    public boolean checkSpecific() {
        boolean checked = false;
        for (PMLRule rule : this.subrules) {
            checked = checked || rule.check();
        }
        Log.d("PML RULE", "PMLRuleOR -> checkSpecific: " + this.label + ": " + checked + " | " + this.subrules.get(0));

        return checked;
    }


}