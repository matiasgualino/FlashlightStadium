package com.mgualino.stadiumshow.activities;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.LinearLayout;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mgualino.stadiumshow.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ColorActivity extends AppCompatActivity {

    private int i;
    LinearLayout layout;
    final Handler handler_interact=new Handler();
    final Runnable runnable_interact = new Runnable() {
        public void run() {
            layout.setBackgroundColor(colors.get(i%colors.size()));
        }
    };

    ArrayList<Integer> colors;
    Integer delayColor;
    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        window.addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_color);

        mActivity = this;

        final AdView mAdView = (AdView) findViewById(R.id.adView_colors);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        layout = (LinearLayout) findViewById(R.id.colorLayout);

        i = 0;
        colors = getIntent().getIntegerArrayListExtra("colors");
        delayColor = getIntent().getIntExtra("delayColor", 250);

        Timer timer_interact=new Timer();
        timer_interact.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                i++;
                UpdateGUI();
            }
        }, 0, delayColor);
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


        return super.onOptionsItemSelected(item);
    }

}
