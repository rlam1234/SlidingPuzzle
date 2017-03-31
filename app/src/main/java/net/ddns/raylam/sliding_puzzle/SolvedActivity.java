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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class SolvedActivity extends AppCompatActivity {
    public static final String NAME = SolvedActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.solvedactivity);


        // If the user presses the hard volumne up/down buttons, affect the Multimedia stream, not the ringer.
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.yay);

        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(final MediaPlayer mp) {
                Log.w(NAME, "about to reset mediaPlayer");
                mediaPlayer.reset();

                startActivity(new Intent(SolvedActivity.this, HistoryActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        Log.w(NAME, "about to start mediaPlayer");
        mediaPlayer.start();
    }   // end onCreate

}
