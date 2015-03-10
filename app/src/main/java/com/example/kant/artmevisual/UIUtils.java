package com.example.kant.artmevisual;

import android.os.Build;
import android.view.View;

/**
 * Created by Quentin on 28/02/2015.
 * EpiAndroid Project.
 */
public class UIUtils {

    public static void setAccessibilityIgnore(View view) {
        view.setClickable(false);
        view.setFocusable(false);
        view.setContentDescription("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        }
    }
}
