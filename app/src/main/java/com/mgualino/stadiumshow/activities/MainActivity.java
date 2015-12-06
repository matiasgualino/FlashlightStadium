package com.mgualino.stadiumshow.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.mgualino.stadiumshow.adapters.ColorAdapter;
import com.mgualino.stadiumshow.R;
import com.mgualino.stadiumshow.controls.colorpicker.ColorPickerDialogDash;
import com.mgualino.stadiumshow.adapters.NsMenuAdapter;
import com.mgualino.stadiumshow.model.NsMenuItemModel;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import info.hoang8f.android.segmented.SegmentedGroup;

public class MainActivity extends ActionBarActivity {

    //flag to detect flash is on or off
    private boolean isLighOn = false;

    private Camera camera;
    private Camera.Parameters pon, poff;

    // Selected colors
    private int mSelectedColorDash0 = 0;
    private int mSelectedColorDash1 = 0;

    // Only for Menu
    private NsMenuAdapter mAdapter;

    private String[] menuItems;
    private static final int MENU_DASH_0 = 0;
    private static final int MENU_DASH_1 = 1;

    int mLastPosition;

    private Activity mActivity;
    RecyclerView recList;
    ColorAdapter ca;
    ArrayList<Integer> colorList;
    int lastIndex = -1;

    RadioButton r1;
    RadioButton r2;
    RadioButton r3;
    RadioButton r4;
    RadioButton r5;

    SegmentedGroup segmented;

    Switch flashlight_switch;
    CardView select_color_card_view;

    Boolean flashON = false;
    Integer delayColor = -1;

    private Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_main);

        mActivity = this;

        final AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);

        r1 = (RadioButton) findViewById(R.id.buttonON);
        r2 = (RadioButton) findViewById(R.id.button2);
        r3 = (RadioButton) findViewById(R.id.button3);
        r4 = (RadioButton) findViewById(R.id.button4);
        r5 = (RadioButton) findViewById(R.id.button5);
        segmented = (SegmentedGroup) findViewById(R.id.slider_selector_segmented);

        try {
            camera = Camera.open();
            pon = camera.getParameters();
            poff = camera.getParameters();
            pon.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            poff.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

            recList = (RecyclerView) findViewById(R.id.colors_cardList);
            recList.setHasFixedSize(true);

            DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

            segmented.setTintColor(Color.parseColor("#33B5E5"), Color.parseColor("#000000"));
            segmented.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    switch (checkedId) {
                        case R.id.buttonON:
                            lastIndex = 0;
                            break;
                        case R.id.button2:
                            lastIndex = 1;
                            break;
                        case R.id.button3:
                            lastIndex = 2;
                            break;
                        case R.id.button4:
                            lastIndex = 3;
                            break;
                        case R.id.button5:
                            lastIndex = 4;
                            break;
                    }

                    if (flashON) {
                        if (lastIndex == 0) {
                            try {
                                camera.setParameters(pon);
                            } catch (Exception ex) {
                                showErrorCamera();
                            }
                        } else {
                            double pot = 0.8 * lastIndex;
                            delayColor = new Double(Math.pow(2.7183, -pot) * 1000).intValue();
                            runTimer();
                        }
                    } else {
                        double pot = 0.5 * lastIndex;
                        delayColor = new Double(Math.pow(2.7183, -pot) * 1000).intValue();
                    }


                }
            });

            int cantCells = (int) (dpWidth/80.0f);

            recList.setLayoutManager(new GridLayoutManager(this, cantCells));


            final ColorPickerDialogDash colordash = (ColorPickerDialogDash)
                    getFragmentManager().findFragmentByTag("dash");
            if (colordash != null) {
                // re-bind listener to fragment
                colordash.setOnColorSelectedListener(colordashListner);
            }

            // Init colors to use in dialogs
            int[] mColor = NsMenuAdapter.ColorUtils.colorChoice(this);

            // Custom Dialog extracted from ColorPreference
            final ColorPickerDialogDash colordashfragment = ColorPickerDialogDash
                    .newInstance(R.string.color_picker_default_title, mColor,
                            mSelectedColorDash1, 5);

            // Implement listener to get selected color value
            colordashfragment.setOnColorSelectedListener(colordashListner);

            mAdapter = new NsMenuAdapter(this);

            // Read preferences to get selected Color
            SharedPreferences shared = PreferenceManager
                    .getDefaultSharedPreferences(this);
            if (shared != null) {
                mSelectedColorDash0 = shared.getInt("dash_colorkey", 0);
            }

            // -------------------------------------------------------------------------------------
            // Dashclock
            // -------------------------------------------------------------------------------------

            // Add Header
            mAdapter.addHeader(R.string.ns_menu_main_header_dash);
            // Add Dashclock items
            NsMenuItemModel mItem1 = new NsMenuItemModel(
                    R.string.ns_menu_main_row_dash_original, mSelectedColorDash0,
                    MENU_DASH_0);
            NsMenuItemModel mItem2 = new NsMenuItemModel(
                    R.string.ns_menu_main_row_dash_dialog, mSelectedColorDash1,
                    MENU_DASH_1);
            mAdapter.addItem(mItem1);
            mAdapter.addItem(mItem2);

            flashlight_switch = (Switch) findViewById(R.id.flashlight_switch);
            flashlight_switch.setChecked(false);
            flashlight_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {

                    if (isChecked) {
                        setRadios();

                        flashON = true;

                        recList.setVisibility(View.INVISIBLE);

                        Context context = MainActivity.this;
                        PackageManager pm = context.getPackageManager();

                        // if device support camera?
                        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                            Log.e("err", "Device has no camera!");
                            return;
                        }

                        if (camera == null) {
                            camera = Camera.open();
                        }

                        segmented.check(R.id.buttonON);
                        r1.setChecked(true);
                        r2.setChecked(false);
                        r3.setChecked(false);
                        r4.setChecked(false);
                        r5.setChecked(false);
                        lastIndex = 0;

                        try {
                            camera.setParameters(pon);
                        } catch (Exception ex) {
                            showErrorCamera();
                        }

                    } else {
                        recList.setVisibility(View.VISIBLE);
                        setRadios();
                        segmented.check(-1);
                        r1.setChecked(false);
                        r2.setChecked(false);
                        r3.setChecked(false);
                        r4.setChecked(false);
                        r5.setChecked(false);
                        lastIndex = -1;

                        flashON = false;
                        if (camera != null) {
                            try {
                                camera.setParameters(poff);
                                camera.stopPreview();
                            } catch (Exception ex) {
                                showErrorCamera();
                            }
                        }

                        isLighOn = false;
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                    }

                }
            });

            select_color_card_view = (CardView) findViewById(R.id.select_color_card_view);
            select_color_card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    colordashfragment.show(getFragmentManager(), "dash");
                }
            });


            setRadios();
            loadColorsList();
        } catch (Exception ex) {
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }

    }

    public void runTimer() {

        if (timer == null) {
            timer = new Timer();
        }

        if (camera != null) {
            try {
                double pot = 0.8 * lastIndex;
                delayColor = new Double(Math.pow(2.7183, -pot) * 1000).intValue();
                isLighOn = true;
                camera.setParameters(pon);
                timer = new Timer();

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!isLighOn) {
                            camera.setParameters(pon);
                            isLighOn = true;
                        } else {
                            camera.setParameters(poff);
                            isLighOn = false;
                        }
                    }
                }, 0, delayColor);
            } catch (Exception ex) {
                showErrorCamera();
            }
        } else {
            showErrorCamera();
        }
    }

    public void showErrorCamera() {
        new AlertDialog.Builder(this)
                .setTitle("ERROR")
                .setMessage(R.string.error_camera)
                .setNeutralButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void setRadios() {
        r1.setText(flashlight_switch.isChecked() ? "ON" : "1");
        r2.setText(flashlight_switch.isChecked() ? "1" : "2");
        r3.setText(flashlight_switch.isChecked() ? "2" : "3");
        r4.setText(flashlight_switch.isChecked() ? "3" : "4");
        r5.setText(flashlight_switch.isChecked() ? "4" : "5");
    }

    public void loadColorsList() {
        if (colorList == null) {
            colorList = new ArrayList<Integer>();
        }

        ca = new ColorAdapter(colorList);
        recList.setAdapter(ca);
    }

    ColorPickerDialogDash.OnColorSelectedListener colordashListner = new ColorPickerDialogDash.OnColorSelectedListener() {

        @Override
        public void onColorSelected(int color) {
            mSelectedColorDash1 = color;
            colorList.add(color);
            NsMenuItemModel item = mAdapter.getItem(mLastPosition);
            if (item != null)
                item.colorSquare = color;
            mAdapter.notifyDataSetChanged();
            loadColorsList();
        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_iniciar) {
            if (flashlight_switch.isChecked()) {
                new AlertDialog.Builder(this)
                        .setTitle("ERROR")
                        .setMessage(R.string.error_flashlight)
                        .setNeutralButton("OK", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else {
                if (segmented.getCheckedRadioButtonId() != -1) {
                    if (colorList != null && colorList.size() > 0) {
                        double pot = 0.5 * lastIndex;
                        delayColor = new Double(Math.pow(2.7183, -pot) * 1000).intValue();
                        if (delayColor > 0) {
                            Intent intent = new Intent(mActivity, ColorActivity.class);
                            intent.putIntegerArrayListExtra("colors", colorList);
                            flashON = false;
                            isLighOn = false;
                            intent.putExtra("delayColor", delayColor);
                            mActivity.startActivity(intent);
                        } else {
                            new AlertDialog.Builder(this)
                                    .setTitle("ERROR")
                                    .setMessage(R.string.error_velocity)
                                    .setNeutralButton("OK", null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("ERROR")
                                .setMessage(R.string.error_colors)
                                .setNeutralButton("OK", null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("ERROR")
                            .setMessage(R.string.error_velocity)
                            .setNeutralButton("OK", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void eliminarColor(View view) {
        int pos = Integer.parseInt(view.getTag().toString());
        colorList.remove(pos);
        loadColorsList();
    }

    public void onBackPressed(){
        finish();
        System.exit(0);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                finish();
                System.exit(0);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
