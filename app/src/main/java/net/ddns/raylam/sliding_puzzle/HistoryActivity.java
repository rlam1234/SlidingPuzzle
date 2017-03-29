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

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.ddns.raylam.sliding_puzzle.data.GameHistory;
import net.ddns.raylam.sliding_puzzle.ui.history.HistoryLayout;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    public static final String NAME = HistoryActivity.class.getSimpleName();

    private static final int DIFFICULTY_LEVELS = 3;
    private static final int DIFFICULTY_EASY = 0;
    private static final int DIFFICULTY_MEDIUM = 1;
    private static final int DIFFICULTY_HARD = 2;

    public static final class Adapter extends PagerAdapter {
        private static final String NAME = Adapter.class.getSimpleName();

        private final LayoutInflater layoutInflater;
        private final ViewPager viewPager;
        private HistoryLayout historyLayout;
        private int difficultyPage = DIFFICULTY_EASY;

        private List<List<GameHistory>> historyList = new ArrayList<List<GameHistory>>();

        private Adapter(ViewPager viewPager) {
            Log.w(NAME, "entering constructor Adapter(" + viewPager + ")");
            this.viewPager = viewPager;
            layoutInflater = LayoutInflater.from(viewPager.getContext());
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.w(NAME, "entering destroyItem(" + container + ", " + position + ", " + object);
            container.removeView((View) object);
            ((AppCompatActivity) container.getContext()).finish();
        }

        @Override
        public int getCount() {
            return DIFFICULTY_LEVELS;
        }

        @Override
        public CharSequence getPageTitle(int difficultyPage) {
            switch(difficultyPage) {
                case DIFFICULTY_HARD:
                    return ((AppCompatActivity) viewPager.getContext()).getString(R.string.difficultyText3);
                case DIFFICULTY_MEDIUM:
                    return ((AppCompatActivity) viewPager.getContext()).getString(R.string.difficultyText2);
                default: case DIFFICULTY_EASY:
                    return ((AppCompatActivity) viewPager.getContext()).getString(R.string.difficultyText1);
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int difficultyPage) {
            View view = layoutInflater.inflate(R.layout.gamehistory, container, false);
            container.addView(view);

            if (view instanceof HistoryLayout) {
                historyLayout = (HistoryLayout) view;
                historyLayout.setAdapter(this);
            } else {
                Log.w(NAME, "instantiateItem: view can be something other than HistoryLayout: " + view);
            }

            return view;
        }

        public void onDisplayHistory(GameHistory gameHistory, int difficultyPage, int historyPosition) {
            viewPager.setCurrentItem(difficultyPage, true);
            historyLayout.updateHistory(gameHistory, historyPosition);
        }
    }   // end class Adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historyactivity);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new Adapter(viewPager));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int previousPage = PuzzleActivity.DIFFICULTY1;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int page) {
                Log.w(NAME, "onCreate: entering onPageSelected(" + page + ")");

                TextView historyTitle = (TextView) findViewById(R.id.historyTitle);
                if (page == DIFFICULTY_EASY) {
                    historyTitle.setText(getString(R.string.play_history_easy));
                } else if (page == DIFFICULTY_MEDIUM) {
                    historyTitle.setText(getString(R.string.play_history_medium));
                } else {
                    historyTitle.setText(getString(R.string.play_history_hard));
                }

                previousPage = page;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }   // end onCreate

}
