package moe.codeest.ecardflow;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.renderscript.RSRuntimeException;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import moe.codeest.ecardflow.mode.AnimMode;
import moe.codeest.ecardflow.mode.BlurAnimMode;
import moe.codeest.ecardflow.provider.ImageProvider;
import moe.codeest.ecardflow.support.RecyclingBitmapDrawable;
import moe.codeest.ecardflow.util.FastBlur;
import moe.codeest.ecardflow.util.RSBlur;

/**
 * Created by codeest on 2017/1/16.
 */

public class ECardFlowLayout extends FrameLayout{

    private static final int SWITCH_ANIM_TIME = 300;    //300 ms
    private static final int MSG_JUDGE_RESET = 0x1;

    private Context mContext;
    private ExecutorService mThreadPool;
    private LruCache<String, RecyclingBitmapDrawable> mLruCache;
    private MyHandler mHandler;
    private NotifyRunnable mNotifyRunnable;

    private ImageView mBgImage;
    private ImageView mBlurImage;
    private ViewPager mViewPager;

    private ImageProvider mProvider;
    private AnimMode mAnimMode;

    private RecyclingBitmapDrawable curBp, lastBp, nextBp;
    private Bitmap curBlurBp, lastBlurBp, nextBlurBp;

    private int mCurDirection = AnimMode.D_RIGHT;
    private int mSwitchAnimTime = SWITCH_ANIM_TIME;
    private int mMinCacheSize;
    private float mLastOffset;
    private int mRadius;
    private int mCurPosition;
    private int mLastPosition;
    private boolean isSwitching;

    public ECardFlowLayout(Context context) {
        super(context);
    }

    public ECardFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.attr_layout);
        mSwitchAnimTime = ta.getInt(R.styleable.attr_layout_switchAnimTime, SWITCH_ANIM_TIME);
        ta.recycle();

        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initViewPager();
    }

    private void init() {
        mThreadPool = Executors.newCachedThreadPool();
        mNotifyRunnable = new NotifyRunnable();
        mHandler = new MyHandler(this);
        initCache();
        mBlurImage = new ImageView(mContext);
        initImageView(mBlurImage);
        mBgImage = new ImageView(mContext);
        initImageView(mBgImage);
    }

    private void initImageView(ImageView image) {
        image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(image);
    }

    private void initViewPager() {
        for (int i= 0; i< getChildCount(); i++) {
            if (getChildAt(i) instanceof ViewPager) {
                mViewPager = (ViewPager) getChildAt(i);
            }
        }
        if (mViewPager == null) {
            throw new RuntimeException("Can't find ViewPager in ECardFlowLayout");
        }
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset != 0 && positionOffset != mLastOffset) {
                    if (positionOffset < mLastOffset) {
                        mCurDirection = AnimMode.D_LEFT;
                    } else {
                        mCurDirection = AnimMode.D_RIGHT;
                    }
                    mLastOffset = positionOffset;
                }
                int lastPosition = Math.round(position + positionOffset);
                if (mLastPosition != lastPosition) {
                    if (mCurDirection == AnimMode.D_LEFT) {
                        switchBgToLast(lastPosition);
                    } else {
                        switchBgToNext(lastPosition);
                    }
                }
                mLastPosition = lastPosition;
                if (mAnimMode != null) {
                    mAnimMode.transformPage(mBgImage, position + positionOffset, mCurDirection);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initCache() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory() / 1024;
        int cacheSize = maxMemory / 5;
        mMinCacheSize = maxMemory / 8;
        mLruCache = new LruCache<String, RecyclingBitmapDrawable>(cacheSize) {
            @Override
            protected int sizeOf(String key, RecyclingBitmapDrawable value) {
                return value.getBitmap().getByteCount() / 1024;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, RecyclingBitmapDrawable oldValue, RecyclingBitmapDrawable newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                if (evicted && oldValue != null) {
                    oldValue.setIsCached(false);
                }
            }

            @Override
            protected RecyclingBitmapDrawable create(String key) {
                RecyclingBitmapDrawable bitmap = new RecyclingBitmapDrawable(getResources(), mProvider.onProvider(Integer.valueOf(key)));
                if (bitmap.getBitmap() == null)
                    return null;
                bitmap.setIsCached(true);
                return bitmap;
            }
        };
    }

    private void updateNextRes(final int position) {
        mCurPosition = position;
        detachBitmap(lastBp);
        lastBp = curBp;
        curBp = nextBp;
        if (mBlurImage != null) {
            recycleBitmap(lastBlurBp);
            lastBlurBp = curBlurBp;
            curBlurBp = nextBlurBp;
        }
        if (mProvider != null) {
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    nextBp = loadBitmap(position + 1);
                    if (mBlurImage != null) {
                        nextBlurBp = blurBitmap(position + 1);
                    }
                    sendMsg();
                }
            });
        } else {
            throw new RuntimeException("setImageProvider is necessary");
        }
    }

    private void updateLastRes(final int position) {
        mCurPosition = position;
        detachBitmap(nextBp);
        nextBp = curBp;
        curBp = lastBp;
        if (mBlurImage != null) {
            recycleBitmap(nextBlurBp);
            nextBlurBp = curBlurBp;
            curBlurBp = lastBlurBp;
        }
        if (mProvider != null) {
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    lastBp = loadBitmap(position - 1);
                    if (mBlurImage != null) {
                        lastBlurBp = blurBitmap(position - 1);
                    }
                    sendMsg();
                }
            });
        } else {
            throw new RuntimeException("setImageProvider is necessary");
        }
    }

    private void startTrans(int targetPosition, ImageView targetImage, RecyclingBitmapDrawable startBp, RecyclingBitmapDrawable endBp) {
        if (endBp == null)
            endBp = loadBitmap(targetPosition);
        TransitionDrawable td = new TransitionDrawable(new Drawable[] {startBp, endBp});
        targetImage.setImageDrawable(td);
        td.setCrossFadeEnabled(true);
        td.startTransition(mSwitchAnimTime);
    }

    private void switchBgToNext(final int targetPosition) {
        if (isSwitching) {
            return;
        }
        isSwitching = true;
        startTrans(targetPosition + 1, mBgImage, curBp, nextBp);
        if (mBlurImage != null) {
            startTrans(targetPosition + 1, mBlurImage, new RecyclingBitmapDrawable(getResources(), curBlurBp), new RecyclingBitmapDrawable(getResources(), nextBlurBp));
        }
        mNotifyRunnable.setTarget(targetPosition, true);
        mBgImage.postDelayed(mNotifyRunnable, mSwitchAnimTime);
    }

    private void switchBgToLast(final int targetPosition) {
        if (isSwitching) {
            return;
        }
        isSwitching = true;
        startTrans(targetPosition - 1, mBgImage, curBp, lastBp);
        if (mBlurImage != null) {
            startTrans(targetPosition - 1, mBlurImage, new RecyclingBitmapDrawable(getResources(), curBlurBp), new RecyclingBitmapDrawable(getResources(), lastBlurBp));
        }
        mNotifyRunnable.setTarget(targetPosition, false);
        mBgImage.postDelayed(mNotifyRunnable, mSwitchAnimTime);
    }

    private void jumpBgToTarget(final int targetPosition) {
        mCurPosition = targetPosition;
        if (isSwitching) {
            return;
        }
        isSwitching = true;
        final RecyclingBitmapDrawable newBitmap = loadBitmap(targetPosition);
        TransitionDrawable td = new TransitionDrawable(new Drawable[] {curBp, newBitmap});
        mBgImage.setImageDrawable(td);
        td.setCrossFadeEnabled(true);
        td.startTransition(mSwitchAnimTime);
        if (mBlurImage != null) {
            TransitionDrawable tdb = new TransitionDrawable(new Drawable[] {new BitmapDrawable(mContext.getResources(), curBlurBp),
                    new BitmapDrawable(mContext.getResources(), blurBitmap(targetPosition))});
            mBlurImage.setImageDrawable(tdb);
            tdb.setCrossFadeEnabled(true);
            tdb.startTransition(mSwitchAnimTime);
        }
        mBgImage.postDelayed(new Runnable() {
            @Override
            public void run() {
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        detachBitmap(nextBp);
                        detachBitmap(lastBp);
                        detachBitmap(curBp);
                        curBp = newBitmap;
                        nextBp = loadBitmap(targetPosition + 1);
                        lastBp = loadBitmap(targetPosition - 1);
                        if (mBlurImage != null) {
                            recycleBitmap(nextBlurBp);
                            recycleBitmap(lastBlurBp);
                            recycleBitmap(curBlurBp);
                            curBlurBp = blurBitmap(targetPosition);
                            nextBlurBp = blurBitmap(targetPosition + 1);
                            lastBlurBp = blurBitmap(targetPosition - 1);
                        }
                        sendMsg();
                    }
                });
            }
        }, mSwitchAnimTime);
    }

    private void sendMsg() {
        Message msg = new Message();
        msg.what = MSG_JUDGE_RESET;
        if (mHandler != null) {
            mHandler.sendMessage(msg);
        }
    }

    private void judgeReset() {
        isSwitching = false;
        if (Math.abs(mCurPosition - mLastPosition) <= 1) {
            if (mCurPosition > mLastPosition) {
                switchBgToLast(mLastPosition);
            } else if (mCurPosition < mLastPosition) {
                switchBgToNext(mLastPosition);
            }
        } else {
            jumpBgToTarget(mLastPosition);
        }
    }

    @Nullable
    private Bitmap blurBitmap(int targetPosition) {
        RecyclingBitmapDrawable bitmapDrawable = mLruCache.get(String.valueOf(targetPosition));
        if (bitmapDrawable == null || bitmapDrawable.getBitmap() == null) {
            return null;
        }
        Bitmap blurBitmap = bitmapDrawable.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                blurBitmap = RSBlur.blur(mContext, blurBitmap, mRadius);
            } catch (RSRuntimeException e) {
                blurBitmap = FastBlur.blur(blurBitmap, mRadius, true);
            }
        } else {
            blurBitmap = FastBlur.blur(blurBitmap, mRadius, true);
        }
        return blurBitmap;
    }

    private RecyclingBitmapDrawable loadBitmap(int targetPosition) {
        RecyclingBitmapDrawable bitmap = mLruCache.get(String.valueOf(targetPosition));
        if (bitmap != null) {
            bitmap.setIsDisplayed(true);
        }
        return bitmap;
    }

    private void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null)
            bitmap.recycle();
    }

    private void detachBitmap(RecyclingBitmapDrawable bitmap) {
        if (bitmap != null) {
            bitmap.setIsDisplayed(false);
        }
    }

    public void setImageProvider(ImageProvider provider) {
        mProvider = provider;
        curBp = loadBitmap(0);
        nextBp = loadBitmap(1);
        if (mAnimMode == null) {
            throw new RuntimeException("You should setAnimMode before setImageProvider");
        }
        if (mBlurImage != null) {
            curBlurBp = blurBitmap(0);
            nextBlurBp = blurBitmap(1);
            mBlurImage.setImageBitmap(blurBitmap(0));
        }
        mBgImage.setImageBitmap(curBp.getBitmap());
    }

    public void setAnimMode(AnimMode animMode) {
        mAnimMode = animMode;
        if (!(mAnimMode instanceof BlurAnimMode)) {
            removeView(mBlurImage);
            mBlurImage = null;
        } else {
            mRadius = ((BlurAnimMode) mAnimMode).getBlurRadius();
        }
    }

    public void setSwitchAnimTime(int switchAnimTime) {
        mSwitchAnimTime = switchAnimTime;
    }

    public void setCacheSize(int megabytes) {
        if (megabytes * 1024 >= mMinCacheSize) {
            mLruCache.resize(megabytes * 1024);
        } else {
            Log.w(getClass().getName(), "Size is too small to resize");
        }
    }

    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        if (!mThreadPool.isShutdown()) {
            mThreadPool.shutdown();
        }
        mHandler = null;
        detachBitmap(curBp);
        detachBitmap(lastBp);
        detachBitmap(nextBp);
        mLruCache.evictAll();
        if (mBlurImage != null) {
            recycleBitmap(curBlurBp);
            recycleBitmap(lastBlurBp);
            recycleBitmap(nextBlurBp);
        }
    }

    private class NotifyRunnable implements Runnable {

        private int targetPosition;
        private boolean isNext;

        @Override
        public void run() {
            if (isNext) {
                updateNextRes(targetPosition);
            } else {
                updateLastRes(targetPosition);
            }
        }

        void setTarget(int targetPosition, boolean isNext) {
            this.targetPosition = targetPosition;
            this.isNext = isNext;
        }
    }

    private static class MyHandler extends Handler {

        WeakReference<ECardFlowLayout> mLayout;

        MyHandler(ECardFlowLayout layout) {
            mLayout = new WeakReference<>(layout);
        }

        @Override
        public void handleMessage(Message msg) {
            ECardFlowLayout layout = mLayout.get();
            switch (msg.what) {
                case MSG_JUDGE_RESET:
                    layout.judgeReset();
                    break;
            }
        }
    }
}