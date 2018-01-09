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

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;


public class BezelledEditText extends EditText {


    public static final int BEZEL_MODE_NONE = -1;
    public static final int BEZEL_MODE_START = 0;
    public static final int BEZEL_MODE_END_CUT = 1;
    public static final int BEZEL_MODE_END_PASTE = 2;

    public static final int BEZEL_SELECTION_OFFSET_Y = 50;


    private float scale = getContext().getResources().getDisplayMetrics().density;

    private int bezelMode;
    private int selStart;
    private int selEnd;
    private int scrollViewScroll;

    public BezelledEditText(Context context) {
        super(context);
    }

    public BezelledEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BezelledEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public boolean onTouchEvent(MotionEvent ev) {

        if (this.bezelMode == BEZEL_MODE_NONE || ev.getAction() == MotionEvent.ACTION_DOWN)
            return super.onTouchEvent(ev);

        if (ev.getAction() == MotionEvent.ACTION_UP) {
            this.performTextAction();
            //this.setCursorVisible(false);
            this.bezelMode = BEZEL_MODE_NONE;
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            this.updateSelection(ev);
            //this.setCursorVisible(true);
        }

        return false;
    }

    private void updateSelection(MotionEvent ev) {
        if (this.bezelMode == BEZEL_MODE_NONE)
            return;

        // from: http://stackoverflow.com/questions/10263384/android-how-to-get-text-position-from-touch-event
        Layout layout = this.getLayout();
        float x = ev.getX() + this.getScrollX();
        float y = ev.getY() + this.getScrollY() + this.scrollViewScroll - this.scale * BEZEL_SELECTION_OFFSET_Y;
        int line = layout.getLineForVertical((int) y);

        int offset = layout.getOffsetForHorizontal(line, x);


        if (this.bezelMode == BEZEL_MODE_START) {

            this.selStart = expandSelection(offset, -1);
            this.selEnd = expandSelection(offset, +1);
            this.setSelection(this.selStart, this.selEnd);

        } else if (this.bezelMode == BEZEL_MODE_END_CUT
                || this.bezelMode == BEZEL_MODE_END_PASTE) {

            this.selEnd = expandSelection(offset, +1);
            this.setSelection(this.selStart, this.selEnd);
        }
    }


    private void performTextAction() {

        ClipboardManager clipboard = (android.content.ClipboardManager)
                this.getContext().getSystemService(Context.CLIPBOARD_SERVICE);

        // Switch selection start/end variables if necessary:
        int tmp = Math.min(this.selStart, this.selEnd);
        int tmp2 = Math.max(this.selStart, this.selEnd);
        this.selStart = tmp;
        this.selEnd = tmp2;

        // Cut:
        if (this.bezelMode == BEZEL_MODE_END_CUT) {
            this.setSelection(this.selStart, this.selEnd);
            ClipData clip = ClipData.newPlainText("Copied Text", this.getText().subSequence(this.selStart, this.selEnd));
            this.setText(this.getText().subSequence(0, this.selStart)
                    + "" + this.getText().subSequence(this.selEnd, this.getText().length()));
            clipboard.setPrimaryClip(clip);

        }
        // Paste:
        else if (this.bezelMode == BEZEL_MODE_END_PASTE) {
            this.setSelection(this.selStart, this.selEnd);
            this.setText(this.getText().subSequence(0, this.selStart) + ""
                    + clipboard.getPrimaryClip().getItemAt(0).getText()
                    + this.getText().subSequence(this.selEnd, this.getText().length()));
        }
    }

    public int getBezelMode() {
        return bezelMode;
    }

    public void setBezelMode(int bezelMode) {
        this.bezelMode = bezelMode;
    }


    private int expandSelection(int index, int direction) {

        int i = index;
        while (i > 0 && i < this.getText().length()
                && !" .:,;".contains(this.getText().charAt(i) + "")) {
            i += direction;
        }
        if (i > 0 && i < this.getText().length()
                && direction < 0 && this.getText().charAt(i) == ' ')
            i -= direction;

        i = Math.max(i, 0);
        return i;
    }

    public void setScrollViewScroll(int scrollViewScroll) {
        Log.d("BEZEL", "setScrollViewScroll: " + scrollViewScroll);
        this.scrollViewScroll = scrollViewScroll;
    }
}
