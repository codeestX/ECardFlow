package moe.codeest.ecardflow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

import moe.codeest.ecardflow.support.CardFlowTransformer;
import moe.codeest.ecardflow.util.DimenUtils;

/**
 * Created by codeest on 2017/1/8.
 */

public class ECardFlow extends ViewPager {

    private CardFlowTransformer mTransformer;
    private ScrollerCustomDuration mScroller;
    private OnExpandStateListener onExpandStateListener;

    public static final int SLIDE_UP_TO_EXPAND = 10;
    public static final int CLICK_TO_EXPAND = 11;

    private static final int DEFAULT_BASE_SWITCH_TIME = 200;
    private static final int DEFAULT_EXPAND_TIME = 700;
    private static final int DEFAULT_EXPAND_MODE = SLIDE_UP_TO_EXPAND;
    private static final int DEFAULT_SWITCH_TIME = DEFAULT_BASE_SWITCH_TIME * 6;
    private static final int DEFAULT_PRELOAD_PAGE_NUM = 3;

    private float mLastX, mLastY, mInterLastX, mInterLastY;
    private float mPageScaleX, mPageScaleY, mScaleX, mScaleY, mScrollY;
    private float mRate;
    private boolean hasReset = true;
    private boolean isExpanding = false;
    private boolean isSwitching = false;

    //Custom Attrs
    private int mSlop;
    private int mExpandTime;
    private int mExpandMode;
    private int mSwitchTime;
    private int mPreloadPageNum;
    private int mMaxRotateY;

    public ECardFlow(Context context) {
        super(context);
    }

    public ECardFlow(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.attr);
        mSwitchTime = ta.getInt(R.styleable.attr_switchTime, DEFAULT_SWITCH_TIME);
        mExpandMode = ta.getInt(R.styleable.attr_expandMode, DEFAULT_EXPAND_MODE);
        mExpandTime = ta.getInteger(R.styleable.attr_expandTime, DEFAULT_EXPAND_TIME);
        mPreloadPageNum = ta.getInteger(R.styleable.attr_preloadPageNum, DEFAULT_PRELOAD_PAGE_NUM);
        mSlop = ta.getInteger(R.styleable.attr_touchSlop, 0);
        mMaxRotateY = ta.getInteger(R.styleable.attr_touchSlop, CardFlowTransformer.MAX_ROTATE_Y);
        ta.recycle();

        mTransformer = new CardFlowTransformer();
        mTransformer.setMaxRotateY(mMaxRotateY);
        setPageTransformer(true, mTransformer);
        initSwitchSpeed(mSwitchTime * 1.00f / DEFAULT_BASE_SWITCH_TIME);
        initExpandRate();
        mSlop = ViewConfiguration.get(this.getContext()).getScaledTouchSlop();
        addOnPageChangeListener(new OnDirectionListener());
        setOffscreenPageLimit(mPreloadPageNum);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthSize >= DimenUtils.getScreenWidth(getContext().getApplicationContext())) {
            widthSize = DimenUtils.getScreenWidth(getContext().getApplicationContext()) * 4 / 5;
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isExpanding) {
            return false;
        }
        int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    if(mLastX == 0) {
                        mLastX = (int) event.getRawX();
                    }
                    if(mLastY == 0) {
                        mLastY = (int) event.getRawY();
                    }
                    int mCurX = (int) event.getRawX();
                    int mCurY = (int) event.getRawY();
                    if (Math.abs(mCurX - mLastX) > mSlop && hasReset) {
                        hasReset = false;
                        if (mCurX > mLastX) {
                            gotoLast();
                        } else {
                            gotoNext();
                        }
                    } else if(mExpandMode == SLIDE_UP_TO_EXPAND && mLastY - mCurY > mSlop && hasReset && !isSwitching) {
                        hasReset = false;
                        expand();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    int mUpX = (int) event.getRawX();
                    int mUpY = (int) event.getRawY();
                    if (mExpandMode == CLICK_TO_EXPAND && Math.abs(mUpX - mInterLastX) <= mSlop && Math.abs(mUpY - mInterLastY) <= mSlop && !isSwitching) {
                        expand();
                    }
                    mLastX = 0;
                    mLastY = 0;
                    hasReset = true;
                    break;
            }
            return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (isSwitching) {
                    return false;
                }
                mInterLastX = (int) event.getRawX();
                mInterLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                return !isExpanding;
            case MotionEvent.ACTION_UP:
                mLastX = 0;
                mLastY = 0;
                mInterLastX = 0;
                mInterLastY = 0;
                hasReset = true;
        }
        return super.onInterceptTouchEvent(event);
    }

    public void gotoNext() {
        setCurrentItem(getCurrentItem() + 1);
    }

    public void gotoLast() {
        setCurrentItem(getCurrentItem() - 1);
    }

    public void setExpandTime(int time) {
        mExpandTime = time;
    }

    public void setTouchSlop(int slop) {
        mSlop = slop;
    }

    public boolean isExpanding() {
        return isExpanding;
    }

    public void expand() {
        isExpanding = true;
        View pageView = getPageView();
        if (pageView == null)
            return;
        cacheData(pageView);
        int location[] = new int[2];
        getLocationInWindow(location);
        pageView.animate().scaleX(1).setDuration(mExpandTime).start();
        pageView.animate().scaleY(1).setDuration(mExpandTime).start();
        mScrollY = (mRate - 1f) * getHeight() / 2 - location[1];
        animate().scaleX(mRate).setDuration(mExpandTime).start();
        animate().scaleY(mRate).setDuration(mExpandTime).start();
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, "translationY", 0, mScrollY).setDuration(mExpandTime);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ECardFlow.this.setPadding(
                        ECardFlow.this.getPaddingLeft(),
                        ECardFlow.this.getPaddingTop(),
                        ECardFlow.this.getPaddingRight(),
                        ECardFlow.this.getPaddingBottom() + getExtraPaddingBottom());
                super.onAnimationEnd(animation);
            }
        });
        anim.start();
        if (onExpandStateListener != null) {
            onExpandStateListener.onExpand(pageView, getCurrentItem());
        }
    }

    public void shrink() {
        isExpanding = false;
        View pageView = getPageView();
        if (pageView == null)
            return;
        pageView.animate().scaleX(mPageScaleX).setDuration(mExpandTime).start();
        pageView.animate().scaleY(mPageScaleY).setDuration(mExpandTime).start();
        animate().scaleX(mScaleX).setDuration(mExpandTime).start();
        animate().scaleY(mScaleY).setDuration(mExpandTime).start();
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, "translationY", mScrollY, 0).setDuration(mExpandTime);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                ECardFlow.this.setPadding(
                        ECardFlow.this.getPaddingLeft(),
                        ECardFlow.this.getPaddingTop(),
                        ECardFlow.this.getPaddingRight(),
                        ECardFlow.this.getPaddingBottom() - getExtraPaddingBottom());
                super.onAnimationEnd(animation);
            }
        });
        anim.start();
        if (onExpandStateListener != null) {
            onExpandStateListener.onShrink(pageView, getCurrentItem());
        }
    }

    private View getPageView() {
        try {
            Fragment fragment = (Fragment) getAdapter().instantiateItem(this, getCurrentItem());
            return fragment.getView();
        } catch (ClassCastException e) {
            throw new ClassCastException("Only support fragment page");
        }
    }

    private int getExtraPaddingBottom() {
        if (getHeight() * mRate > DimenUtils.getScreenHeight(getContext().getApplicationContext())) {
            return (int) ((getHeight() * mRate - DimenUtils.getScreenHeight(getContext().getApplicationContext())) / mRate);
        } else {
            return  0;
        }
    }

    private void cacheData(View view) {
        mPageScaleX = view.getScaleX();
        mPageScaleY = view.getScaleY();
        mScaleX = getScaleX();
        mScaleY = getScaleY();
    }

    private class OnDirectionListener implements OnPageChangeListener {

        private float mLastOffset;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mTransformer != null && positionOffset != 0 && positionOffset != mLastOffset) {
                if (positionOffset < mLastOffset) {
                    mTransformer.setDirection(CardFlowTransformer.D_LEFT);
                } else {
                    mTransformer.setDirection(CardFlowTransformer.D_RIGHT);
                }
                mLastOffset = positionOffset;
            }
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            isSwitching = state != ViewPager.SCROLL_STATE_IDLE;
        }
    }

    void initExpandRate() {
        post(new Runnable() {
            @Override
            public void run() {
                mRate = DimenUtils.getScreenWidth(getContext().getApplicationContext()) * 1.00f / getWidth();
            }
        });
    }

    private void initSwitchSpeed(float scrollFactor) {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = viewpager.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);

            mScroller = new ScrollerCustomDuration(getContext(),
                    (Interpolator) interpolator.get(null));
            mScroller.setScrollFactor(scrollFactor);
            scroller.set(this, mScroller);
        } catch (Exception e) {

        }
    }

    public static class ScrollerCustomDuration extends Scroller {

        private double mScrollFactor = DEFAULT_SWITCH_TIME / DEFAULT_BASE_SWITCH_TIME;

        public ScrollerCustomDuration(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, (int) (duration * mScrollFactor));
        }

        public void setScrollFactor(double mScrollFactor) {
            this.mScrollFactor = mScrollFactor;
        }
    }

    public interface OnExpandStateListener {
        void onExpand(View page, int position);

        void onShrink(View page, int position);
    }

    public void setOnExpandStateListener (OnExpandStateListener onExpandStateListener) {
        this.onExpandStateListener = onExpandStateListener;
    }
}