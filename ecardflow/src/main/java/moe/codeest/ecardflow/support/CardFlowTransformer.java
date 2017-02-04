package moe.codeest.ecardflow.support;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by codeest on 2017/1/8.
 */

public class CardFlowTransformer implements ViewPager.PageTransformer {

    public static final int MAX_ROTATE_Y = 5;
    private static final float PAGE_SCALE = 0.9f;
    public static final int D_LEFT = -1;
    public static final int D_RIGHT = 1;

    private int mDirection = D_LEFT;
    private float mMaxRotateY = MAX_ROTATE_Y;

    @Override
    public void transformPage(View page, float position) {
        final float height = page.getHeight();
        final float width = page.getWidth();
        page.setPivotY(0.5f * height);
        page.setPivotX(0.5f * width);
        if (position >= -2 && position <= 2) {
            float mFraction = mDirection * (float) Math.abs(Math.sin(Math.PI * position));
            page.setRotationY(mFraction * mMaxRotateY);
            page.setScaleX(PAGE_SCALE);
            page.setScaleY(PAGE_SCALE);
        }
    }

    public void setDirection(int mDirection) {
        this.mDirection = mDirection;
    }

    public void setMaxRotateY(int rotate) {
        mMaxRotateY = rotate;
    }
}
