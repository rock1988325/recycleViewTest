package com.example.tysw02.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.Set;

/**
 * Created by Cliff on 2017/09/07.
 */
public class RecyclerViewListAdapter extends RecyclerView.Adapter<RecyclerViewListHolder> {

    private Context context;
    private LinkedList<String> mediaFileXML;
    private LinkedList<Bitmap> imageList;
    private LinkedList<String> textList;
    private LinkedList<Boolean> imageDownloadedList;
    private Set<String> fileSet;

    public RecyclerViewListAdapter(@NonNull Context context,
                                   LinkedList<String> mediaFileXML){
        super();
        this.context = context;
        this.mediaFileXML = mediaFileXML;
    }

    @Override
    public RecyclerViewListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerviewlist_item, null);
        RecyclerViewListHolder holder = new RecyclerViewListHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewListHolder holder, int position) {
        //20181102 Cliff
        if(holder == null){
//            Log.e("onBindViewHolder","holder == null");
            return;
        }
        if(mediaFileXML==null)return;
        holder.mainText.setText(mediaFileXML.get(position));
    }

    @Override
    public int getItemCount() {
        return mediaFileXML.size();
    }

}
