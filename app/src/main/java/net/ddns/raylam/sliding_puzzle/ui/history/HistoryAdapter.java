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
import android.net.LocalSocketAddress;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.ddns.raylam.sliding_puzzle.R;
import net.ddns.raylam.sliding_puzzle.data.SolveHistory;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<CellViewHolder> {
    private static final String NAME = HistoryAdapter.class.getSimpleName();

    public List<SolveHistory> history = new ArrayList<>();
    private final LayoutInflater layoutInflater;

    public HistoryAdapter(Context context, ViewGroup layout) {
        Log.w(NAME, "constructing HistoryAdapter(" + context + ", " + layout + ")");

        layoutInflater = LayoutInflater.from(context);
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public CellViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CellViewHolder(layoutInflater.inflate(R.layout.historycell, parent, false));
    }

    @Override
    public void onBindViewHolder(CellViewHolder holder, int position) {
		Log.w(NAME, "onBindViewHolder: invoking holder.update(" + history.get(position) + ", " + position + ")");

        holder.update(history.get(position), position);
    }

    @Override
    public int getItemCount() {
        return history.size();
    }
}
