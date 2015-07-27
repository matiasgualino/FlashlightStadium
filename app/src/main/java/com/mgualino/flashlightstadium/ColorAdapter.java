package com.mgualino.flashlightstadium;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mgualino on 26/7/15.
 */
public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

    private List<Integer> colorsList;

    public ColorAdapter (List<Integer> colorsList) {
        this.colorsList = colorsList;
    }

    @Override
    public int getItemCount() {
        return colorsList.size();
    }

    @Override
    public void onBindViewHolder(ColorViewHolder colorViewHolder, int i) {
        Integer color = colorsList.get(i);
        colorViewHolder.cardColor.setBackgroundColor(color);
        colorViewHolder.deleteButton.setTag("" + i);
    }

    @Override
    public ColorViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.color_card_view, viewGroup, false);
        return new ColorViewHolder(itemView);
    }


    public static class ColorViewHolder extends RecyclerView.ViewHolder {

        protected View cardColor;
        protected TextView deleteButton;

        public ColorViewHolder(View v) {
            super(v);
            cardColor =  (View) v.findViewById(R.id.cardColor);
            deleteButton =  (TextView) v.findViewById(R.id.deleteButton);
        }
    }

}
