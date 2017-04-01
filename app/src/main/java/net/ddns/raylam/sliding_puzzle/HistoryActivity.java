/*
 * This Sliding Tile Puzzle application for Android™ was created by Raymond Lam for the final project of SCS2682: Mobile Applications for Android Devices.
 *
 * Copyright © 2017 Raymond Lam. All rights reserved.
 *
 * No part of this application, either code or image, may be used for any purpose other than to evaluate his programming style.
 * Therefore, any reproduction or modification by any means is strictly prohibited without prior written permission.
 *
 */
package net.ddns.raylam.sliding_puzzle;

import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.ddns.raylam.sliding_puzzle.data.SolveHistory;
import net.ddns.raylam.sliding_puzzle.ui.AboutDialog;
import net.ddns.raylam.sliding_puzzle.ui.DifficultyDialog;
import net.ddns.raylam.sliding_puzzle.ui.HelpDialog;
import net.ddns.raylam.sliding_puzzle.ui.history.HistoryLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

public class HistoryActivity extends AppCompatActivity {
    public static final String NAME = HistoryActivity.class.getSimpleName();

    private static final int DIFFICULTY_LEVELS = 3;
    private static final int DIFFICULTY_EASY = 0;
    private static final int DIFFICULTY_MEDIUM = 1;
    private static final int DIFFICULTY_HARD = 2;

    private int difficulty;
    private ActionBarOverflow actionBarOverflow;

    public static final class Adapter extends PagerAdapter {
        private static final String NAME = Adapter.class.getSimpleName();

        private final LayoutInflater layoutInflater;
        private final ViewPager viewPager;
        private HistoryLayout historyLayout;
        private List<List<SolveHistory>> historyList = new ArrayList<List<SolveHistory>>(3);

        private Adapter(ViewPager viewPager) {
            this.viewPager = viewPager;
            layoutInflater = LayoutInflater.from(viewPager.getContext());
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return DIFFICULTY_LEVELS;
        }

        @Override
        public CharSequence getPageTitle(int difficultyPage) {
            switch(difficultyPage) {
                case DIFFICULTY_HARD:
                    return ((AppCompatActivity) viewPager.getContext()).getString(R.string.play_history_hard);
                case DIFFICULTY_MEDIUM:
                    return ((AppCompatActivity) viewPager.getContext()).getString(R.string.play_history_medium);
                default: case DIFFICULTY_EASY:
                    return ((AppCompatActivity) viewPager.getContext()).getString(R.string.play_history_easy);
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int difficulty) {
            View view = layoutInflater.inflate(R.layout.gamehistory, container, false);
            container.addView(view);

            if (view instanceof HistoryLayout) {
                historyLayout = (HistoryLayout) view;
                historyLayout.setHistory(historyList.get(difficulty));
                historyLayout.setAdapter(this);
            }

            return view;
        }
    }   // end class Adapter

    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historyactivity);
        actionBarOverflow = new ActionBarOverflow(this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new Adapter(viewPager);

        SharedPreferences sharedPreferences = getSharedPreferences(PuzzleActivity.NAME, MODE_PRIVATE);
        retrieveGameHistory(sharedPreferences.getString(PuzzleActivity.NAME_GAME_HISTORY1, "[]"),
                sharedPreferences.getString(PuzzleActivity.NAME_GAME_HISTORY2, "[]"),
                sharedPreferences.getString(PuzzleActivity.NAME_GAME_HISTORY3, "[]"));

        difficulty = sharedPreferences.getInt(PuzzleActivity.NAME_DIFFICULTY, DIFFICULTY_EASY);

        viewPager.setAdapter(adapter);
    }   // end onCreate

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);

        actionBarOverflow.createMenuItems(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        return actionBarOverflow.optionsItemSelected(item);
    }

    private void retrieveGameHistory(final String easyJson, final String mediumJson, final String hardJson) {
        Gson historyGson = new Gson();
        Type historyType = new TypeToken<List<SolveHistory>>() {}.getType();

        if (easyJson == null || easyJson.equals("[]")) {
            adapter.historyList.add(new ArrayList<SolveHistory>());
        } else {
            adapter.historyList.add((ArrayList<SolveHistory>) historyGson.fromJson(easyJson, historyType));
        }

        if (mediumJson == null || mediumJson.equals("[]")) {
            adapter.historyList.add(new ArrayList<SolveHistory>());
        } else {
            adapter.historyList.add((ArrayList<SolveHistory>) historyGson.fromJson(mediumJson, historyType));
        }

        if (hardJson == null || hardJson.equals("[]")) {
            adapter.historyList.add(new ArrayList<SolveHistory>());
        } else {
            adapter.historyList.add((ArrayList<SolveHistory>) historyGson.fromJson(hardJson, historyType));
        }
    }
}
