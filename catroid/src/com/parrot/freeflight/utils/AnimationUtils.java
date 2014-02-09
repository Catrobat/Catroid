
package com.parrot.freeflight.utils;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

@SuppressLint("NewApi")
public final class AnimationUtils
{
    private AnimationUtils()
    {}


    public static Animation makeInvisibleAnimated(final View view)
    {
        final Animation a = new AlphaAnimation(1.00f, 0.00f);
        a.setDuration(500);
        a.setAnimationListener(getFadeOutListener(view));

        view.startAnimation(a);
        
        return a;
    }


    @SuppressLint("NewApi")
    public static Animation makeVisibleAnimated(final View view)
    {
        final Animation a = new AlphaAnimation(0.00f, 1.00f);
        a.setDuration(500);
        a.setAnimationListener(getFadeInListener(view));

        view.startAnimation(a);
        
        return a;
    }


    private static AnimationListener getFadeOutListener(final View view)
    {
        final AnimationListener fadeOutListener = new AnimationListener()
        {

            public void onAnimationEnd(final Animation animation)
            {
                view.setVisibility(View.INVISIBLE);
            }


            public void onAnimationRepeat(final Animation animation)
            {

            }


            public void onAnimationStart(final Animation animation)
            {

            }
        };

        return fadeOutListener;
    }


    private static AnimationListener getFadeInListener(final View view)
    {
        final AnimationListener fadeInListener = new AnimationListener()
        {

            public void onAnimationEnd(final Animation animation)
            {

            }


            public void onAnimationRepeat(final Animation animation)
            {

            }


            public void onAnimationStart(final Animation animation)
            {
                view.setVisibility(View.VISIBLE);
            }
        };

        return fadeInListener;
    }
}
