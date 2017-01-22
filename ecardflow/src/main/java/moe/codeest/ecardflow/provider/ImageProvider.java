package moe.codeest.ecardflow.provider;

import android.graphics.Bitmap;

/**
 * Created by codeest on 2017/1/19.
 */

public interface ImageProvider {

    Bitmap onProvider(int position);
}
