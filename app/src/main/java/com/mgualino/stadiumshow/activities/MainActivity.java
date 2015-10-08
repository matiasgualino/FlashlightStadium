package com.mgualino.stadiumshow.activities;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.Slider;
import com.github.clans.fab.FloatingActionButton;
import com.mgualino.stadiumshow.adapters.ColorAdapter;
import com.mgualino.stadiumshow.R;
import com.mgualino.stadiumshow.controls.colorpicker.ColorPickerDialogDash;
import com.mgualino.stadiumshow.adapters.NsMenuAdapter;
import com.mgualino.stadiumshow.controls.segmentedslider.OnSegmentSelectedListener;
import com.mgualino.stadiumshow.controls.segmentedslider.SliderSelector;
import com.mgualino.stadiumshow.model.NsMenuItemModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.w3c.dom.Text;

import info.hoang8f.android.segmented.SegmentedGroup;

public class MainActivity extends ActionBarActivity {

    //flag to detect flash is on or off
    private boolean isLighOn = false;

    private Camera camera;

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
  //  Button btnComenzar;
    ArrayList<Integer> colorList;
    //TextView lblIntro;
    TextView lblColors;
    ArrayList<View> mViews;
    int lastIndex = 0;

    RadioButton r1;
    RadioButton r2;
    RadioButton r3;
    RadioButton r4;
    RadioButton r5;

    SegmentedGroup segmented;
    ImageView imageColor;
    ImageView imageFlashlight;

    Camera.Parameters p;

    Button btnAddColor;

    ImageView imageOn;

    Boolean flashON = false;

    private static final int MILIS_FACTOR = 220;
    private static final int CANT_RANGES = 5;
    Integer delayColor = MILIS_FACTOR*CANT_RANGES;

  //  String INTRO_TXT = "Seleccioná la velocidad con la que se cambian los colores.";

    private boolean running = false;
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

        imageFlashlight = (ImageView) findViewById(R.id.imageFlash);
        imageColor= (ImageView) findViewById(R.id.imageColor);
        imageOn = (ImageView) findViewById(R.id.imageON);
        r1 = (RadioButton) findViewById(R.id.buttonON);
        r2= (RadioButton) findViewById(R.id.button2);
        r3 = (RadioButton) findViewById(R.id.button3);
        r4 = (RadioButton) findViewById(R.id.button4);
        r5 = (RadioButton) findViewById(R.id.button5);
        segmented = (SegmentedGroup) findViewById(R.id.slider_selector_segmented);

        camera = Camera.open();
        p = camera.getParameters();

        /*lblColors = (TextView) findViewById(R.id.lblColors);
        lblColors.setTextColor(getResources().getColor(R.color.white));
*/

/*
        // Init segments
        mViews = new ArrayList<>();
        SliderSelector mSliderSelector = (SliderSelector)findViewById(R.id.slider_selector);

        for (int i = 0; i < CANT_RANGES; i++) {
            TextView mTextView = new TextView(this);
            mTextView.setText("" + (i + 1));
            if (i == 0) {
                mTextView.setTextColor(getResources().getColor(R.color.black));
            } else {
                mTextView.setTextColor(getResources().getColor(R.color.white));
            }
            mViews.add(mTextView);
        }

        mSliderSelector.setSegmentViews(mViews);
        mSliderSelector.setSegmentSelectedListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(int segmentIndex) {
                TextView mTextViewOld = (TextView) mViews.get(lastIndex);
                mTextViewOld.setTextColor(getResources().getColor(R.color.white));
                mViews.remove(lastIndex);
                mViews.add(lastIndex, mTextViewOld);

                TextView mTextView = (TextView) mViews.get(segmentIndex);
                mTextView.setTextColor(getResources().getColor(R.color.black));
                mViews.remove(segmentIndex);
                mViews.add(segmentIndex, mTextView);
                delayColor = MILIS_FACTOR * (CANT_RANGES - segmentIndex);
                lastIndex = segmentIndex;
            }
        });

        /*slider = (Slider) findViewById(R.id.slider);
        slider.setValue(delayColor);
        slider.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int i) {

                lblIntro.setText(INTRO_TXT.replace("###", df.format(ms)).replace("$$$", delayColor.toString()));
            }
        });*/
/*


   /*     final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fab.show(true);
                fab.setShowAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.animator.show_from_bottom));
                fab.setHideAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.animator.hide_to_bottom));
            }
        }, 300);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colordashfragment.show(getFragmentManager(), "dash");
            }
        });
*/
     /*   btnAddColor = (Button) findViewById(R.id.btnAddColor);
        btnAddColor.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               colordashfragment.show(getFragmentManager(), "dash");
                                           }
                                       });
       /* btnComenzar = (Button) findViewById(R.id.btnComenzar);
        btnComenzar.setTextColor(getResources().getColor(R.color.white));
        btnComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

/*            }
        });
*/

        recList = (RecyclerView) findViewById(R.id.colors_cardList);
        recList.setHasFixedSize(true);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int size = (int) ((dpWidth - 20)/5.0f);

        segmented.setTintColor(Color.parseColor("#000000"), Color.parseColor("#FFFFFF"));
        segmented.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                switch(checkedId) {
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
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(p);
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

        setRadios();

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

        imageFlashlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setRadios();

                if (camera == null) {
                    camera = Camera.open();
                }

                flashON = true;

                imageOn.setVisibility(View.GONE);
                recList.setVisibility(View.INVISIBLE);

                Context context = MainActivity.this;
                PackageManager pm = context.getPackageManager();

                // if device support camera?
                if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    Log.e("err", "Device has no camera!");
                    return;
                }

                if (lastIndex == 0) {
                    isLighOn = true;
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(p);
                } else {
                    double pot = 0.8 * lastIndex;
                    delayColor = new Double(Math.pow(2.7183, -pot) * 1000).intValue();
                    runTimer();
                }

            }
        });

        imageColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageOn.setVisibility(View.VISIBLE);
                recList.setVisibility(View.VISIBLE);
                setRadios();
                flashON = false;
                if (camera != null) {
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(p);
                    camera.stopPreview();
                    camera = null;
                }

                isLighOn = false;
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }

                colordashfragment.show(getFragmentManager(), "dash");
            }
        });

        imageOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ColorActivity.class);
                intent.putIntegerArrayListExtra("colors", colorList);
                flashON = false;
                double pot = 0.3 * lastIndex;
                double delay = Math.pow(2.7183, -pot);
                intent.putExtra("delayColor", delay);
                mActivity.startActivity(intent);
            }
        });

        loadColorsList();

    }

    public void runTimer() {

        if (timer == null) {
            timer = new Timer();
        }
        final Camera.Parameters p = camera.getParameters();
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isLighOn) {
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(p);
                    isLighOn = true;
                } else {
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(p);
                    isLighOn = false;
                }
            }
        }, 0, delayColor);



    }

    public void setRadios() {
        r1.setText(imageOn.getVisibility() == View.GONE ? "ON" : "1");
        r2.setText(imageOn.getVisibility() == View.GONE ? "1" : "2");
        r3.setText(imageOn.getVisibility() == View.GONE ? "2" : "3");
        r4.setText(imageOn.getVisibility() == View.GONE ? "3" : "4");
        r5.setText(imageOn.getVisibility() == View.GONE ? "4" : "5");
    }

    public void loadColorsList() {
        if (colorList == null) {
            colorList = new ArrayList<Integer>();
        }

        imageOn.setVisibility(colorList.size() > 0 ? View.VISIBLE : View.GONE);

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

        if (camera != null) {
            camera.release();
        }
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
