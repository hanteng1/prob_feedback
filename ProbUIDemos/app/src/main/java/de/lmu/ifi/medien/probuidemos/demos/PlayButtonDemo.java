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

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;


import de.lmu.ifi.medien.probui.system.ProbUIManager;
import de.lmu.ifi.medien.probuidemos.R;
import de.lmu.ifi.medien.probuidemos.gui.elements.PlayButton;

public class PlayButtonDemo extends Activity {


    private int trackIndex = 0;
    private int[] trackImageIDs = {R.drawable.play_demo_image_1, R.drawable.play_demo_image_2};
    private Drawable[] images;

    private SeekBar proggy;
    private boolean playing;


    private Thread thread;
    private PlayButton playButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_button_demo);


        this.images = new Drawable[trackImageIDs.length];
        int i = 0;
        for (int trackImageID : trackImageIDs) {
            this.images[i] = getResources().getDrawable(trackImageID);
            i++;
        }

        this.proggy = (SeekBar) findViewById(R.id.progressBar);
        this.proggy.setMax(100);

        ProbUIManager manager = new ProbUIManager(findViewById(android.R.id.content).getRootView(),
                R.id.probUIContainer);
        manager.autoAssignInteractors();


        this.playButton = (PlayButton) findViewById(R.id.playButton);


        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    updatePlaying();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.thread.start();


    }


    public void onDestroy() {

        super.onDestroy();
        this.thread.interrupt();
    }


    public void onClickPlayButton(View view) {

        int trackIndexOld = trackIndex;

        PlayButton pb = (PlayButton) view;
        if (pb.getLastAction() == PlayButton.ACTION_FSKP)
            trackIndex++;
        else if (pb.getLastAction() == PlayButton.ACTION_BSKP)
            trackIndex--;

        if (trackIndex > trackImageIDs.length - 1)
            trackIndex = trackImageIDs.length - 1;
        if (trackIndex < 0)
            trackIndex = 0;

        if (trackIndexOld != trackIndex) {
            ((ImageView) findViewById(R.id.imageView)).setImageDrawable(this.images[trackIndex]);//.setImageResource(trackImageIDs[trackIndex]);
            this.proggy.setProgress(0);
        }


        if (pb.getLastAction() == PlayButton.ACTION_PLAY) {
            this.playing = true;
            Log.d("PLAYDEMO", "playing set to true!");
        } else if (pb.getLastAction() == PlayButton.ACTION_PAUSE
                || pb.getLastAction() == PlayButton.ACTION_FSKP
                || pb.getLastAction() == PlayButton.ACTION_BSKP) {
            this.playing = false;
            Log.d("PLAYDEMO", "playing set to false!");
        }
    }


    private void updatePlaying() {

        this.proggy.incrementProgressBy(this.playButton.getWinding() * 3);
        if (this.playing) {
            this.proggy.incrementProgressBy(1);
            Log.d("PLAYDEMO", "progress bar update!");
        }
    }
}
