package moe.codeest.ecardflow.provider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

/**
 * Created by codeest on 2017/1/21.
 */

public class PathImageProvider implements ImageProvider{

    private String[] mPath;
    private int width, height;

    public PathImageProvider(String[] path, int width, int height) {
        this.mPath = path;
        this.width = width;
        this.height = height;
    }

    @Nullable
    @Override
    public Bitmap onProvider(int position) {
        if (position >= 0 && position < mPath.length) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mPath[position], options);
            int inSampleSize = Math.min(options.outWidth / width, options.outHeight / height);
            int dstSample = 1;
            if (inSampleSize > dstSample) {
                dstSample = inSampleSize;
            }
            options.inJustDecodeBounds = false;
            options.inSampleSize = dstSample;
            return BitmapFactory.decodeFile(mPath[position], options);
        } else {
            return null;
        }
    }
}