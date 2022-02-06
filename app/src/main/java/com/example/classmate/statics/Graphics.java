package com.example.classmate.statics;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.classmate.R;

public class Graphics {

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

    public static final int[] PROFILE_PIC_LETTERS = {
            R.drawable.profile_letter_a, R.drawable.profile_letter_b, R.drawable.profile_letter_c,
            R.drawable.profile_letter_d, R.drawable.profile_letter_e, R.drawable.profile_letter_f,
            R.drawable.profile_letter_g, R.drawable.profile_letter_h, R.drawable.profile_letter_i,
            R.drawable.profile_letter_j, R.drawable.profile_letter_k, R.drawable.profile_letter_l,
            R.drawable.profile_letter_m, R.drawable.profile_letter_n, R.drawable.profile_letter_o,
            R.drawable.profile_letter_p, R.drawable.profile_letter_q, R.drawable.profile_letter_r,
            R.drawable.profile_letter_s, R.drawable.profile_letter_t, R.drawable.profile_letter_u,
            R.drawable.profile_letter_v, R.drawable.profile_letter_w, R.drawable.profile_letter_x,
            R.drawable.profile_letter_y, R.drawable.profile_letter_z
    };
}
