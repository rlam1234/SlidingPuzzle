/*
 * This Sliding Tile Puzzle application for Android™ was created by Raymond Lam for the final project of SCS2682: Mobile Applications for Android Devices.
 *
 * Copyright © 2017 Raymond Lam. All rights reserved.
 *
 * No part of this application, either code or image, may be used for any purpose other than to evaluate his programming style.
 * Therefore, any reproduction or modification by any means is strictly prohibited without prior written permission.
 *
 */
package net.ddns.raylam.sliding_puzzle.ui.overflow;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import net.ddns.raylam.sliding_puzzle.HistoryActivity;
import net.ddns.raylam.sliding_puzzle.PuzzleActivity;
import net.ddns.raylam.sliding_puzzle.R;

public class ActionBarOverflow {
    // Name of this Activity; used for logging/debugging purposes
    public static final String NAME = PuzzleActivity.class.getSimpleName();

    // Tags for data saved/retrieved from saveInstanceState/onCreate
    private static final String NAME_EMPTY_ROW = "emptyTileRow";
    private static final String NAME_EMPTY_COLUMN = "emptyTileColumn";
    private static final String NAME_MOVES = "moves";
    private static final String NAME_ELAPSED_TIME = "elapsedTime";
    private static final String NAME_SOLVE_TIME = "solveTime";
    private static final String NAME_ID = "id";

    // Bundle difficulty name when passing to DifficultyDialog also used for SharedPreferences
    public static final String NAME_DIFFICULTY = "difficulty";

    public static final String NAME_GAME_HISTORY1 = "gameEasyHistory";
    public static final String NAME_GAME_HISTORY2 = "gameMediumHistory";
    public static final String NAME_GAME_HISTORY3 = "gameHardHistory";

    // Menu items
    public static final int MENU_SETTINGS = 1;
    public static final int MENU_HELP = 2;
    public static final int MENU_ABOUT = 3;
    public static final int MENU_HISTORY = 4;

    // Game difficulty levels
    public static final int DIFFICULTY_LEVELS = 3;  // number of difficulty levels
    public static final int DIFFICULTY1 = 1;       // Easy
    public static final int DIFFICULTY2 = 2;       // Medium
    public static final int DIFFICULTY3 = 3;       // Hard

    // Current game difficulty level
    private int difficulty = DIFFICULTY1;

    private final Activity activity;

    public ActionBarOverflow(@NonNull Activity activity) {
        this.activity = activity;
    }

    public void createMenuItems(Menu menu) {
        MenuItem mSettings = menu.add(0, MENU_SETTINGS, MENU_SETTINGS, R.string.settingsItem);
        mSettings.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        MenuItem mHelp = menu.add(0, MENU_HELP, MENU_HELP, R.string.helpItem);
        mHelp.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        MenuItem mAbout = menu.add(0, MENU_ABOUT, MENU_ABOUT, R.string.aboutItem);
        mAbout.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        if (activity instanceof PuzzleActivity) {
            MenuItem mHistory = menu.add(0, MENU_HISTORY, MENU_HISTORY, R.string.solveHistoryTitle);
            mHistory.setIcon(R.drawable.ic_library_books_white_24dp);
            mHistory.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
    }

    public boolean optionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SETTINGS:
                menuSettings();
                return true;
            case MENU_HELP:
                menuHelp();
                return true;
            case MENU_ABOUT:
                menuAbout();
                return true;
            case MENU_HISTORY:
                menuHistory();
                return true;
        }
        return false;
    }

    private void menuSettings() {
		// Log the soundEnabled and difficulty settings from SharedPreferences
        SharedPreferences sp = activity.getSharedPreferences(PuzzleActivity.NAME, Context.MODE_PRIVATE);
        Log.w(NAME, "soundEnabled = " + sp.getBoolean(PuzzleActivity.NAME_SOUND_ENABLED, true)
                + ", difficulty = " + sp.getInt(PuzzleActivity.NAME_DIFFICULTY, -1));

        activity.getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .add(new SettingsDialog(), SettingsDialog.NAME)
                .commit();
    }

    private void menuHelp() {
        activity.getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .add(new HelpDialog(), HelpDialog.NAME)
                .commit();
    }

    private void menuAbout() {
        activity.getFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .add(new AboutDialog(), AboutDialog.NAME)
                .commit();
    }

    // This method can only be called from a PuzzleActivity
    private void menuHistory() {
        ((PuzzleActivity) activity).stopTimer();

        activity.startActivity(new Intent(activity, HistoryActivity.class));

        ((PuzzleActivity) activity).initialize();
    }
}

