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

import android.support.annotation.NonNull;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.Format;
import net.ddns.raylam.sliding_puzzle.PuzzleActivity;
import net.ddns.raylam.sliding_puzzle.R;
import net.ddns.raylam.sliding_puzzle.data.SolveHistory;

class CellViewHolder extends RecyclerView.ViewHolder {
    private static final String NAME = CellViewHolder.class.getSimpleName();

    private View cell;
    private TextView date;
    private TextView elapsedTime;
    private TextView moves;

    private final Format DATE_FORMAT;
    private final Format TIME_FORMAT;

	// Limit the number of characters in the date/time to make sure it doesn't overflow the field
	private final int MAXIMUM_DATE_TIME_SIZE = 20;

    CellViewHolder(@NonNull final View itemView) {
        super(itemView);

        cell = itemView;

        date = (TextView) itemView.findViewById(R.id.dateCell);
        elapsedTime = (TextView) itemView.findViewById(R.id.elapsedTimeCell);
        moves = (TextView) itemView.findViewById(R.id.movesCell);

        // Setup the date/time formatters to use the system date/time format
        DATE_FORMAT = android.text.format.DateFormat.getDateFormat(itemView.getContext());
        TIME_FORMAT = android.text.format.DateFormat.getTimeFormat(itemView.getContext());
    }

    void update(@NonNull final SolveHistory solveHistory) {
		if (cell != null) {
			if (getLayoutPosition() % 2 == 0) {
				cell.setBackgroundColor(cell.getContext().getColor(R.color.androidGreen));
			} else {
				cell.setBackgroundColor(cell.getContext().getColor(R.color.white));
			}
		}

        String dateTime = ((SimpleDateFormat) DATE_FORMAT).format(solveHistory.date)
                +   " "
                +   ((SimpleDateFormat) TIME_FORMAT).format(solveHistory.date);

		// Set text to the first MAXIMUM_DATE_TIME_SIZE characters to prevent field overflow
        date.setText(dateTime.substring(0, Math.min(dateTime.length(), MAXIMUM_DATE_TIME_SIZE)));
        elapsedTime.setText(PuzzleActivity.intToHHMMSS(solveHistory.elapsedTime));
        moves.setText(NumberFormat.getInstance().format(solveHistory.moves));
    }
}
