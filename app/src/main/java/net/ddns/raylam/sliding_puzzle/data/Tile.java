package net.ddns.raylam.sliding_puzzle.data;

import android.widget.ImageView;

/*
 * This represents one of the tiles in the puzzle.
 * The ImageView contains the picture on the tile.
 * The id contains an identifier to make it easier to programmatically determine which tile is which.
 */
public class Tile {
    public int id;
    public ImageView imageView;

    public Tile(int id, ImageView imageView) {
        this.id = id;
        this.imageView = imageView;
    }

    @Override
    public String toString() {
        return "id = " + id + ", imageView = " + imageView;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Tile && (id == ((Tile) obj).id);
    }
}
