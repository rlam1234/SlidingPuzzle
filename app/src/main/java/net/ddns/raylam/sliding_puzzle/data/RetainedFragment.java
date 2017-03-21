package net.ddns.raylam.sliding_puzzle.data;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import static net.ddns.raylam.sliding_puzzle.PuzzleActivity.MAX_COLS;
import static net.ddns.raylam.sliding_puzzle.PuzzleActivity.MAX_ROWS;

/*
 *  This class is used to save instance state data for configuration changes; it is not a UI element.
 */
public class RetainedFragment extends Fragment {
    public static final String NAME = RetainedFragment.class.getSimpleName();
    public int[] savedIds = new int[MAX_ROWS * MAX_COLS];

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }
}
