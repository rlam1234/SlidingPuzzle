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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import net.ddns.raylam.sliding_puzzle.HistoryActivity;
import net.ddns.raylam.sliding_puzzle.R;
import net.ddns.raylam.sliding_puzzle.data.SolveHistory;

import java.util.List;

public class HistoryLayout extends LinearLayout {
    private static final String NAME = HistoryLayout.class.getSimpleName();

    private static final AttributeSet DEFAULT_ATTRIBUTE_SET = null;
    private static final int DEFAULT_DEF_STYLE_ATTR = 0;
    private HistoryActivity.Adapter adapter;
    private HistoryAdapter historyAdapter;

    public HistoryLayout(final Context context) {
        this(context, DEFAULT_ATTRIBUTE_SET, DEFAULT_DEF_STYLE_ATTR);
    }

    public HistoryLayout(final Context context, final AttributeSet attributeSet) {
        this(context, attributeSet, DEFAULT_DEF_STYLE_ATTR);
    }

    public HistoryLayout(final Context context, final AttributeSet attributeSet, final int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);

        historyAdapter = new HistoryAdapter(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(historyAdapter);
    }

    public void setAdapter(final HistoryActivity.Adapter adapter) {
        this.adapter = adapter;
    }

    public void setHistory(@NonNull List<SolveHistory> history) {
        historyAdapter.history = history;

        // If there is no puzzle solve history for this difficulty level, show text saying so.
		if (history.size() == 0) {
			findViewById(R.id.nothingText).setVisibility(VISIBLE);
            findViewById(R.id.titleLayout).setVisibility(GONE);
		} else {
            findViewById(R.id.nothingText).setVisibility(GONE);
            findViewById(R.id.titleLayout).setVisibility(VISIBLE);
        }
    }

    public void updateHistory(SolveHistory solveHistory, int position) {
        if (solveHistory != null) {
            historyAdapter.history.add(0, solveHistory);
            for (int i = 0; i < historyAdapter.history.size(); i++)
                historyAdapter.notifyItemChanged(i);
        }
    }
}
