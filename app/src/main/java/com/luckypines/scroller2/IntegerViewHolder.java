package com.luckypines.scroller2;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by fumiaki on 5/7/17.
 */

public class IntegerViewHolder extends RecyclerView.ViewHolder {
    private TextView text1;
    public IntegerViewHolder(View itemView) {
        super(itemView);
        text1 = (TextView)itemView.findViewById(R.id.text1);
    }

    public void setInteger(int value) {
        text1.setText(Integer.toString(value));
    }
}
