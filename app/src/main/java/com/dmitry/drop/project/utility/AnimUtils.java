package com.dmitry.drop.project.utility;


import android.content.Context;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;


/**
 * Utility methods and constants for working with animations.
 */
public class AnimUtils {

    private static Interpolator fastOutSlowIn;
    public static int VIEW_POST = 1000;
    public static final float ORIGINAL_SCALE = 1f;
    public static final float ORIGINAL_ALPHA = 1f;
    public static final float MAXIMUM_SCALE = 25f;
    public static final float TRANSPARENT_ALPHA = 0f;
    public static final int ONE_SECOND = 1000;

    private AnimUtils() {
    }

    public static Interpolator getFastOutSlowInInterpolator(Context context) {
        if (fastOutSlowIn == null) {
            fastOutSlowIn = AnimationUtils.loadInterpolator(context,
                    android.R.interpolator.fast_out_slow_in);
        }
        return fastOutSlowIn;
    }
}
