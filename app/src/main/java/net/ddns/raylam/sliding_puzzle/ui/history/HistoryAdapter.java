/*
 * This Sliding Tile Puzzle application for Android™ was created by Raymond Lam for the final project of SCS2682: Mobile Applications for Android Devices.
 *
 * Copyright © 2017 Raymond Lam. All rights reserved.
 *
 * No part of this application, either code or image, may be used for any purpose other than to evaluate his programming style.
 * Therefore, any reproduction or modification by any means is strictly prohibited without prior written permission.
 *
 */
package net.ddns.raylam.sliding_puzzle.ui.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.ddns.raylam.sliding_puzzle.R;
import net.ddns.raylam.sliding_puzzle.data.GameHistory;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<CellViewHolder> {
    public List<GameHistory> history = new ArrayList<>();            // game play historyactivity
    private final LayoutInflater layoutInflater;
//    private final OnHistoryClickListener historyClickListener;

//    public HistoryAdapter(Context context, OnHistoryClickListener historyClickListener) {
    public HistoryAdapter(Context context, ViewGroup layout) {
        layoutInflater = LayoutInflater.from(context);
//        this.historyClickListener = historyClickListener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public CellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return new CellViewHolder(layoutInflater.inflate(R.layout.historycell, parent, false), historyClickListener);
        return new CellViewHolder(layoutInflater.inflate(R.layout.historycell, parent, false));
    }

    @Override
    public void onBindViewHolder(CellViewHolder holder, int position) {
        holder.update(history.get(position), position);
    }

    @Override
    public int getItemCount() {
        return history.size();
    }
}
