package com.mgualino.flashlightstadium;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.gc.materialdesign.views.Slider;
import com.github.clans.fab.FloatingActionButton;
import com.mgualino.flashlightstadium.colorpicker.ColorPickerDialogDash;
import com.mgualino.flashlightstadium.colorpicker.NsMenuAdapter;
import com.mgualino.flashlightstadium.colorpicker.NsMenuItemModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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
    Button btnComenzar;
    ArrayList<Integer> colorList;
    TextView lblIntro;

    Slider slider;

    Integer delayColor = 250;
    Double ms;

    String INTRO_TXT = "Desliza para cambiar la duración de cada color. Duración actual: ### segundos ($$$ ms).";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;

        lblIntro = (TextView) findViewById(R.id.lblIntro);
        final DecimalFormat df = new DecimalFormat("0.00");
        ms = delayColor/1000.0;

        lblIntro.setText(INTRO_TXT.replace("###", df.format(ms)).replace("$$$", delayColor.toString()));

        slider = (Slider) findViewById(R.id.slider);
        slider.setValue(delayColor);
        slider.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int i) {
                delayColor = i;
                ms = delayColor/1000.0;
                lblIntro.setText(INTRO_TXT.replace("###", df.format(ms)).replace("$$$", delayColor.toString()));
            }
        });

        recList = (RecyclerView) findViewById(R.id.colors_cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

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

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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

        btnComenzar = (Button) findViewById(R.id.btnComenzar);
        btnComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, ColorActivity.class);
                intent.putIntegerArrayListExtra("colors", colorList);
                intent.putExtra("delayColor", delayColor);
                mActivity.startActivity(intent);
            }
        });

        loadColorsList();

    }

    public void loadColorsList() {
        if (colorList == null) {
            colorList = new ArrayList<Integer>();
        }

        btnComenzar.setEnabled(colorList.size() > 0);

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

    public void eliminarColor(View view) {
        int pos = Integer.parseInt(view.getTag().toString());
        colorList.remove(pos);
        loadColorsList();
    }
}
