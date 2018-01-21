package com.example.t_hant.pagefold;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private FlipViewController flipView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        flipView = new FlipViewController(this);
        flipView.setAnimationBitmapFormat(Bitmap.Config.RGB_565);
        flipView.setAdapter(new UIAdapter(this));

        setContentView(flipView);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        flipView.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        flipView.onPause();
    }
}
