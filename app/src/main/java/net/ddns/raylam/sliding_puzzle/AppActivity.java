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
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AppActivity extends Activity {
    public static final String NAME = AppActivity.class.getSimpleName();

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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(AppActivity.this, PuzzleActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 1500);
    }   // end onCreate

}
