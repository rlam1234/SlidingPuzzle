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
import android.net.Uri;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import java.io.IOException;

public class AppActivity extends AppCompatActivity {
    public static final String NAME = AppActivity.class.getSimpleName();
    public static final String NAME_POSITION = "position";

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appactivity);

        findViewById(R.id.attributionText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.attributionUrl))));
            }
        });

        // If the user presses the hard volumne up/down buttons, affect the Multimedia stream, not the ringer.
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mediaPlayer = MediaPlayer.create(this, R.raw.tada);

        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(final MediaPlayer mp) {
                doneWithMediaPlayer();

                startActivity(new Intent(AppActivity.this, PuzzleActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        if (savedInstanceState != null)
            mediaPlayer.seekTo(savedInstanceState.getInt(NAME_POSITION));

		mediaPlayer.start();
    }   // end onCreate

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null)
            doneWithMediaPlayer();

        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mediaPlayer != null)
            outState.putInt(NAME_POSITION, mediaPlayer.getCurrentPosition());

        super.onSaveInstanceState(outState);
    }

    private void doneWithMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}
