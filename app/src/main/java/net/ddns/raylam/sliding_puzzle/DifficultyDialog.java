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

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import static android.app.Activity.RESULT_OK;

public class DifficultyDialog extends DialogFragment {
    public static String NAME = DifficultyDialog.class.getSimpleName();
    private PuzzleActivity activity;
    private RadioButton difficulty1Button;
    private RadioButton difficulty2Button;
    private RadioButton difficulty3Button;

    // Fragments require a no-arg constructor
    public DifficultyDialog() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.difficultyfragment, container);

        // Find the activity and radio buttons for later use
        activity = (PuzzleActivity) getActivity();
        difficulty1Button = (RadioButton) view.findViewById(R.id.difficulty1Button);
        difficulty2Button = (RadioButton) view.findViewById(R.id.difficulty2Button);
        difficulty3Button = (RadioButton) view.findViewById(R.id.difficulty3Button);

        getDialog().setTitle(R.string.difficultyItem);

        // Check the current difficulty level so the user knows what it is
        if (activity.difficulty == PuzzleActivity.DIFFICULTY1)
            difficulty1Button.setChecked(true);
        else if (activity.difficulty == PuzzleActivity.DIFFICULTY2)
            difficulty2Button.setChecked(true);
        else if (activity.difficulty == PuzzleActivity.DIFFICULTY3)
            difficulty3Button.setChecked(true);

        ((RadioGroup) view.findViewById(R.id.difficultyGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (difficulty1Button.isChecked())
                    activity.difficulty = PuzzleActivity.DIFFICULTY1;

                if (difficulty2Button.isChecked())
                    activity.difficulty = PuzzleActivity.DIFFICULTY2;

                if (difficulty3Button.isChecked())
                    activity.difficulty = PuzzleActivity.DIFFICULTY3;

                dismiss();
            }
        });

        return view;
    }
}
