package com.mgualino.flashlightstadium.colorpicker;

import com.mgualino.flashlightstadium.R;

/**
 * Created by mgualino on 25/7/15.
 */
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.QuickContactBadge;

/**
 *
 * The color square used as an entry point to launching the {@link ColorPickerDialogDash}.
 */
public class CalendarColorSquare extends QuickContactBadge {

    public CalendarColorSquare(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarColorSquare(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setBackgroundColor(int color) {
        Drawable[] colorDrawable = new Drawable[] {
                getContext().getResources().getDrawable(R.drawable.calendar_color_square) };
        setImageDrawable(new ColorStateDrawable(colorDrawable, color));
    }
}