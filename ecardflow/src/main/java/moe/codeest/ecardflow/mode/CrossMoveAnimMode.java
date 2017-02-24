package moe.codeest.ecardflow.mode;

import android.widget.ImageView;

/**
 * Created by codeest on 2017/2/25.
 */

public class CrossMoveAnimMode implements AnimMode {

    private static final float DEFAULT_MOVE_SCALE = 1.3f;
    private float mScale;

    public CrossMoveAnimMode() {
        this.mScale = DEFAULT_MOVE_SCALE;
    }

    public CrossMoveAnimMode(float mScale) {
        this.mScale = mScale;
    }

    @Override
    public void transformPage(ImageView ivBg, float position, int direction) {
        ivBg.setScaleX(mScale);
        ivBg.setScaleY(mScale);
        float totalMoveWidth = ivBg.getWidth() * ((mScale - 1) / 2);
        int lastPosition = Math.round(position);
        float mFraction;
        if (lastPosition % 2 == 0) {
            mFraction = -1 * (float) Math.sin(Math.PI * position);
        } else {
            mFraction = (float) Math.sin(Math.PI * position);
        }
        ivBg.setTranslationY(totalMoveWidth * mFraction);
    }
}
