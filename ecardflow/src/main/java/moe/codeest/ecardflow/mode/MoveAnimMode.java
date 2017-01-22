package moe.codeest.ecardflow.mode;

import android.widget.ImageView;

/**
 * Created by codeest on 2017/1/20.
 */

public class MoveAnimMode implements AnimMode {

    private static final float DEFAULT_MOVE_SCALE = 1.3f;
    private float mScale;

    public MoveAnimMode() {
        this.mScale = DEFAULT_MOVE_SCALE;
    }

    public MoveAnimMode(float mScale) {
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
        ivBg.setTranslationX(totalMoveWidth * mFraction);
    }
}
