package com.bobin.somemapapp.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;

// part of com.material.components project
public final class ViewUtils {
    public static void expand(final View v) {
        Animation a = expandAction(v);
        v.startAnimation(a);
    }

    public static float dpToPixels(Context context, int dp) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void changeAllTextViewsToCustomFont(ViewGroup container) {
        Context context = container.getContext();
        for (int i = 0; i < container.getChildCount(); ++i) {
            View view = container.getChildAt(i);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                Typeface myCustomFont = getCustomTypeface(context);
                textView.setTypeface(myCustomFont);
            }
        }
    }

    public static Typeface getCustomTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "SourceSansPro-Regular.ttf");
    }

    public static Spanned toHtml(String html) {
        return Html.fromHtml(html);
    }

    public static void glideRoundImage(ImageView imageView, String url, RequestListener<Drawable> listener) {
        Glide.with(imageView)
                .load(url)
                .apply(new RequestOptions().circleCrop())
                .listener(listener)
                .into(imageView);
    }

    public static void glideRoundImage(ImageView imageView, String url) {
        Glide.with(imageView)
                .load(url)
                .apply(new RequestOptions().circleCrop())
                .into(imageView);
    }

    private static Animation expandAction(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
        return a;
    }
}
