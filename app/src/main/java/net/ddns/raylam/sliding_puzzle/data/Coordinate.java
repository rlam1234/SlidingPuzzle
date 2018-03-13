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

/**
 * This represents an x-y co-ordinate.
 */
public class Coordinate {
    public float x = -1;
    public float y = -1;

    public Coordinate() { }

    public Coordinate(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
