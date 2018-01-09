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

package de.lmu.ifi.medien.probuidemos.demos;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import de.lmu.ifi.medien.probui.system.ProbUIManager;
import de.lmu.ifi.medien.probuidemos.R;
import de.lmu.ifi.medien.probuidemos.gui.elements.BezelledEditText;
import de.lmu.ifi.medien.probuidemos.gui.elements.BezelledScrollView;
import de.lmu.ifi.medien.probuidemos.gui.elements.ProbBezelButton;

public class BezelDemo extends AppCompatActivity {

    private BezelledEditText editText;
    private BezelledScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bezel_demo);

        editText = (BezelledEditText) findViewById(R.id.editText);
        editText.setBezelMode(BezelledEditText.BEZEL_MODE_NONE);

        scrollView = (BezelledScrollView) findViewById(R.id.scrollView);
        scrollView.setBezelled(false);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                editText.setScrollViewScroll(scrollView.getScrollY());
            }
        });

        ProbUIManager manager = new ProbUIManager(
                findViewById(android.R.id.content).getRootView(), R.id.probUIContainer);
        manager.autoAssignInteractors();
    }

    public void onClickBezel(View view) {
        Log.d("BEZEL", "clicked a bezel!");
        if (view == findViewById(R.id.bezel)) {
            editText.setBezelMode(BezelledEditText.BEZEL_MODE_START);
            editText.setHighlightColor(ProbBezelButton.COLOUR_BEZEL_MODE_START);
            ((ProbBezelButton) view).setHighlightColor(ProbBezelButton.COLOUR_BEZEL_MODE_START);
        } else if (view == findViewById(R.id.bezel2)) {
            editText.setBezelMode(BezelledEditText.BEZEL_MODE_END_CUT);
            editText.setHighlightColor(ProbBezelButton.COLOUR_BEZEL_MODE_END_CUT);
            ((ProbBezelButton) view).setHighlightColor(ProbBezelButton.COLOUR_BEZEL_MODE_END_CUT);
        } else if (view == findViewById(R.id.bezel3)) {
            editText.setBezelMode(BezelledEditText.BEZEL_MODE_END_PASTE);
            editText.setHighlightColor(ProbBezelButton.COLOUR_BEZEL_MODE_END_PASTE);
            ((ProbBezelButton) view).setHighlightColor(ProbBezelButton.COLOUR_BEZEL_MODE_END_PASTE);
        }
        scrollView.setBezelled(true);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}
