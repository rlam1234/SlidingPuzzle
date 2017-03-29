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

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import net.ddns.raylam.sliding_puzzle.R;
import net.ddns.raylam.sliding_puzzle.data.GameHistory;

public class CellViewHolder extends RecyclerView.ViewHolder {
    private static final String NAME = CellViewHolder.class.getSimpleName();

    private TextView date;
    private TextView elapsedTime;
    private TextView moves;
//    private TextView difficulty;
    private int position;           // position in game play history

//    public CellViewHolder(View itemView, final OnHistoryClickListener historyClickListener) {
    public CellViewHolder(View itemView) {
        super(itemView);

        date = (TextView) itemView.findViewById(R.id.dateCell);
        elapsedTime = (TextView) itemView.findViewById(R.id.elapsedTimeCell);
        moves = (TextView) itemView.findViewById(R.id.movesCell);
//        difficulty = (TextView) itemView.findViewById(R.id.difficultyCell);

//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (historyClickListener != null) {
//                    GameHistory gameScore = new GameHistory(date.getText().toString(), elapsedTime.getText().toString(), moves.getText().toString(), difficulty.getText().toString());
//                    historyClickListener.onHistoryClick(gameScore, position);
//                }
//            }
//        });
    }

    public void update(GameHistory gameHistory, int position) {
        Log.w(NAME, "entering update(" + gameHistory + ", " + position + ")");
        date.setText(gameHistory.date.toString());
        elapsedTime.setText(gameHistory.elapsedTime);
        moves.setText(gameHistory.moves);
//        difficulty.setText(gameHistory.difficulty);
        this.position = position;
    }
}
