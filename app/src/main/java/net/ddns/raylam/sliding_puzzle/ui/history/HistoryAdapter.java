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
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.ddns.raylam.sliding_puzzle.R;
import net.ddns.raylam.sliding_puzzle.data.SolveHistory;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.NO_ID;

class HistoryAdapter extends RecyclerView.Adapter<CellViewHolder> {
    private static final String NAME = HistoryAdapter.class.getSimpleName();

    List<SolveHistory> history = new ArrayList<>();
    private final LayoutInflater layoutInflater;

    HistoryAdapter(@NonNull final Context context) {
        layoutInflater = LayoutInflater.from(context);
        setHasStableIds(true);
    }

    // Returns the stable ID for the item at the given position
    @Override
    public long getItemId(int position) {
        return position < 0 || position >= history.size() ? NO_ID : history.get(position).date.getTime();
    }

    @Override
    public CellViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        return new CellViewHolder(layoutInflater.inflate(R.layout.historycell, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final CellViewHolder holder, int position) {
        holder.update(history.get(position));
    }

    @Override
    public int getItemCount() {
        return history.size();
    }
}
