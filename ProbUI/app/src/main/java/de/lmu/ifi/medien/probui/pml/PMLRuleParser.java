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

package de.lmu.ifi.medien.probui.pml;

import de.lmu.ifi.medien.probui.pml.rules.PMLRule;
import de.lmu.ifi.medien.probui.pml.rules.PMLRuleSet;

public interface PMLRuleParser {


    /**
     * Creates a rule from the given PML stattement.
     *
     * @param pmlStatement The PML statement that defines the rule.
     * @return The rule object.
     */
    public PMLRule parse(String pmlStatement);


    /**
     * Returns the parsed label for the last parsed rule.
     * @return
     */
    public String getRuleLabel();
}
