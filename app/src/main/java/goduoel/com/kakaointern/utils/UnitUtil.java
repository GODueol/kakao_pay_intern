package goduoel.com.kakaointern.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class UnitUtil {

    public static int convertDpToPixel(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }
}

