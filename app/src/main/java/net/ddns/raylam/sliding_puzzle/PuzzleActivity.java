package net.ddns.raylam.sliding_puzzle;

import android.app.FragmentManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import net.ddns.raylam.sliding_puzzle.data.RetainedFragment;
import net.ddns.raylam.sliding_puzzle.data.Tile;

import java.util.Random;

public class PuzzleActivity extends AppCompatActivity {
    // Name of this Activity; used for logging/debugging purposes
    public static final String NAME = PuzzleActivity.class.getSimpleName();


    // Tags for data saved/retrieved from saveInstanceState/onCreate
    private static final String NAME_EMPTY_ROW = "emptyTileRow";
    private static final String NAME_EMPTY_COLUMN = "emptyTileColumn";
    private static final String NAME_MOVES = "moves";
    private static final String NAME_START_TIME = "startTime";
    private static final String NAME_ID = "id";
//    private RetainedFragment retainedFragment;


    // The random number generator used to mix up the puzzle
    private static final Random RANDOM_GENERATOR = new Random(System.currentTimeMillis());

    // Constants linking numbers to puzzle directions (used to randomly mix up the puzzle)
    private static final int UP = 0;
    private static final int DOWN = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;

    public static final int MAX_ROWS = 3;
    public static final int MAX_COLS = 3;

    private int moves = 0;                                  // Number of moves taken so far
    private long startTime = System.currentTimeMillis();    // The time since the puzzle was first displayed
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

    /*
     * This OnClickListener handles the actions associated with tapping on a tile (switching it with the empty tile,
     * and updating the various status boxes).
     */
    private final OnClickListener tileOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
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
                int tmpIndex = tiles[emptyTileRow][emptyTileColumn].id;
                tiles[emptyTileRow][emptyTileColumn].id = tiles[tileRow][tileColumn].id;

                tiles[tileRow][tileColumn].imageView.setImageDrawable(getDrawable(R.drawable.blank));
                tiles[tileRow][tileColumn].imageView.setBackground(null);
                tiles[tileRow][tileColumn].id = tmpIndex;

                emptyTileRow = tileRow;
                emptyTileColumn = tileColumn;

                moves++;
                movesView.setText(Integer.toString(moves));
                timeView.setText(longToHHMMSS(System.currentTimeMillis() - startTime));


                if (isSolved())
                    Toast.makeText(getBaseContext(), "Puzzle Sovled!", Toast.LENGTH_LONG).show();
            } else {    // We can't move the selected tile; tell the user why not
                if (tileRow == emptyTileRow && tileColumn == emptyTileColumn) {
                    Toast.makeText(getBaseContext(), getString(R.string.errorCannotMoveEmpty), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.errorCannotMove), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puzzleactivity);

        findViewById(R.id.newPuzzle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomizeTiles();
            }
        });

        movesView = (TextView) findViewById(R.id.moves);
        timeView = (TextView) findViewById(R.id.elapsedTime);

//        FragmentManager fragmentManager = getFragmentManager();
//        retainedFragment = (RetainedFragment) fragmentManager.findFragmentByTag(RetainedFragment.NAME);
//        if (retainedFragment == null) {
//            retainedFragment = new RetainedFragment();
//            fragmentManager.beginTransaction().add(retainedFragment, RetainedFragment.NAME).commit();
//        }

        if (savedInstanceState == null) {
            randomizeTiles();
        } else {
            emptyTileRow = savedInstanceState.getInt(NAME_EMPTY_ROW);
            emptyTileColumn = savedInstanceState.getInt(NAME_EMPTY_COLUMN);
            moves = savedInstanceState.getInt(NAME_MOVES);
            movesView.setText(Integer.toString(moves));
            startTime = savedInstanceState.getLong(NAME_START_TIME);
            timeView.setText(longToHHMMSS(System.currentTimeMillis() - startTime));

            // Assign the tiles to the puzzle board
//            tiles[0][0] = new Tile(retainedFragment.savedIds[0], (ImageView) findViewById(R.id.tile00));
//            tiles[0][1] = new Tile(retainedFragment.savedIds[1], (ImageView) findViewById(R.id.tile01));
//            tiles[0][2] = new Tile(retainedFragment.savedIds[2], (ImageView) findViewById(R.id.tile02));
//            tiles[1][0] = new Tile(retainedFragment.savedIds[3], (ImageView) findViewById(R.id.tile10));
//            tiles[1][1] = new Tile(retainedFragment.savedIds[4], (ImageView) findViewById(R.id.tile11));
//            tiles[1][2] = new Tile(retainedFragment.savedIds[5], (ImageView) findViewById(R.id.tile12));
//            tiles[2][0] = new Tile(retainedFragment.savedIds[6], (ImageView) findViewById(R.id.tile20));
//            tiles[2][1] = new Tile(retainedFragment.savedIds[7], (ImageView) findViewById(R.id.tile21));
//            tiles[2][2] = new Tile(retainedFragment.savedIds[8], (ImageView) findViewById(R.id.tile22));

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

            Log.w(NAME, "onCreate: restoreTiles:\n" + tilesToString());
        }
    }   // end onCreate

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(NAME_EMPTY_ROW, emptyTileRow);
        outState.putInt(NAME_EMPTY_COLUMN, emptyTileColumn);
        outState.putInt(NAME_MOVES, moves);
        outState.putLong(NAME_START_TIME, startTime);

        Log.w(NAME, "onSaveInstanceState:\n" + tilesToString());

        for (int row = 0; row < MAX_ROWS; row++)
            for (int columns = 0; columns < MAX_COLS; columns++)
//                retainedFragment.savedIds[row * MAX_COLS + columns] = tiles[row][columns].id;
                 outState.putInt(NAME_ID + (row * MAX_COLS + columns), tiles[row][columns].id);

        super.onSaveInstanceState(outState);
    }

    /*
         * Randomize the puzzle board by moving the blank tile around (using valid movements); this will
         * ensure that the resulting board is solvable vs just randomly placing all the tiles on the puzzle.
         */
    private void randomizeTiles() {
        final int MAXIMUM_MOVES = 5;      // the number of times to move the empty tile before we consider the puzzle to be randomized
        int counter = 0;                  // number of successful moves of the empty tile
        int previousDirection = -1;

        initialize();

        while (counter < MAXIMUM_MOVES) {
            Log.w(NAME, "counter = " + counter + " emptyTile = (" + emptyTileRow + ", " + emptyTileColumn + ")");

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
        }   // end while

        setTileBackground();

        Log.w(NAME, "randomizeTiles: tiles = \n" + tilesToString());
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
        startTime = System.currentTimeMillis();
        movesView.setText(Integer.toString(moves));
        timeView.setText(longToHHMMSS(0L));

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

        Log.w(NAME, "initializeTiles: tiles = \n" + tilesToString());
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
//                tiles[row][column].id = row * MAX_COLS + column;
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
        Log.w(NAME, "isSolved: tiles = \n" + tilesToString());

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
                tilesToString += "(" + tiles[row][column].id + ", " + tiles[row][column].imageView.getDrawable() + ") ";
            }
            tilesToString += "\n";
        }

        return tilesToString;
    }

    private String longToHHMMSS(long time) {
        long totalSeconds = time / 1000;
        long second = totalSeconds % 60;

        long totalMinutes = totalSeconds / 60;
        long minute = totalMinutes % 60;

        long totalHours = totalMinutes / 60;
        long hour = totalHours % 24;

        return (hour <= 9 ? "0" : "") + hour + ":" + (minute <= 9 ? "0" : "") + minute + ":" + (second <= 9 ? "0" : "") + second;
    }
}
