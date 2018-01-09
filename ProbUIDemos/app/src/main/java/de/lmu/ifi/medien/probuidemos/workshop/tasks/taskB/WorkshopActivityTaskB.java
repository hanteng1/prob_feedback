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

package de.lmu.ifi.medien.probuidemos.workshop.tasks.taskB;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver;

import de.lmu.ifi.medien.probuidemos.R;
import de.lmu.ifi.medien.probui.system.ProbUIManager;

public class WorkshopActivityTaskB extends AppCompatActivity {

    // Images to load:
    private int[] imageIDs = {R.drawable.workshop_task_b_img_01_s,
            R.drawable.workshop_task_b_img_02_s,
            R.drawable.workshop_task_b_img_03_s};
    private MyProbUIImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workshop_activity_task_b);

        // Load the images:
        BitmapDrawable[] images = new BitmapDrawable[imageIDs.length];
        int i = 0;
        for (int imageID : imageIDs) {
            images[i] = (BitmapDrawable) getResources().getDrawable(imageID);
            i++;
        }
        // Set images for the view:
        this.imageView = ((MyProbUIImageView) findViewById(R.id.imageView));
        imageView.setImages(images);

        ViewTreeObserver vto = imageView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageView.updateImage();
            }
        });

        // ProbUI setup:
        ProbUIManager manager = new ProbUIManager(
                findViewById(android.R.id.content).getRootView(), R.id.probUIContainer);
        manager.autoAssignInteractors();


    }

}
