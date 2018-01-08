package com.svp.svp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.svp.svp.Objects.Navigation_Date;

import java.util.ArrayList;

/**
 * Created by Eric Schumacher on 04.01.2018.
 */

public class ListAdapter_NavigationDates extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Navigation_Date> mDates;

    public ListAdapter_NavigationDates(ArrayList<Navigation_Date> dates) {
        mDates = dates;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mDates.size();
    }
}
