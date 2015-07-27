package com.mgualino.flashlightstadium;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ColorActivity extends AppCompatActivity {

    private int i;
    View bg;
    final Handler handler_interact=new Handler();
    final Runnable runnable_interact = new Runnable() {
        public void run() {
            bg.setBackgroundColor(colors.get(i%colors.size()));
        }
    };

    ArrayList<Integer> colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);

        bg = findViewById(R.id.bg);
        i = 0;
        colors = getIntent().getIntegerArrayListExtra("colors");

        Timer timer_interact=new Timer();
        timer_interact.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                i++;



                UpdateGUI();
            }
        }, 0, 500);
    }

    private void UpdateGUI() {
        handler_interact.post(runnable_interact);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_color, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
