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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class DifficultyActivity extends AppCompatActivity {
    private int difficulty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.difficultyactivity);

        // Check the currently selected difficulty level
        difficulty = getIntent().getIntExtra(PuzzleActivity.NAME_DIFFICULTY, PuzzleActivity.DIFFICULTY1);
        if (difficulty == PuzzleActivity.DIFFICULTY1)
            ((RadioButton) findViewById(R.id.difficulty1Button)).setChecked(true);
        else if (difficulty == PuzzleActivity.DIFFICULTY2)
            ((RadioButton) findViewById(R.id.difficulty2Button)).setChecked(true);
        else if (difficulty == PuzzleActivity.DIFFICULTY3)
            ((RadioButton) findViewById(R.id.difficulty3Button)).setChecked(true);

        ((RadioGroup) findViewById(R.id.difficultyGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                Log.w("DifficultyActivity", "onCheckedChanged: radioGroup = " + group + ", checkedId = " + checkedId);

                if (((RadioButton) findViewById(R.id.difficulty2Button)).isChecked())
                    difficulty = PuzzleActivity.DIFFICULTY2;

                if (((RadioButton) findViewById(R.id.difficulty3Button)).isChecked())
                    difficulty = PuzzleActivity.DIFFICULTY3;

                setResult(RESULT_OK, new Intent().putExtra(PuzzleActivity.NAME_DIFFICULTY, difficulty));
                finish();
            }
        });
    }
}
