package com.example.tysw02.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


/**
 * Created by Lucas Yeh on 2015/12/23.
 */
public class RecyclerViewListHolder extends RecyclerView.ViewHolder{

    public TextView mainText;
    public TextView bottemText;
    public TextView rightText;

    public RecyclerViewListHolder(View itemView) {
        super(itemView);
        mainText = (TextView)itemView.findViewById(R.id.recyclerview_main_item_text);
        bottemText = (TextView)itemView.findViewById(R.id.recyclerview_bottom_text);
        rightText = (TextView)itemView.findViewById(R.id.recyclerview_right_text);
    }

}
