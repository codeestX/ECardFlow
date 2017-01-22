package moe.codeest.ecardflow.mode;

import android.widget.ImageView;

/**
 * Created by codeest on 2017/1/20.
 */

public class BlurAnimMode implements AnimMode {

    private static final int DEFAULT_BLUR_RADIUS = 25;
    private int mRadius;

    public BlurAnimMode() {
        this.mRadius = DEFAULT_BLUR_RADIUS;
    }

    public BlurAnimMode(int mRadius) {
        this.mRadius = mRadius;
    }

    @Override
    public void transformPage(ImageView ivBg, float position, int direction) {
        float mFraction = (float) Math.cos(2 * Math.PI * position);
        if (mFraction < 0)
            mFraction = 0;
            ivBg.setAlpha(mFraction);
    }

    public int getBlurRadius() {
        return mRadius;
    }
}
