package com.example.classmate.statics;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.classmate.R;

public class Background {

    public static Drawable get(Context context, int drawable, int color) {
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, drawable);
        Drawable wrappedDrawable = null;
        if (unwrappedDrawable != null) {
            wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
            DrawableCompat.setTint(wrappedDrawable, color);
        }
        return wrappedDrawable;
    }

    public static Drawable getCourseTabDrawable(Context context, int color) {
        return get(context, R.drawable.course_tab, color);
    }
}
