package moe.codeest.ecardflow.provider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

/**
 * Created by codeest on 2017/1/19.
 */

public class DrawableImageProvider implements ImageProvider{

    private int[] mRes;
    private int width, height;
    private Context mContext;

    public DrawableImageProvider(Context mContext, int[] res, int width, int height) {
        this.mRes = res;
        this.mContext = mContext;
        this.width = width;
        this.height = height;
    }

    @Nullable
    @Override
    public Bitmap onProvider(int position) {
        if (position >= 0 && position < mRes.length) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(mContext.getResources(), mRes[position], options);
            int inSampleSize = Math.min(options.outWidth / width, options.outHeight / height);
            int dstSample = 1;
            if (inSampleSize > dstSample) {
                dstSample = inSampleSize;
            }
            options.inJustDecodeBounds = false;
            options.inSampleSize = dstSample;
            return BitmapFactory.decodeResource(mContext.getResources(), mRes[position], options);
        } else {
            return null;
        }
    }
}
