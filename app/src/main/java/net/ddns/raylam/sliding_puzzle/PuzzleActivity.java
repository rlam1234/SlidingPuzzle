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
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.ddns.raylam.sliding_puzzle.data.SolveHistory;
import net.ddns.raylam.sliding_puzzle.data.Tile;
import net.ddns.raylam.sliding_puzzle.ui.overflow.ActionBarOverflow;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class PuzzleActivity extends AppCompatActivity {
    // Name of this Activity; used for logging/debugging purposes
    public static final String NAME = PuzzleActivity.class.getSimpleName();

    // Tags for data saved/retrieved from saveInstanceState/onCreate
    private static final String NAME_EMPTY_ROW = "emptyTileRow";
    private static final String NAME_EMPTY_COLUMN = "emptyTileColumn";
    private static final String NAME_MOVES = "moves";
    private static final String NAME_ELAPSED_TIME = "elapsedTime";
    private static final String NAME_SOLVE_TIME = "solveTime";
    private static final String NAME_ID = "id";

    public static final String NAME_SOUND_ENABLED = "soundEnabled";

    // Bundle difficulty name when passing to DifficultyDialog also used for SharedPreferences
    public static final String NAME_DIFFICULTY = "difficulty";

    public static final String NAME_GAME_HISTORY1 = "gameEasyHistory";
    public static final String NAME_GAME_HISTORY2 = "gameMediumHistory";
    public static final String NAME_GAME_HISTORY3 = "gameHardHistory";

    // The random number generator used to mix up the puzzle
    private static final Random RANDOM_GENERATOR = new Random(System.currentTimeMillis());

    // Constants linking numbers to puzzle directions (used to randomly mix up the puzzle)
    private static final int UP = 0;
    private static final int DOWN = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;

    public static final int MAX_ROWS = 3;
    public static final int MAX_COLS = 3;

    private int moves = 0;				// Number of moves taken so far
    private TimerTask timer;
    private int solveTime = -1;			// Time taken to solve the puzzle (in seconds)
    private TextView movesView;
    private TextView timeView;

    /*
     * tiles[][] represents the puzzle board with tiles[0][0] being the upper left square and tiles[2][2] the lower right square.
     * A Tile is an ImageView which contains the picture of that tile and its associated id number, which identifies that tile
     * so it can be easily compared.
     */
    private Tile[][] tiles = new Tile[MAX_ROWS][MAX_COLS];

    // emptyTileRow and emptyTileColumn keep track of the indices of the empty tile.
    private int emptyTileRow = 2;
    private int emptyTileColumn = 2;

    // Stores the images of the various puzzle pieces; puzzlePieces[i] holds the image of the puzzle piece with id i.
    private Drawable[] puzzlePieces = new Drawable[MAX_ROWS * MAX_COLS];

    private ActionBarOverflow actionBarOverflow;

    // Game difficulty levels
    public static final int DIFFICULTY1 = 1;       // Easy
    public static final int DIFFICULTY2 = 2;       // Medium
    public static final int DIFFICULTY3 = 3;       // Hard

    // Current game difficulty level
    private int difficulty = DIFFICULTY1;

    private List<SolveHistory> easyHistory = new ArrayList<>();
    private List<SolveHistory> mediumHistory = new ArrayList<>();
    private List<SolveHistory> hardHistory = new ArrayList<>();

    /*
     * This OnClickListener handles the actions associated with tapping on a tile (switching it with the empty tile,
     * and updating the various status boxes).
     */
    private final OnClickListener tileOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // If the puzzle's been solved already, don't allow the user to move the tiles around
            if (solveTime > -1)
                return;

            int tileRow = -1;
            int tileColumn = -1;
            for (int row = 0; row < MAX_ROWS; row++)
                for (int column = 0; column < MAX_COLS; column++)
                    if (v.equals(tiles[row][column].imageView)) {
                        tileRow = row;
                        tileColumn = column;
                    }

            // Can we slide the tapped tile into the empty space?
            if ((tileRow == emptyTileRow - 1 && tileColumn == emptyTileColumn)
                    || (tileRow == emptyTileRow + 1 && tileColumn == emptyTileColumn)
                    || (tileRow == emptyTileRow && tileColumn == emptyTileColumn - 1)
                    || (tileRow == emptyTileRow && tileColumn == emptyTileColumn + 1)) {
                tiles[emptyTileRow][emptyTileColumn].imageView.setImageDrawable(tiles[tileRow][tileColumn].imageView.getDrawable());
                tiles[emptyTileRow][emptyTileColumn].imageView.setBackground(getDrawable(R.drawable.customborder));
                int tmpId = tiles[emptyTileRow][emptyTileColumn].id;
                tiles[emptyTileRow][emptyTileColumn].id = tiles[tileRow][tileColumn].id;

                tiles[tileRow][tileColumn].imageView.setImageDrawable(getDrawable(R.drawable.blank));
                tiles[tileRow][tileColumn].imageView.setBackground(null);
                tiles[tileRow][tileColumn].id = tmpId;

                emptyTileRow = tileRow;
                emptyTileColumn = tileColumn;

                moves++;
                movesView.setText(getString(R.string.moves) + ": " + Integer.toString(moves));

                if (isSolved())
                    puzzleSolved();
            } else {    // We can't move the selected tile; tell the user why not
                if (tileRow == emptyTileRow && tileColumn == emptyTileColumn) {
                    Toast.makeText(getBaseContext(), getString(R.string.errorCannotMoveEmpty), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.errorCannotMove), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };  // end instantiate tileOnClickListener

    /*
     * TimerTask updates the elapsed time clock.
     */
    private class TimerTask extends AsyncTask<Void, Integer, Integer> {
        @NonNull
        private Integer elapsedTime = 0;

        @NonNull
        private final WeakReference<PuzzleActivity> puzzleActivityWeakReference;

        private TimerTask(@NonNull PuzzleActivity puzzleActivity) {
            puzzleActivityWeakReference = new WeakReference<>(puzzleActivity);

            Log.w(NAME, "instantiating TimerTask(" + puzzleActivity + ")");
        }

        @Override
        protected void onProgressUpdate(final Integer... time) {
			timeView.setText(getString(R.string.time) + ": " + intToHHMMSS(elapsedTime));
            if (puzzleActivityWeakReference.get() != null)
			    puzzleActivityWeakReference.get()
                        .timeView
                        .setText(getString(R.string.time) + ": " + intToHHMMSS(elapsedTime));
        }

        @Override
        protected Integer doInBackground(Void... stuff) {
            while(!isCancelled()) {
                SystemClock.sleep(1000);
                elapsedTime++;
                publishProgress(elapsedTime);
            }

            return elapsedTime;
        }
    }   // end class TimerTask

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puzzleactivity);
        actionBarOverflow = new ActionBarOverflow(this);

		findViewById(R.id.newPuzzle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomizeTiles();
            }
        });

        movesView = (TextView) findViewById(R.id.moves);
        timeView = (TextView) findViewById(R.id.elapsedTime);

        // Retrieve the difficulty level
        SharedPreferences sharedPreferences = getSharedPreferences(NAME, MODE_PRIVATE);
		difficulty = sharedPreferences.getInt(NAME_DIFFICULTY, DIFFICULTY1);

        retrieveGameHistory(
                    sharedPreferences.getString(NAME_GAME_HISTORY1, "[]"),
                    sharedPreferences.getString(NAME_GAME_HISTORY2, "[]"),
                    sharedPreferences.getString(NAME_GAME_HISTORY3, "[]"));

        if (savedInstanceState == null) {
			initialize();
            timeView.setText(getString(R.string.time) + ": " + intToHHMMSS(0));
        } else {
            emptyTileRow = savedInstanceState.getInt(NAME_EMPTY_ROW);
            emptyTileColumn = savedInstanceState.getInt(NAME_EMPTY_COLUMN);
            moves = savedInstanceState.getInt(NAME_MOVES);
            movesView.setText(getString(R.string.moves) + ": " + Integer.toString(moves));
            solveTime = savedInstanceState.getInt(NAME_SOLVE_TIME);

            if (solveTime > 0) {
				timeView.setText(getString(R.string.time) + ": " + intToHHMMSS(solveTime));
			}
            else {
                int elapsedTime = savedInstanceState.getInt(NAME_ELAPSED_TIME);

                Log.w(NAME, "elapsedTime = " + elapsedTime);

                if (elapsedTime > 0) {
                    timer = new TimerTask(this);
                    timer.elapsedTime = elapsedTime;
                    timer.execute();
                }
                timeView.setText(getString(R.string.time) + ": " + intToHHMMSS(elapsedTime));
            }

            tiles[0][0] = new Tile(savedInstanceState.getInt(NAME_ID + "0"), (ImageView) findViewById(R.id.tile00));
            tiles[0][1] = new Tile(savedInstanceState.getInt(NAME_ID + "1"), (ImageView) findViewById(R.id.tile01));
            tiles[0][2] = new Tile(savedInstanceState.getInt(NAME_ID + "2"), (ImageView) findViewById(R.id.tile02));
            tiles[1][0] = new Tile(savedInstanceState.getInt(NAME_ID + "3"), (ImageView) findViewById(R.id.tile10));
            tiles[1][1] = new Tile(savedInstanceState.getInt(NAME_ID + "4"), (ImageView) findViewById(R.id.tile11));
            tiles[1][2] = new Tile(savedInstanceState.getInt(NAME_ID + "5"), (ImageView) findViewById(R.id.tile12));
            tiles[2][0] = new Tile(savedInstanceState.getInt(NAME_ID + "6"), (ImageView) findViewById(R.id.tile20));
            tiles[2][1] = new Tile(savedInstanceState.getInt(NAME_ID + "7"), (ImageView) findViewById(R.id.tile21));
            tiles[2][2] = new Tile(savedInstanceState.getInt(NAME_ID + "8"), (ImageView) findViewById(R.id.tile22));

            initializeTiles();
        }
    }   // end onCreate

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(NAME_EMPTY_ROW, emptyTileRow);
        outState.putInt(NAME_EMPTY_COLUMN, emptyTileColumn);
        outState.putInt(NAME_MOVES, moves);
        outState.putInt(NAME_ELAPSED_TIME, timer == null ? 0 : timer.elapsedTime);
        outState.putInt(NAME_SOLVE_TIME, solveTime);

		stopTimer();

		for (int row = 0; row < MAX_ROWS; row++)
            for (int columns = 0; columns < MAX_COLS; columns++)
                 outState.putInt(NAME_ID + (row * MAX_COLS + columns), tiles[row][columns].id);

        super.onSaveInstanceState(outState);
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();

		stopTimer();
	}

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);

        actionBarOverflow.createMenuItems(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        Log.w(NAME, "entering onOptionsItemSelected(" + item + ")");

        return actionBarOverflow.optionsItemSelected(item);
    }

    private void retrieveGameHistory(final String easyJson, final String mediumJson, final String hardJson) {
        Gson historyGson = new Gson();
        Type historyType = new TypeToken<List<SolveHistory>>() {}.getType();

        if (easyJson == null || easyJson.equals("[]")) {
            easyHistory = new ArrayList<>();
        } else {
            easyHistory = historyGson.fromJson(easyJson, historyType);
        }

        if (mediumJson == null || mediumJson.equals("[]")) {
            mediumHistory = new ArrayList<>();
        } else {
            mediumHistory = historyGson.fromJson(mediumJson, historyType);
        }

        if (hardJson == null || hardJson.equals("[]")) {
            hardHistory = new ArrayList<>();
        } else {
            hardHistory = historyGson.fromJson(hardJson, historyType);
        }
    }

    /*
     * Randomize the puzzle board by moving the blank tile around (using valid movements); this will
     * ensure that the resulting board is solvable vs just randomly placing all the tiles on the puzzle.
     */
    public void randomizeTiles() {
        // Maximum number of times to randomly move the empty tile around; the more times it's moved,
        // the harder the puzzle will be to solve.  These numbers should be odd to avoid the small
        // chance that the randomized puzzle will actually be in the solved state.
        final int MAXIMUM_MOVES1 = 5;       // maximum number of times for an easy puzzle
        final int MAXIMUM_MOVES2 = 25;      // maximum number of times for a medium puzzle
        final int MAXIMUM_MOVES3 = 45;      // maximum number of times for a hard puzzle

        int counter = 0;                  // number of successful moves of the empty tile
        int previousDirection = -1;
		solveTime = -1;

        initialize();

        difficulty = getSharedPreferences(NAME, MODE_PRIVATE).getInt(NAME_DIFFICULTY, DIFFICULTY1);
        int maximumMoves;      // the number of times to move the empty tile before we consider the puzzle to be randomized
        if (difficulty == DIFFICULTY1)
            maximumMoves = MAXIMUM_MOVES1;
        else if (difficulty == DIFFICULTY2)
            maximumMoves = MAXIMUM_MOVES2;
        else
            maximumMoves = MAXIMUM_MOVES3;

        do {
            // Pick a random direction to move the empty tile;
            // if it's moving the tile back where just it came from, pick another direction
            int direction = randomDirection();
            if (direction == oppositeDirection(previousDirection))
                continue;
            else
                previousDirection = direction;

            // Move the empty tile to its new position
            switch (direction) {
                case UP:
                    if (emptyTileRow != 0) {                // can't go up if we're at row 0
                        swapTiles(tiles[emptyTileRow - 1][emptyTileColumn], tiles[emptyTileRow--][emptyTileColumn]);
                        counter++;
                    }
                    break;
                case DOWN:
                    if (emptyTileRow != MAX_ROWS - 1) {     // can't go down if we're at the last row
                        swapTiles(tiles[emptyTileRow + 1][emptyTileColumn], tiles[emptyTileRow++][emptyTileColumn]);
                        counter++;
                    }
                    break;
                case LEFT:
                    if (emptyTileColumn != 0) {             // can't go left if we're at column 0
                        swapTiles(tiles[emptyTileRow][emptyTileColumn - 1], tiles[emptyTileRow][emptyTileColumn--]);
                        counter++;
                    }
                    break;
                default: case RIGHT:
                    if (emptyTileColumn != MAX_COLS - 1) {  // can't go right if we're at the last column
                        swapTiles(tiles[emptyTileRow][emptyTileColumn + 1], tiles[emptyTileRow][emptyTileColumn++]);
                        counter++;
                    }
            }   // end switch
        } while (counter < maximumMoves || isSolved());

        setTileBackground();

		stopTimer();
        timer = new TimerTask(this);
        timer.execute();
    }   // end randomizeTiles

    private int oppositeDirection(int direction) {
        switch(direction) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            default: case RIGHT:
                return LEFT;
        }
    }

    private int randomDirection() {
        return RANDOM_GENERATOR.nextInt(4);
    }

    /*
     * The initialized state is such that tiles[2][2] is the blank tile and the other elements
     * of the array are in order going from left to right, top to bottom.
     */
    private void initialize() {
        // Reset the statistical counters
        moves = 0;
        movesView.setText(getString(R.string.moves) + ": " + Integer.toString(moves));
        timeView.setText(getString(R.string.time) + ": " + intToHHMMSS(0));
        emptyTileRow = 2;
        emptyTileColumn = 2;

        // Assign the tiles to the puzzle board; initially, this will be in the solved position.
        tiles[0][0] = new Tile(0, (ImageView) findViewById(R.id.tile00));
        tiles[0][1] = new Tile(1, (ImageView) findViewById(R.id.tile01));
        tiles[0][2] = new Tile(2, (ImageView) findViewById(R.id.tile02));
        tiles[1][0] = new Tile(3, (ImageView) findViewById(R.id.tile10));
        tiles[1][1] = new Tile(4, (ImageView) findViewById(R.id.tile11));
        tiles[1][2] = new Tile(5, (ImageView) findViewById(R.id.tile12));
        tiles[2][0] = new Tile(6, (ImageView) findViewById(R.id.tile20));
        tiles[2][1] = new Tile(7, (ImageView) findViewById(R.id.tile21));
        tiles[2][2] = new Tile(8, (ImageView) findViewById(R.id.tile22));

        initializeTiles();
	}

    private void initializeTiles() {
        // Associate the puzzle pieces with their images
        puzzlePieces[0] = getDrawable(R.drawable.android00);
        puzzlePieces[1] = getDrawable(R.drawable.android01);
        puzzlePieces[2] = getDrawable(R.drawable.android02);
        puzzlePieces[3] = getDrawable(R.drawable.android10);
        puzzlePieces[4] = getDrawable(R.drawable.android11);
        puzzlePieces[5] = getDrawable(R.drawable.android12);
        puzzlePieces[6] = getDrawable(R.drawable.android20);
        puzzlePieces[7] = getDrawable(R.drawable.android21);
        puzzlePieces[8] = getDrawable(R.drawable.blank);

        // Assign the images with the tiles, given their ids.
        tiles[0][0].imageView.setImageDrawable(puzzlePieces[tiles[0][0].id]);
        tiles[0][1].imageView.setImageDrawable(puzzlePieces[tiles[0][1].id]);
        tiles[0][2].imageView.setImageDrawable(puzzlePieces[tiles[0][2].id]);
        tiles[1][0].imageView.setImageDrawable(puzzlePieces[tiles[1][0].id]);
        tiles[1][1].imageView.setImageDrawable(puzzlePieces[tiles[1][1].id]);
        tiles[1][2].imageView.setImageDrawable(puzzlePieces[tiles[1][2].id]);
        tiles[2][0].imageView.setImageDrawable(puzzlePieces[tiles[2][0].id]);
        tiles[2][1].imageView.setImageDrawable(puzzlePieces[tiles[2][1].id]);
        tiles[2][2].imageView.setImageDrawable(puzzlePieces[tiles[2][2].id]);

        // Set the tiles' OnClickListeners
        for (int row = 0; row < MAX_ROWS; row++)
            for (int column = 0; column < MAX_COLS; column++) {
                tiles[row][column].imageView.setOnClickListener(tileOnClickListener);
            }
    }

    /*
     * Sets the borders for all the non-empty tiles; the empty tile has no border
     */
    private void setTileBackground() {
        for (int row = 0; row < MAX_ROWS; row++)
            for (int column = 0; column < MAX_COLS; column++)
                if (row == emptyTileRow && column == emptyTileColumn)
                    tiles[row][column].imageView.setBackground(null);
                else
                    tiles[row][column].imageView.setBackground(getDrawable(R.drawable.customborder));
    }

    private boolean isSolved() {
        boolean isSolved = true;

        for (int row = 0; row < MAX_ROWS; row++)
            for (int column = 0; column < MAX_COLS; column++)
                isSolved = isSolved && (tiles[row][column].id == row * MAX_COLS + column);

        return isSolved;
    }

    private void swapTiles(Tile a, Tile b) {
        int tmpId = a.id;
        Drawable tmpDrawable = a.imageView.getDrawable();

        a.imageView.setImageDrawable(b.imageView.getDrawable());
        a.id = b.id;

        b.imageView.setImageDrawable(tmpDrawable);
        b.id = tmpId;
    }

    /*
     * Returns the ids and images of the tiles of the puzzle board; used for debugging.
     */
    private String tilesToString() {
        String tilesToString = "";

        for (int row = 0; row < MAX_ROWS; row++) {
            for (int column = 0; column < MAX_COLS; column++) {
				tilesToString += "(" + tiles[row][column].id + ") ";
            }
            tilesToString += "\n";
        }

        return tilesToString;
    }

    public static String intToHHMMSS(int time) {
        if (time <= 0)
            return "00:00:00";

        int second = time % 60;

        int totalMinutes = time / 60;
        int minute = totalMinutes % 60;

        int totalHours = totalMinutes / 60;
        int hour = totalHours % 24;

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    private void puzzleSolved() {
		// timer can be null if the user solves the puzzle and continues to move the tiles around to solve it again
		// before the timer can be cancelled the first time around.
		if (timer != null) {
            solveTime = timer.elapsedTime;
			timer.cancel(true);
			timer = null;

            Log.w(NAME, "timer set to null");

            timeView.setText(getString(R.string.time) + ": " + intToHHMMSS(solveTime));

            // Save the game play history
            onHistoryChanged(difficulty, new SolveHistory(new Date(), solveTime, moves, difficulty));

            startActivity(new Intent(PuzzleActivity.this, SolvedActivity.class));
        }
	}

    public void onHistoryChanged(int difficulty, SolveHistory solveHistory) {
        Gson historyGson = new Gson();
        Type historyType = new TypeToken<List<SolveHistory>>() {}.getType();

        if (difficulty == DIFFICULTY1) {
            easyHistory.add(0, solveHistory);
            getSharedPreferences(NAME, MODE_PRIVATE)
                    .edit()
                    .putString(NAME_GAME_HISTORY1, historyGson.toJson(easyHistory, historyType))
                    .apply();
        } else if (difficulty == DIFFICULTY2) {
            mediumHistory.add(0, solveHistory);
            getSharedPreferences(NAME, MODE_PRIVATE)
                    .edit()
                    .putString(NAME_GAME_HISTORY2, historyGson.toJson(mediumHistory, historyType))
                    .apply();
        } else if (difficulty == DIFFICULTY3) {
            hardHistory.add(0, solveHistory);
            getSharedPreferences(NAME, MODE_PRIVATE)
                    .edit()
                    .putString(NAME_GAME_HISTORY3, historyGson.toJson(hardHistory, historyType))
                    .apply();
        }
    }

    private void stopTimer() {
		if (timer != null) {
			timer.cancel(true);
			timer = null;
			Log.w(NAME, "set timer to null");
		}
	}
}
