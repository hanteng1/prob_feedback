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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;


public class BezelledScrollView extends ScrollView {
    private boolean bezelled;

    public BezelledScrollView(Context context) {
        super(context);
    }

    public BezelledScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BezelledScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public boolean onTouchEvent(MotionEvent ev){
        if(!this.bezelled || ev.getAction() == MotionEvent.ACTION_UP) {
            this.setBezelled(false);
            return super.onTouchEvent(ev);
        }
        return false;
    }

    public void setBezelled(boolean bezelled) {
        this.bezelled = bezelled;
    }
}
