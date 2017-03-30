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

import java.text.SimpleDateFormat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.Format;
import java.util.Date;
import net.ddns.raylam.sliding_puzzle.PuzzleActivity;
import net.ddns.raylam.sliding_puzzle.R;
import net.ddns.raylam.sliding_puzzle.data.SolveHistory;

public class CellViewHolder extends RecyclerView.ViewHolder {
    private static final String NAME = CellViewHolder.class.getSimpleName();

    private TextView date;
    private TextView elapsedTime;
    private TextView moves;
    private int position;           // position in game play history

    public CellViewHolder(View itemView) {
        super(itemView);

        Log.w(NAME, "constructing CellViewHolder(" + itemView + ")");

        date = (TextView) itemView.findViewById(R.id.dateCell);
        elapsedTime = (TextView) itemView.findViewById(R.id.elapsedTimeCell);
        moves = (TextView) itemView.findViewById(R.id.movesCell);

        Format dateFormat = android.text.format.DateFormat.getDateFormat(itemView.getContext());
        String pattern = ((SimpleDateFormat) dateFormat).toLocalizedPattern();

        Log.w(NAME, "date = " + ((SimpleDateFormat) dateFormat).format(new Date()));
    }

    public void update(SolveHistory solveHistory, int position) {
        Log.w(NAME, "entering update(" + solveHistory + ", " + position + ")");
        date.setText(solveHistory.date.toString());
        elapsedTime.setText(PuzzleActivity.intToHHMMSS(solveHistory.elapsedTime));
        moves.setText("" + solveHistory.moves);
        this.position = position;
    }
}
