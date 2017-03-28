/*
 * This Sliding Tile Puzzle application for Android™ was created by Raymond Lam for the final project of SCS2682: Mobile Applications for Android Devices.
 *
 * Copyright © 2017 Raymond Lam. All rights reserved.
 *
 * No part of this application, either code or image, may be used for any purpose other than to evaluate his programming style.
 * Therefore, any reproduction or modification by any means is strictly prohibited without prior written permission.
 *
 */
package net.ddns.raylam.sliding_puzzle.data;


import java.util.Date;

public class GameHistory {
    public Date date = new Date();
    public int elapsedTime;
    public int moves;
    public int difficulty;

    public GameHistory() {};

    public GameHistory(Date date, int elapsedTime, int moves, int difficulty) {
        if (date != null)
            this.date = date;

        if (elapsedTime >= 0)
            this.elapsedTime = elapsedTime;

        if (moves >= 0)
            this.moves = moves;

        this.difficulty = difficulty;
    }

    public GameHistory(String date, String elapsedTime, String moves, String difficulty) {
        this.date = new Date(Date.parse(date));
        this.elapsedTime = Integer.parseInt(elapsedTime);
        this.moves = Integer.parseInt(moves);
        this.difficulty = Integer.parseInt(difficulty);
    }

    @Override
    public String toString() {
        return date + ":\t" + elapsedTime + "\t" + moves + "\t" + difficulty;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GameHistory && date.equals(((GameHistory) o).date);
    }
}
