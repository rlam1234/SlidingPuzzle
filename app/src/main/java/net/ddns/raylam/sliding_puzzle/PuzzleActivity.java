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

import net.ddns.raylam.sliding_puzzle.data.Coordinate;
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
    private static final String NAME_ROW = "row";
    private static final String NAME_COLUMN = "column";
	private static final String NAME_SOLVED_STATE = "solvedState";

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

	// Animation duration is 125 ms
	private static final int MOVE_TIME = 125;

    private int moves = 0;				// Number of moves taken so far
    private TimerTask timer;
    private int solveTime = -1;			// Time taken to solve the puzzle (in seconds)
    private TextView movesView;
    private TextView timeView;

    /*
     * tiles[][] represents the puzzle board with tiles[0][0] being the upper left square and tiles[2][2] the lower right square.
     * A Tile is an ImageView which contains the picture of that tile and its associated id number, which identifies that Drawable
     * so it can be easily compared.
     */
    public Tile[][] tiles = new Tile[MAX_ROWS][MAX_COLS];

    // emptyTileRow and emptyTileColumn keep track of the indices of the empty tile.
    private int emptyTileRow = 2;
    private int emptyTileColumn = 2;

	/*
	 * solvedState[][] contains the tile id for the corresponding tile in tiles[][] when tiles[][] is in the solved state.
	 */
	int[][] solvedState = new int[MAX_ROWS][MAX_COLS];

	/*
	 * The x-y co-ordinates for each tile's ImageView. E.g. viewCoordinates[0][0] will have the x-y co-ordinates for the tile
	 * in row 0, column 0.
	 */
	private Coordinate[][] viewCoordinates = new Coordinate[MAX_ROWS][MAX_COLS];

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

	private final class FinishAnimationRunnable implements Runnable {
		private int tileRow;
		private int tileColumn;

		FinishAnimationRunnable(int tileRow, int tileColumn) {
			this.tileRow = tileRow;
			this.tileColumn = tileColumn;
		}

		@Override
		public void run() {
			// update tiles[][] to reflect the up to date state of the puzzle
			Tile emptyTile = tiles[emptyTileRow][emptyTileColumn];
			tiles[emptyTileRow][emptyTileColumn] = tiles[tileRow][tileColumn];
            tiles[emptyTileRow][emptyTileColumn].row = emptyTileRow;
            tiles[emptyTileRow][emptyTileColumn].column = emptyTileColumn;

			tiles[tileRow][tileColumn] = emptyTile;
            tiles[tileRow][tileColumn].row = tileRow;
            tiles[tileRow][tileColumn].column = tileColumn;

			emptyTileRow = tileRow;
			emptyTileColumn = tileColumn;

			moves++;
			movesView.setText(getString(R.string.moves) + ": " + Integer.toString(moves));

			Log.d(NAME, "emptyTileRow = " + emptyTileRow + ", emptyTileColumn = " + emptyTileColumn);
			Log.d(NAME, "tiles[][] is now:\n" + tilesToString());

			if (isSolved()) {
				puzzleSolved();
			}
		}
	}	// end class FinishAnimationRunnable

	/*
     * This OnClickListener handles the actions associated with tapping on a tile (swapping it with the empty tile,
     * and updating the various status boxes).
     */
    private final OnClickListener tileOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // If the puzzle has been solved already, don't allow the user to move the tiles around anymore
            if (timer == null) {
                Toast.makeText(PuzzleActivity.this, R.string.errorNewPuzzle, Toast.LENGTH_SHORT).show();

                return;
            }

            // Find which tile has been tapped
            int tileRow = -1;
            int tileColumn = -1;
            for (int row = 0; row < MAX_ROWS; row++) {
				for (int column = 0; column < MAX_COLS; column++) {
					if (v.equals(tiles[row][column].imageView)) {
						tileRow = row;
						tileColumn = column;
					}
				}
			}

			Log.d(NAME, "Clicked on tileRow = " + tileRow + ", tileColumn = " + tileColumn);

			Runnable finishAnimation = new FinishAnimationRunnable(tileRow, tileColumn);
			ImageView tileView = (ImageView)v; // =tiles[tileRow][tileColumn].imageView;

			// Can we slide the tapped tile into the empty space?
            if (tileRow == emptyTileRow - 1 && tileColumn == emptyTileColumn) {
				tileView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
				tileView.animate()
					.translationYBy(tileView.getHeight())
					.setDuration(MOVE_TIME)
					.withLayer()
					.withEndAction(finishAnimation);
				tileView.setLayerType(View.LAYER_TYPE_NONE, null);
			} else if (tileRow == emptyTileRow + 1 && tileColumn == emptyTileColumn) {
				tileView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
				tileView.animate()
					.translationYBy(-tileView.getHeight())
					.setDuration(MOVE_TIME)
					.withLayer()
					.withEndAction(finishAnimation);
				tileView.setLayerType(View.LAYER_TYPE_NONE, null);
			} else if (tileRow == emptyTileRow && tileColumn == emptyTileColumn - 1) {
				tileView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
				tileView.animate()
					.translationXBy(tileView.getWidth())
					.setDuration(MOVE_TIME)
					.withLayer()
					.withEndAction(finishAnimation);
				tileView.setLayerType(View.LAYER_TYPE_NONE, null);
			} else if (tileRow == emptyTileRow && tileColumn == emptyTileColumn + 1) {
				tileView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
				tileView.animate()
					.translationXBy(-tileView.getWidth())
					.setDuration(MOVE_TIME)
					.withLayer()
					.withEndAction(finishAnimation);
				tileView.setLayerType(View.LAYER_TYPE_NONE, null);
            } else {    // We can't move the selected tile; tell the user why not
				Toast.makeText(PuzzleActivity.this, getString(R.string.errorCannotMove), Toast.LENGTH_SHORT).show();
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
        }

        @Override
        protected void onProgressUpdate(final Integer... time) {
            if (puzzleActivityWeakReference.get() != null) {
				puzzleActivityWeakReference.get()
					.timeView
					.setText(getString(R.string.time) + ": " + intToHHMMSS(elapsedTime));
			}
        }

        @Override
        protected Integer doInBackground(Void... stuff) {
            while(true) {
                SystemClock.sleep(1000);
				if (isCancelled()) {
					break;
				} else {
					elapsedTime++;
					publishProgress(elapsedTime);
				}
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

            if (solveTime > -1) {
				timeView.setText(getString(R.string.time) + ": " + intToHHMMSS(solveTime));
			} else {
                int elapsedTime = savedInstanceState.getInt(NAME_ELAPSED_TIME);

                if (elapsedTime > 0) {
                    timer = new TimerTask(this);
                    timer.elapsedTime = elapsedTime;
                    timer.execute();
                }
                timeView.setText(getString(R.string.time) + ": " + intToHHMMSS(elapsedTime));
            }

			// Retrieve the saved state for tiles[][] and solvedState[]
			for (int row = 0; row < MAX_ROWS; row++) {
				for (int column = 0; column < MAX_COLS; column++) {
					int index = row * MAX_COLS + column;
					int tileID = savedInstanceState.getInt(NAME_ID + index);
					tiles[row][column] = new Tile(  tileID,
                                                    (ImageView) findViewById(tileID),
                                                    savedInstanceState.getInt(NAME_ROW + index),
                                                    savedInstanceState.getInt(NAME_COLUMN + index));
					solvedState[row][column] = savedInstanceState.getInt(NAME_SOLVED_STATE + index);
				}
			}

            initializeTiles();

			Log.d(NAME, "at end of onCreate; tiles = \n" + tilesToString());
			Log.d(NAME, "at end of onCreate; solvedState = \n" + solvedStateToString());
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

		// Save the current state of tiles[][] and solvedState[]
		for (int row = 0; row < MAX_ROWS; row++) {
			for (int column = 0; column < MAX_COLS; column++) {
				int index = row * MAX_COLS + column;
				outState.putInt(NAME_ID + index, tiles[row][column].id);
                outState.putInt(NAME_ROW + index, tiles[row][column].row);
                outState.putInt(NAME_COLUMN + index, tiles[row][column].column);
				outState.putInt(NAME_SOLVED_STATE + index, solvedState[row][column]);
			}
		}

		Log.d(NAME, "onSaveInstanceState, tiles =\n" + tilesToString());
		Log.d(NAME, "onSaveInstanceState, solvedState =\n" + solvedStateToString());

        super.onSaveInstanceState(outState);
    }	// end onSaveInstanceState

	@Override
	protected void onDestroy() {
		super.onDestroy();

		stopTimer();

        for (int row = 0; row < MAX_ROWS; row++) {
            for (int column = 0; column < MAX_COLS; column++) {
                tiles[row][column].imageView.setOnClickListener(null);
            }
        }

	}	// end onDestroy

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);

        actionBarOverflow.createMenuItems(menu);

        return true;
    }	// end onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        return actionBarOverflow.optionsItemSelected(item);
    }

    // Converts the JSON strings to the ArrayLists that they represent
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
    }	// end retrieveGameHistory

    /*
     * Randomize the puzzle board by moving the blank tile around (using valid movements); this will
     * ensure that the resulting board is solvable vs just randomly placing all the tiles on the puzzle.
     */
    public void randomizeTiles() {
        // Maximum number of times to randomly move the empty tile around; the more times it's moved,
        // the harder the puzzle will be to solve.  These numbers should be odd to avoid the small
        // chance that the randomized puzzle will actually be in the solved state.
        final int MAXIMUM_MOVES1 = 5;       // maximum number of times for an easy puzzle (this is very easy for testing/demoing)
        final int MAXIMUM_MOVES2 = 25;      // maximum number of times for a medium puzzle
        final int MAXIMUM_MOVES3 = 45;      // maximum number of times for a hard puzzle

        int counter = 0;                  // number of successful moves of the empty tile
        int previousDirection = -1;
		solveTime = -1;

		// Reset the puzzle board before randomizing it so we have an accurate measure of the difficulty level since
		// this method can be invoked when the puzzle is in an unsolved state.
        initialize();

        difficulty = getSharedPreferences(NAME, MODE_PRIVATE).getInt(NAME_DIFFICULTY, DIFFICULTY1);
        int maximumMoves;      // the number of times to move the empty tile before we consider the puzzle to be randomized
        if (difficulty == DIFFICULTY1) {
			maximumMoves = MAXIMUM_MOVES1;
		} else if (difficulty == DIFFICULTY2) {
			maximumMoves = MAXIMUM_MOVES2;
		} else {
			maximumMoves = MAXIMUM_MOVES3;
		}

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
                        swapTiles(emptyTileRow - 1,emptyTileColumn);
                        counter++;
                    }
                    break;
                case DOWN:
                    if (emptyTileRow != MAX_ROWS - 1) {     // can't go down if we're at the last row
                        swapTiles(emptyTileRow + 1, emptyTileColumn);
                        counter++;
                    }
                    break;
                case LEFT:
                    if (emptyTileColumn != 0) {             // can't go left if we're at column 0
                        swapTiles(emptyTileRow, emptyTileColumn - 1);
                        counter++;
                    }
                    break;
                default: case RIGHT:
                    if (emptyTileColumn != MAX_COLS - 1) {  // can't go right if we're at the last column
                        swapTiles(emptyTileRow, emptyTileColumn + 1);
                        counter++;
                    }
            }   // end switch
		// Continue looping until the desired number of moves has been reached; if the puzzle is somehow solved at the end
		// of this, move the tile again so that it's not solved
        } while (counter < maximumMoves || isSolved());

		Log.d(NAME, "emptyTileRow = " + emptyTileRow + ", emptyTileColumn = " + emptyTileColumn);
		Log.d(NAME, "randomized tiles[][]:\n" + tilesToString());
		Log.d(NAME, "randomized solvedState[][]:\n" + solvedStateToString());

//		setTileBackground();

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
    }	// end oppositeDirection

    private int randomDirection() {
        return RANDOM_GENERATOR.nextInt(4);
    }

    /*
     * The initialized state is such that tiles[2][2] is the blank tile and the other elements
     * of the array are in order going from left to right, top to bottom.
     */
    public void initialize() {
        // Reset the statistical counters
        moves = 0;
        movesView.setText(getString(R.string.moves) + ": " + Integer.toString(moves));
        timeView.setText(getString(R.string.time) + ": " + intToHHMMSS(0));
        emptyTileRow = 2;
        emptyTileColumn = 2;

        // Assign the tiles to the puzzle board; initially, this will be in the solved position.
        tiles[0][0] = new Tile(R.id.tile00, (ImageView) findViewById(R.id.tile00), 0, 0);
        tiles[0][1] = new Tile(R.id.tile01, (ImageView) findViewById(R.id.tile01), 0, 1);
        tiles[0][2] = new Tile(R.id.tile02, (ImageView) findViewById(R.id.tile02), 0, 2);
        tiles[1][0] = new Tile(R.id.tile10, (ImageView) findViewById(R.id.tile10), 1, 0);
        tiles[1][1] = new Tile(R.id.tile11, (ImageView) findViewById(R.id.tile11), 1,1);
        tiles[1][2] = new Tile(R.id.tile12, (ImageView) findViewById(R.id.tile12), 1, 2);
        tiles[2][0] = new Tile(R.id.tile20, (ImageView) findViewById(R.id.tile20),2, 0);
        tiles[2][1] = new Tile(R.id.tile21, (ImageView) findViewById(R.id.tile21), 2, 1);
        tiles[2][2] = new Tile(R.id.tile22, (ImageView) findViewById(R.id.tile22), 2, 2);

		// Remember the solved state
		for (int row = 0; row < MAX_ROWS; row++) {
			for (int column = 0; column < MAX_COLS; column++) {
				solvedState[row][column] = tiles[row][column].id;
			}
		}

		Log.d(NAME, "in initialize, tiles =\n" + tilesToString());
		Log.d(NAME, "in initialize, solvedState =\n" + solvedStateToString());

        initializeTiles();
	}   // end initialize

	// Set the tile's images and OnClickListeners
    private void initializeTiles() {
        // Populate viewCoordinates
        for (int row = 0; row < MAX_ROWS; row++) {
            for (int column = 0; column < MAX_COLS; column++) {
                viewCoordinates[row][column] = new Coordinate(tiles[row][column].imageView.getX(), tiles[row][column].imageView.getY());
            }
        }

        // Set the tiles' OnClickListeners and correct ImageView positions
        for (int row = 0; row < MAX_ROWS; row++) {
			for (int column = 0; column < MAX_COLS; column++) {
				if (row == emptyTileRow && column == emptyTileColumn) {
					// Don't set an OnClickListener on the emptyTile
				} else {
					tiles[row][column].imageView.setOnClickListener(tileOnClickListener);
				}

				int tileRow = tiles[row][column].row;
				int tileColumn = tiles[row][column].column;
				tiles[row][column].imageView.setX(viewCoordinates[tileRow][tileColumn].x);
                tiles[row][column].imageView.setY(viewCoordinates[tileRow][tileColumn].y);
			}
		}

//        setTileBackground();
		// Make sure the empty tile is invisible
		tiles[emptyTileRow][emptyTileColumn].imageView.setVisibility(View.INVISIBLE);
    }


    // Sets the borders for all the non-empty tiles; the empty tile is invisible
//    private void setTileBackground() {
//        for (int row = 0; row < MAX_ROWS; row++) {
//			for (int column = 0; column < MAX_COLS; column++) {
//				if (row == emptyTileRow && column == emptyTileColumn) {
//					tiles[row][column].imageView.setVisibility(View.INVISIBLE);
//				}
//				else {
//					tiles[row][column].imageView.setBackground(getDrawable(R.drawable.customborder));
//				}
//			}
//		}
//    }

    private boolean isSolved() {
        boolean isSolved = true;

        for (int row = 0; row < MAX_ROWS; row++) {
            for (int column = 0; column < MAX_COLS; column++) {
				//                isSolved = isSolved && (tiles[row][column].id == row * MAX_COLS + column);
				isSolved = isSolved && (tiles[row][column].id == solvedState[row][column]);
			}
        }
        return isSolved;
    }

    // tiles[][] element to swap with the empty Tile
	// Assumption: setting a duration of 0 means the system won't get the chance to access the ImageView before it has completed
	// its animation sequence
    private void swapTiles(int row, int column) {
		Tile tile = tiles[row][column];
		tile.row = emptyTileRow;
		tile.column = emptyTileColumn;
		tile.imageView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

		if (row == emptyTileRow - 1) {					// swap the empty tile with the tile above it
			tile.imageView.animate()
				.translationYBy(tile.imageView.getHeight())
				.withLayer()
				.setDuration(0)
				.start();

			tiles[row][column] = tiles[emptyTileRow][emptyTileColumn];
            tiles[row][column].row = row;
            tiles[row][column].column = column;
			tiles[emptyTileRow--][emptyTileColumn] = tile;
		} else if (row == emptyTileRow + 1) {			// swap the empty tile with the tile below it
			tile.imageView.animate()
				.translationYBy(-tile.imageView.getHeight())
				.withLayer()
				.setDuration(0)
				.start();

			tiles[row][column] = tiles[emptyTileRow][emptyTileColumn];
			tiles[emptyTileRow++][emptyTileColumn] = tile;
		} else if (column == emptyTileColumn - 1) {		// swap the empty tile with the tile to its left
			tile.imageView.animate()
				.translationXBy(tile.imageView.getWidth())
				.withLayer()
				.setDuration(0)
				.start();

			tiles[row][column] = tiles[emptyTileRow][emptyTileColumn];
			tiles[emptyTileRow][emptyTileColumn--] = tile;
		} else if (column == emptyTileColumn + 1) {		// swap the empty tile with the tile to its right
			tile.imageView.animate()
				.translationXBy(-tile.imageView.getWidth())
				.withLayer()
				.setDuration(0)
				.start();

			tiles[row][column] = tiles[emptyTileRow][emptyTileColumn];
			tiles[emptyTileRow][emptyTileColumn++] = tile;
		}

		tile.imageView.setLayerType(View.LAYER_TYPE_NONE, null);
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

            timeView.setText(getString(R.string.time) + ": " + intToHHMMSS(solveTime));

            // Save the game play history
            onHistoryChanged(difficulty, new SolveHistory(new Date(), solveTime, moves, difficulty));

			startActivity(new Intent(PuzzleActivity.this, SolvedActivity.class));
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
                    .apply();						// asynchronously apply changes to SharedPreferences
        } else if (difficulty == DIFFICULTY2) {
            mediumHistory.add(0, solveHistory);
            getSharedPreferences(NAME, MODE_PRIVATE)
                    .edit()
                    .putString(NAME_GAME_HISTORY2, historyGson.toJson(mediumHistory, historyType))
					.apply();						// asynchronously apply changes to SharedPreferences
        } else if (difficulty == DIFFICULTY3) {
            hardHistory.add(0, solveHistory);
            getSharedPreferences(NAME, MODE_PRIVATE)
                    .edit()
                    .putString(NAME_GAME_HISTORY3, historyGson.toJson(hardHistory, historyType))
					.apply();						// asynchronously apply changes to SharedPreferences
        }
    }

    public void stopTimer() {
		if (timer != null) {
			timer.cancel(true);
			timer = null;
		}
	}

	/*
	 * Returns the ids and images of the tiles of the puzzle board; used for debugging.
	 */
	private String tilesToString() {
		String tilesToString = "";

		for (int row = 0; row < MAX_ROWS; row++) {
			for (int column = 0; column < MAX_COLS; column++) {
				tilesToString += "[" + tiles[row][column].id
					+ String.format("(%7.2f, %7.2f)", tiles[row][column].imageView.getX(), tiles[row][column].imageView.getY())
					+ "] ";
			}
			tilesToString += "\n";
		}

		return tilesToString;
	}

	/*
	 * Returns the ids of the solved tile state; used for debugging.
	 */
	private String solvedStateToString() {
		String solvedStateToString = "";

		for (int row = 0; row < MAX_ROWS; row++) {
			for (int column = 0; column < MAX_COLS; column++) {
				solvedStateToString += "[" + tiles[row][column].id
					+ String.format("(%7.2f, %7.2f)", tiles[row][column].imageView.getX(), tiles[row][column].imageView.getY())
					+ "] ";
			}
			solvedStateToString += "\n";
		}

		return solvedStateToString;
	}
}
