/*
 * This Sliding Tile Puzzle application for Android™ was created by Raymond Lam for the final project of SCS2682: Mobile Applications for Android Devices.
 *
 * Copyright © 2017 Raymond Lam. All rights reserved.
 *
 * No part of this application, either code or image, may be used for any purpose other than to evaluate his programming style.
 * Therefore, any reproduction or modification by any means is strictly prohibited without prior written permission.
 *
 */
package net.ddns.raylam.sliding_puzzle.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import net.ddns.raylam.sliding_puzzle.PuzzleActivity;
import net.ddns.raylam.sliding_puzzle.R;

import static android.content.Context.MODE_PRIVATE;

public class DifficultyDialog extends DialogFragment {
    public static String NAME = DifficultyDialog.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private RadioButton difficulty1Button;
    private RadioButton difficulty2Button;
    private RadioButton difficulty3Button;
    private int difficulty;

    // Fragments require a no-arg constructor
    public DifficultyDialog() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.difficultyfragment, container);

        // Find the activity and radio buttons for later use
        difficulty1Button = (RadioButton) view.findViewById(R.id.difficulty1Button);
        difficulty2Button = (RadioButton) view.findViewById(R.id.difficulty2Button);
        difficulty3Button = (RadioButton) view.findViewById(R.id.difficulty3Button);

        getDialog().setTitle(R.string.difficultyItem);

        sharedPreferences = getActivity().getSharedPreferences(PuzzleActivity.NAME, MODE_PRIVATE);
        difficulty = sharedPreferences.getInt(PuzzleActivity.NAME_DIFFICULTY, PuzzleActivity.DIFFICULTY1);

        // Check the current difficulty level so the user knows what it is
        if (difficulty == PuzzleActivity.DIFFICULTY1)
            difficulty1Button.setChecked(true);
        else if (difficulty == PuzzleActivity.DIFFICULTY2)
            difficulty2Button.setChecked(true);
        else if (difficulty == PuzzleActivity.DIFFICULTY3)
            difficulty3Button.setChecked(true);

        ((RadioGroup) view.findViewById(R.id.difficultyGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (difficulty1Button.isChecked())
                    saveDifficulty(PuzzleActivity.DIFFICULTY1);

                if (difficulty2Button.isChecked())
                    saveDifficulty(PuzzleActivity.DIFFICULTY2);

                if (difficulty3Button.isChecked())
                    saveDifficulty(PuzzleActivity.DIFFICULTY3);

//                dismiss();
            }
        });

        return view;
    }

    private void saveDifficulty(int difficulty) {
        sharedPreferences.edit().putInt(PuzzleActivity.NAME_DIFFICULTY, difficulty).apply();
    }
}
