/*
 * This Sliding Tile Puzzle application for Android™ was created by Raymond Lam for the final project of SCS2682: Mobile Applications for Android Devices.
 *
 * Copyright © 2017 Raymond Lam. All rights reserved.
 *
 * No part of this application, either code or image, may be used for any purpose other than to evaluate his programming style.
 * Therefore, any reproduction or modification by any means is strictly prohibited without prior written permission.
 *
 */
package net.ddns.raylam.sliding_puzzle.data;

import android.support.annotation.NonNull;
import android.widget.ImageView;

/*
 * This represents one of the tiles in the puzzle.
 * The ImageView contains the picture on the tile.
 * The id contains an identifier to make it easier to programmatically determine which tile is which.
 */
public class Tile {
	private static final String NAME = Tile.class.getSimpleName();

	public int id;
    @NonNull
    public ImageView imageView;

	public float startX = -1;
	public float startY = -1;

    public Tile(int id, @NonNull final ImageView imageView) {
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
