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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.ddns.raylam.sliding_puzzle.data.SolveHistory;
import net.ddns.raylam.sliding_puzzle.ui.overflow.ActionBarOverflow;
import net.ddns.raylam.sliding_puzzle.ui.history.HistoryLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    public static final String NAME = HistoryActivity.class.getSimpleName();

    private static final int DIFFICULTY_LEVELS = 3;
    private static final int DIFFICULTY_EASY = 0;
    private static final int DIFFICULTY_MEDIUM = 1;
    private static final int DIFFICULTY_HARD = 2;

    private ActionBarOverflow actionBarOverflow;

    public static final class Adapter extends PagerAdapter {
        private static final String NAME = Adapter.class.getSimpleName();

        private final LayoutInflater layoutInflater;
        private final ViewPager viewPager;
        private HistoryLayout historyLayout;
        private List<List<SolveHistory>> historyList = new ArrayList<>(DIFFICULTY_LEVELS);

        private Adapter(@NonNull final ViewPager viewPager) {
            this.viewPager = viewPager;
            layoutInflater = LayoutInflater.from(viewPager.getContext());
        }

        @Override
        public void destroyItem(@NonNull final ViewGroup container, int position, @NonNull final Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return DIFFICULTY_LEVELS;
        }

        @Override
        public CharSequence getPageTitle(int difficultyPage) {
            if (difficultyPage == DIFFICULTY_HARD) {
				return viewPager.getContext().getString(R.string.play_history_hard);
			} else if (difficultyPage == DIFFICULTY_MEDIUM) {
				return viewPager.getContext().getString(R.string.play_history_medium);
			} else {
				return viewPager.getContext().getString(R.string.play_history_easy);
			}
        }

        @Override
        public boolean isViewFromObject(final View view, final Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(@NonNull final ViewGroup container, int difficulty) {
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
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historyactivity);
        actionBarOverflow = new ActionBarOverflow(this);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.solveHistoryTitle));

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new Adapter(viewPager);

        SharedPreferences sharedPreferences = getSharedPreferences(PuzzleActivity.NAME, MODE_PRIVATE);
        retrieveGameHistory(sharedPreferences.getString(PuzzleActivity.NAME_GAME_HISTORY1, "[]"),
                sharedPreferences.getString(PuzzleActivity.NAME_GAME_HISTORY2, "[]"),
                sharedPreferences.getString(PuzzleActivity.NAME_GAME_HISTORY3, "[]"));

        viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(sharedPreferences.getInt(PuzzleActivity.NAME_DIFFICULTY, PuzzleActivity.DIFFICULTY1) - 1);
    }   // end onCreate

    @Override
    public boolean onCreateOptionsMenu(@NonNull final Menu menu) {
        super.onCreateOptionsMenu(menu);

        actionBarOverflow.createMenuItems(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (actionBarOverflow.optionsItemSelected(item)) {
			return true;
		} else {
            finish();
            return super.onOptionsItemSelected(item);
        }
    }

    // Convert the solve history lists for easy, medium, hard difficulties from a JSON string to ArrayLists
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
