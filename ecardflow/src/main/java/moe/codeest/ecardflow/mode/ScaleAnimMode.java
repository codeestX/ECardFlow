package moe.codeest.ecardflow.mode;

import android.widget.ImageView;

/**
 * Created by codeest on 2017/2/25.
 */

public class ScaleAnimMode implements AnimMode {

    private static final int DEFAULT_SCALE_RATE = 1;
    private int mScaleRate;

    public ScaleAnimMode() {
        this.mScaleRate = DEFAULT_SCALE_RATE;
    }

    public ScaleAnimMode(int scaleRate) {
        this.mScaleRate = scaleRate;
    }

    @Override
    public void transformPage(ImageView ivBg, float position, int direction) {
        float mFraction = mScaleRate * (float) Math.abs(Math.sin(Math.PI * position));
        ivBg.setScaleX(1 + mFraction);
        ivBg.setScaleY(1 + mFraction);
    }
}
