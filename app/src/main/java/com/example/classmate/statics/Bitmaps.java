package com.example.classmate.statics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.core.content.res.ResourcesCompat;

import com.example.classmate.R;

import java.io.ByteArrayOutputStream;

public class Bitmaps {

    public static final long MAX_SIZE = (long) Math.pow(2, 24);

    public static class Default {

        private static Bitmap getBitmap(Context context) {
            Drawable drawable = ResourcesCompat.getDrawable(context.getResources(),
                    R.drawable.account_circle_grey, null);
            assert drawable != null;
            return ((BitmapDrawable) drawable).getBitmap();
        }

        public static byte[] getBytes(Context context) {
            return Bitmaps.getBytes(getBitmap(context));
        }
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static byte[] getBytes(ImageView imageView) {
        return getBytes(getBitmap(imageView));
    }

    public static byte[] getBytes(Context context, int drawable) {
        return getBytes(getBitmap(context, drawable));
    }

    public static Bitmap getBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static BitmapDrawable getDrawable(ImageView imageView) {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        return ((BitmapDrawable) imageView.getDrawable());
    }

    public static Bitmap getBitmap(ImageView imageView) {
        return getDrawable(imageView).getBitmap();
    }

    public static Bitmap getBitmap(Context context, int drawable) {
        return getCircularBitmap(BitmapFactory.decodeResource(context.getResources(), drawable));
    }

    public static void setBytes(ImageView imageView, byte[] bytes) {
        Bitmap bitmap = getCircularBitmap(bytes);
        imageView.setImageBitmap(bitmap);
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        int bitmapSideLength = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap dstBitmap = Bitmap.createBitmap(bitmapSideLength, bitmapSideLength, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dstBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Rect rect = new Rect(0, 0, bitmapSideLength, bitmapSideLength);
        RectF rectF = new RectF(rect);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        float left = (bitmapSideLength - bitmap.getWidth()) / 2f;
        float top = (bitmapSideLength - bitmap.getHeight()) / 2f;
        canvas.drawBitmap(bitmap, left, top, paint);
        bitmap.recycle();
        return dstBitmap;
    }

    public static Bitmap getCircularBitmap(byte[] bytes) {
        return getCircularBitmap(getBitmap(bytes));
    }

    public static byte[] getCircularBytes(Bitmap bitmap) {
        return getBytes(getCircularBitmap(bitmap));
    }

    public static byte[] getCircularBytes(byte[] bytes) {
        return getBytes(getCircularBitmap(bytes));
    }

}
