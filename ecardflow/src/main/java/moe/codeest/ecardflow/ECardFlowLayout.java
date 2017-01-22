package moe.codeest.ecardflow;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Handler;
import android.renderscript.RSRuntimeException;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import moe.codeest.ecardflow.mode.AnimMode;
import moe.codeest.ecardflow.mode.BlurAnimMode;
import moe.codeest.ecardflow.provider.ImageProvider;
import moe.codeest.ecardflow.util.FastBlur;
import moe.codeest.ecardflow.util.RSBlur;

/**
 * Created by codeest on 2017/1/16.
 */

public class ECardFlowLayout extends FrameLayout{

    private static final int SWITCH_ANIM_TIME = 300;

    private Context mContext;
    private ExecutorService mThreadPool;
    private Handler mHandler;
    private NotifyRunnable mNotifyRunnable;

    private ImageView mBgImage;
    private ImageView mBlurImage;
    private ViewPager mViewPager;

    private ImageProvider mProvider;
    private AnimMode mAnimMode;

    private Bitmap curBp, lastBp, nextBp;
    private Bitmap curBlurBp, lastBlurBp, nextBlurBp;

    private int mCurDirection = AnimMode.D_RIGHT;
    private int mSwitchAnimTime = SWITCH_ANIM_TIME;
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

    private void init() {

        mThreadPool = Executors.newCachedThreadPool();
        mHandler = new Handler();
        mNotifyRunnable = new NotifyRunnable();

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

    private void updateNextRes(final int position) {
        mCurPosition = position;
        recycleBitmap(lastBp);
        lastBp = curBp;
        curBp = nextBp;
        if (mProvider != null) {
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    nextBp = mProvider.onProvider(position + 1);
                }
            });
        } else {
            throw new RuntimeException("setImageProvider is necessary");
        }
        if (mBlurImage != null) {
            recycleBitmap(lastBlurBp);
            lastBlurBp = curBlurBp;
            curBlurBp = nextBlurBp;
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    nextBlurBp = blurBitmap(mProvider.onProvider(position + 1));
                }
            });
        }
    }

    private void updateLastRes(final int position) {
        mCurPosition = position;
        recycleBitmap(nextBp);
        nextBp = curBp;
        curBp = lastBp;
        if (mProvider != null) {
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    lastBp = mProvider.onProvider(position - 1);
                }
            });
        } else {
            throw new RuntimeException("setImageProvider is necessary");
        }
        if (mBlurImage != null) {
            recycleBitmap(nextBlurBp);
            nextBlurBp = curBlurBp;
            curBlurBp = lastBlurBp;
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    lastBlurBp = blurBitmap(mProvider.onProvider(position - 1));
                }
            });
        }
    }

    private void switchBgToNext(final int targetPosition) {
        if (isSwitching) {
            return;
        }
        isSwitching = true;
        TransitionDrawable td = new TransitionDrawable(new Drawable[] {new BitmapDrawable(mContext.getResources(), curBp),
                new BitmapDrawable(mContext.getResources(), nextBp)});
        mBgImage.setImageDrawable(td);
        td.setCrossFadeEnabled(true);
        td.startTransition(mSwitchAnimTime);
        if (mBlurImage != null) {
            TransitionDrawable tdb = new TransitionDrawable(new Drawable[] {new BitmapDrawable(mContext.getResources(), curBlurBp),
                    new BitmapDrawable(mContext.getResources(), nextBlurBp)});
            mBlurImage.setImageDrawable(tdb);
            tdb.setCrossFadeEnabled(true);
            tdb.startTransition(mSwitchAnimTime);
        }
        mNotifyRunnable.setTarget(targetPosition, true);
        mBgImage.postDelayed(mNotifyRunnable, mSwitchAnimTime);
    }

    private void switchBgToLast(final int targetPosition) {
        if (isSwitching) {
            return;
        }
        isSwitching = true;
        TransitionDrawable td = new TransitionDrawable(new Drawable[] {new BitmapDrawable(mContext.getResources(), curBp),
                new BitmapDrawable(mContext.getResources(), lastBp)});
        mBgImage.setImageDrawable(td);
        td.setCrossFadeEnabled(true);
        td.startTransition(mSwitchAnimTime);
        if (mBlurImage != null) {
            TransitionDrawable tdb = new TransitionDrawable(new Drawable[] {new BitmapDrawable(mContext.getResources(), curBlurBp),
                    new BitmapDrawable(mContext.getResources(), lastBlurBp)});
            mBlurImage.setImageDrawable(tdb);
            tdb.setCrossFadeEnabled(true);
            tdb.startTransition(mSwitchAnimTime);
        }
        mNotifyRunnable.setTarget(targetPosition, false);
        mBgImage.postDelayed(mNotifyRunnable, mSwitchAnimTime);
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
            isSwitching = false;
            judgeReset();
        }

        public void setTarget(int targetPosition, boolean isNext) {
            this.targetPosition = targetPosition;
            this.isNext = isNext;
        }
    }

    private void judgeReset() {
        if (mCurPosition > mLastPosition) {
            switchBgToLast(mLastPosition);
        } else if (mCurPosition < mLastPosition) {
            switchBgToNext(mLastPosition);
        }
    }

    @Nullable
    private Bitmap blurBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        Bitmap blurBitmap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                blurBitmap = RSBlur.blur(mContext, bitmap, mRadius);
            } catch (RSRuntimeException e) {
                blurBitmap = FastBlur.blur(bitmap, mRadius, true);
            }
        } else {
            blurBitmap = FastBlur.blur(bitmap, mRadius, true);
        }
        return blurBitmap;
    }

    private void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null)
            bitmap.recycle();
    }

    public void setImageProvider(ImageProvider provider) {
        mProvider = provider;
        curBp = mProvider.onProvider(0);
        nextBp = mProvider.onProvider(1);
        if (mAnimMode == null) {
            throw new RuntimeException("You should setAnimMode before setImageProvider");
        }
        if (mBlurImage != null) {
            curBlurBp = blurBitmap(mProvider.onProvider(0));
            nextBlurBp = blurBitmap(mProvider.onProvider(1));
            mBlurImage.setImageBitmap(blurBitmap(curBlurBp));
        }
        mBgImage.setImageBitmap(curBp);
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

    public void onDestroy() {
        recycleBitmap(curBp);
        recycleBitmap(lastBp);
        recycleBitmap(nextBp);
        if (mBlurImage != null) {
            recycleBitmap(curBlurBp);
            recycleBitmap(lastBlurBp);
            recycleBitmap(nextBlurBp);
        }
        mHandler.removeCallbacks(mNotifyRunnable);
    }
}
