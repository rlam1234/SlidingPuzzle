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
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import net.ddns.raylam.sliding_puzzle.PuzzleActivity;
import net.ddns.raylam.sliding_puzzle.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class SettingsDialog extends DialogFragment {
    public static String NAME = SettingsDialog.class.getSimpleName();
    private Activity activity;
    private SharedPreferences sharedPreferences;

    // Sound Settings
    private Switch soundSwitch;
    private SeekBar volumeBar;
    private boolean soundEnabled;
    private AudioManager audioManager;
    private SettingsContentObserver settingsContentObserver;

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

            Log.w(NAME, "constructing SpinnerAdapter(" + context + ", " + difficultyLevels + ")");

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

    private class SettingsContentObserver extends ContentObserver {
        public SettingsContentObserver(Context context, Handler handler) {
            super(handler);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return false;
        }

        @Override
        public void onChange(boolean selfChange) {
            if (volumeBar != null)
                volumeBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }
    }

    // Fragments require a no-arg constructor
    public SettingsDialog() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settingsfragment, container);

        activity = getActivity();
        sharedPreferences = activity.getSharedPreferences(PuzzleActivity.NAME, MODE_PRIVATE);

        // Sound Settings
        soundEnabled = sharedPreferences.getBoolean(PuzzleActivity.NAME_SOUND_ENABLED, true);

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

        // Volume Settings

        // If the user presses the hard volume up/down buttons, affect the Multimedia stream, not the ringer.
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        audioManager = (AudioManager) activity.getSystemService(AUDIO_SERVICE);
        volumeBar = (SeekBar) ((ScrollView) view).getChildAt(0).findViewById(R.id.volume);
        volumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Listen for hard volume up/down button events and update volumeBar
        // Code from http://stackoverflow.com/questions/11318933/listen-to-volume-changes-events-on-android
        settingsContentObserver = new SettingsContentObserver(activity, new Handler());
        activity.getApplicationContext()
                .getContentResolver()
                .registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, settingsContentObserver);

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
                spinnerAdapter.selectedItemPosition = position;
                difficulty = position + 1;  // position is base 0 but difficulty levels are base 1
                sharedPreferences.edit().putInt(PuzzleActivity.NAME_DIFFICULTY, difficulty).apply();

				// If the user changes the difficulty level, randomize the puzzle.
				// This avoids the user cheating by starting at an easy level, changing
				// the spinner and completing the easy puzzle but getting credit for doing
				// a hard one.
                if (activity instanceof PuzzleActivity) {
                    ((PuzzleActivity) activity).stopTimer();
                    ((PuzzleActivity) activity).initialize();
                }
			}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing to do here
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        getActivity().getContentResolver().unregisterContentObserver(settingsContentObserver);

        super.onDestroyView();
    }
}
