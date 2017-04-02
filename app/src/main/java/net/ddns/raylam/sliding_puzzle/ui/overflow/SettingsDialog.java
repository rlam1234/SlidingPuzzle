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
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;

import net.ddns.raylam.sliding_puzzle.PuzzleActivity;
import net.ddns.raylam.sliding_puzzle.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class SettingsDialog extends DialogFragment {
    public static String NAME = SettingsDialog.class.getSimpleName();
    private SharedPreferences sharedPreferences;

    // Sound Settings
    private Switch soundSwitch;
    private SeekBar volumeBar;
    private boolean soundEnabled;

    // Difficulty Settings
    private int difficulty;
    private Spinner spinner;
    private SpinnerAdapter spinnerAdapter;
    private List<String> difficultyLevels = new ArrayList<>(3);


    private static final class SpinnerAdapter extends ArrayAdapter<String> {
        private final LayoutInflater layoutInflater;
        private int selectedItemPosition;

        private SpinnerAdapter(Context context, List<String> difficultyLevels) {
            super(context, 0, difficultyLevels);

            layoutInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View cell, @NonNull ViewGroup parent) {
            if (cell == null)
                cell = layoutInflater.inflate(R.layout.difficulty_spinner_cell, parent, false);

            String level = getItem(position);
            if (level != null)
                ((TextView) cell).setText(level);

            return cell;
        }

        @Override
        public View getDropDownView(int position, @Nullable View cell, @NonNull ViewGroup parent) {
            if (cell == null)
                cell = layoutInflater.inflate(R.layout.difficulty_spinner_dropdown, parent, false);

            String level = getItem(position);
            if (level != null)
                ((TextView) cell).setText(level);

            cell.setBackgroundColor(selectedItemPosition == position ? Color.LTGRAY : 0);

            return cell;

        }
    }

    // Fragments require a no-arg constructor
    public SettingsDialog() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settingsfragment, container);

        Activity activity = getActivity();
        sharedPreferences = activity.getSharedPreferences(PuzzleActivity.NAME, MODE_PRIVATE);

        // Sound Settings
        soundEnabled = sharedPreferences.getBoolean(PuzzleActivity.NAME_SOUND_ENABLED, false);

        // ScrollView contains RelativeLayout contains required Views
        soundSwitch = (Switch) ((ScrollView) view).getChildAt(0).findViewById(R.id.soundSwitch);
        soundSwitch.setChecked(soundEnabled);
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    soundEnabled = true;
                else
                    soundEnabled = false;

                sharedPreferences.edit().putBoolean(PuzzleActivity.NAME_SOUND_ENABLED, soundEnabled).apply();
            }
        });

        // TODO: Volume Settings


        // Difficulty Settings
        difficultyLevels.add(getString(R.string.difficultyText1));
        difficultyLevels.add(getString(R.string.difficultyText2));
        difficultyLevels.add(getString(R.string.difficultyText3));

        spinnerAdapter = new SpinnerAdapter(activity, difficultyLevels);

        difficulty = sharedPreferences.getInt(PuzzleActivity.NAME_DIFFICULTY, PuzzleActivity.DIFFICULTY1);
        spinnerAdapter.selectedItemPosition = difficulty - 1;

        spinner = (Spinner) ((ScrollView) view).getChildAt(0).findViewById(R.id.difficultySpinner);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(difficulty - 1, false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String level = spinnerAdapter.getItem(position);
                spinnerAdapter.selectedItemPosition = position;
                difficulty = position + 1;  // position is base 0 but difficulty levels are base 1
                sharedPreferences.edit().putInt(PuzzleActivity.NAME_DIFFICULTY, difficulty).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing to do here
            }
        });

        return view;
    }
}
