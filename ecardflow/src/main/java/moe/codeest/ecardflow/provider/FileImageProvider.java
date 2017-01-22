package moe.codeest.ecardflow.provider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import java.io.File;

/**
 * Created by codeest on 2017/1/21.
 */

public class FileImageProvider implements ImageProvider{

    private File[] mFile;
    private int width, height;

    public FileImageProvider(File[] files, int width, int height) {
        this.mFile = files;
        this.width = width;
        this.height = height;
    }

    @Nullable
    @Override
    public Bitmap onProvider(int position) {
        if (position >= 0 && position < mFile.length && mFile[position].exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mFile[position].getPath(), options);
            int inSampleSize = Math.min(options.outWidth / width, options.outHeight / height);
            int dstSample = 1;
            if (inSampleSize > dstSample) {
                dstSample = inSampleSize;
            }
            options.inJustDecodeBounds = false;
            options.inSampleSize = dstSample;
            return BitmapFactory.decodeFile(mFile[position].getPath(), options);
        } else {
            return null;
        }
    }
}
