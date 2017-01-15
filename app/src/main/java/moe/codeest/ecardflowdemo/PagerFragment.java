package moe.codeest.ecardflowdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;

/**
 * Created by codeest on 2017/1/9.
 */

public class PagerFragment extends Fragment{

    private ImageView ivBack;
    private RecyclerView rvContent;
    private TextView tvBtn;

    private ItemAdapter mAdapter;

    private boolean isReadyShrink;
    private int mScrollDirection = 0;

    public static PagerFragment newInstance() {
        return new PagerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_pager, null);
        ivBack = (ImageView) mView.findViewById(R.id.iv_back);
        rvContent = (RecyclerView) mView.findViewById(R.id.rv_content);
        tvBtn = (TextView) mView.findViewById(R.id.tv_btn);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivBack.setVisibility(View.GONE);
        mAdapter = new ItemAdapter();
        rvContent.setLayoutManager(new LinearLayoutManager(getContext()));
        final FadeInUpAnimator mAnim = new FadeInUpAnimator();
        mAnim.setInterpolator(new FastOutSlowInInterpolator());
        rvContent.setItemAnimator(mAnim);
        rvContent.getItemAnimator().setAddDuration(400);
        rvContent.addItemDecoration(new ItemAdapter.DefaultItemDecoration());
        rvContent.setAdapter(mAdapter);
        rvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0) {
                    mScrollDirection = -1;
                } else if (dy > 0) {
                    mScrollDirection = 1;
                }
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && recyclerView.getChildAt(0).getY() >= dp2px(getContext(), 18)) {
                    isReadyShrink = true;
                }
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING && recyclerView.getChildAt(0).getY() >= dp2px(getContext(), 18) && isReadyShrink && mScrollDirection < 0) {
                    isReadyShrink = false;
                    mScrollDirection = 0;
                    ((OnPageListener) getActivity()).onShrink();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        tvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Check-In",Toast.LENGTH_SHORT).show();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isReadyShrink = false;
                mScrollDirection = 0;
                ((OnPageListener) getActivity()).onShrink();
            }
        });
    }

    public void loadMoreData() {
        ivBack.setVisibility(View.VISIBLE);
        mAdapter.setItemCount(4);
        mAdapter.notifyItemRangeInserted(1, 3);
        pageExpandAnim();
    }

    public void hideMoreData() {
        ivBack.setVisibility(View.GONE);
        mAdapter.setItemCount(1);
        mAdapter.notifyItemRangeRemoved(1, 3);
        pageShrinkAnim();
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void pageExpandAnim() {
        rvContent.animate().scaleX(0.9f).setDuration(700).start();
    }

    private void pageShrinkAnim() {
        rvContent.animate().scaleX(1).setDuration(700).start();
    }

    public interface OnPageListener {
        void onShrink();
    }
}
