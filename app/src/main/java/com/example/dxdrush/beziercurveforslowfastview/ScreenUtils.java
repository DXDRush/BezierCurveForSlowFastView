package com.example.dxdrush.beziercurveforslowfastview;

import android.content.Context;

/**
 * Created by dxdrush on 10/10/2017.
 */

public class ScreenUtils {

    public static int dp2px(Context context, int values) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (values * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
