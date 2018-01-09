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

package de.lmu.ifi.medien.probuidemos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import de.lmu.ifi.medien.probuidemos.demos.BezelDemo;
import de.lmu.ifi.medien.probuidemos.demos.ImageViewerDemo;
import de.lmu.ifi.medien.probuidemos.demos.ListDemo;
import de.lmu.ifi.medien.probuidemos.demos.MenuButtonDemo;
import de.lmu.ifi.medien.probuidemos.demos.PlayButtonDemo;
import de.lmu.ifi.medien.probuidemos.demos.RubbingDemo;
import de.lmu.ifi.medien.probuidemos.demos.SliderDemo;
import de.lmu.ifi.medien.probuidemos.demos.SlidingWidgetsDemo;
import de.lmu.ifi.medien.probuidemos.workshop.tasks.taskA.WorkshopActivityTaskA;
import de.lmu.ifi.medien.probuidemos.workshop.tasks.taskA2.WorkshopActivityTaskA2;
import de.lmu.ifi.medien.probuidemos.workshop.tasks.taskB.WorkshopActivityTaskB;
import de.lmu.ifi.medien.probuidemos.workshop.tasks.taskC.WorkshopActivityTaskC;
import de.lmu.ifi.medien.probuidemos.workshop.tasks.taskD.WorkshopActivityTaskD;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FOR THE WORKSHOP:
        // fill spinner:
        List<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Task A");
        spinnerArray.add("Task A2");
        spinnerArray.add("Task B");
        spinnerArray.add("Task C");
        spinnerArray.add("Task D");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.workshop_task_spinner);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);


    }



    public void launchWorkshop(View view) {
        Spinner spinner = (Spinner) findViewById(R.id.workshop_task_spinner);
        Intent intent = null;
        if (((String) spinner.getSelectedItem()).equals("Task A"))
            intent = new Intent(this, WorkshopActivityTaskA.class);
        else if (((String) spinner.getSelectedItem()).equals("Task A2"))
            intent = new Intent(this, WorkshopActivityTaskA2.class);
        else if (((String) spinner.getSelectedItem()).equals("Task B"))
            intent = new Intent(this, WorkshopActivityTaskB.class);
        else if (((String) spinner.getSelectedItem()).equals("Task C"))
            intent = new Intent(this, WorkshopActivityTaskC.class);
        else if (((String) spinner.getSelectedItem()).equals("Task D"))
            intent = new Intent(this, WorkshopActivityTaskD.class);

        if (intent != null)
            startActivity(intent);
    }



    public void launchPlayButtonDemo(View view) {
        Intent intent = new Intent(this, PlayButtonDemo.class);
        startActivity(intent);
    }

    public void launchSliderDemo(View view) {
        Intent intent = new Intent(this, SliderDemo.class);
        startActivity(intent);
    }

    public void launchMenuDemo(View view) {
        Intent intent = new Intent(this, MenuButtonDemo.class);
        startActivity(intent);
    }

    public void launchListDemo(View view) {
        Intent intent = new Intent(this, ListDemo.class);
        startActivity(intent);
    }

    public void launchImageViewerDemo(View view) {
        Intent intent = new Intent(this, ImageViewerDemo.class);
        startActivity(intent);
    }

    public void launchSlidingWidgetsDemo(View view) {
        Intent intent = new Intent(this, SlidingWidgetsDemo.class);
        startActivity(intent);
    }

    public void launchBezelDemo(View view) {
        Intent intent = new Intent(this, BezelDemo.class);
        startActivity(intent);
    }

    public void launchRubbingDemo(View view) {
        Intent intent = new Intent(this, RubbingDemo.class);
        startActivity(intent);
    }

    public void launchInfoScreen(View view) {
        Intent intent = new Intent(this, InfoScreen.class);
        startActivity(intent);
    }
}
