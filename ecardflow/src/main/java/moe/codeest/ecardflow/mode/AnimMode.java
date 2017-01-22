package moe.codeest.ecardflow.mode;

import android.widget.ImageView;

/**
 * Created by codeest on 2017/1/20.
 */

public interface AnimMode {

    int D_LEFT = -1;
    int D_RIGHT = 1;

    void transformPage(ImageView ivBg, float position, int direction);
}
