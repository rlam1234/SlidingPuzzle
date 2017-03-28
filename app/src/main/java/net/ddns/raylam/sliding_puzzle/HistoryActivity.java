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

import net.ddns.raylam.sliding_puzzle.data.GameHistory;
import net.ddns.raylam.sliding_puzzle.ui.history.HistoryLayout;

public class HistoryActivity extends AppCompatActivity {
    public static final String NAME = HistoryActivity.class.getSimpleName();

    public static final class Adapter extends PagerAdapter {
        private static final String NAME = Adapter.class.getSimpleName();

        private static final int DIFFICULTY_EASY = 1;
        private static final int DIFFICULTY_MEDIUM = 2;
        private static final int DIFFICULTY_HARD = 3;

        private final LayoutInflater layoutInflater;
        private final ViewPager viewPager;
        private HistoryLayout historyLayout;
        private int difficultyPage = DIFFICULTY_EASY;

        private Adapter(ViewPager viewPager) {
            Log.w(NAME, "entering constructor Adapter(" + viewPager + ")");
            this.viewPager = viewPager;
            layoutInflater = LayoutInflater.from(viewPager.getContext());
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.w(NAME, "entering destroyItem(" + container + ", " + position + ", " + object);
            ((AppCompatActivity) container.getContext()).finish();
        }

        @Override
        public int getCount() {
            return PuzzleActivity.DIFFICULTY_LEVELS;
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
            View view = layoutInflater.inflate(R.layout.historyactivity, container, false);
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

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        Adapter adapter = new Adapter(viewPager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }   // end onCreate

}
