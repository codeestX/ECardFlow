package moe.codeest.ecardflowdemo.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import moe.codeest.ecardflowdemo.R;

import static moe.codeest.ecardflowdemo.fragment.PagerFragment.dp2px;

/**
 * Created by codeest on 2017/1/13.
 */

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int DEFAULT_COUNT = 1;

    private int mCurrentItemCount = DEFAULT_COUNT;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DefaultViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mCurrentItemCount;
    }

    public void setItemCount(int mCurrentItemCount) {
        this.mCurrentItemCount = mCurrentItemCount;
    }

    public static class DefaultViewHolder extends RecyclerView.ViewHolder {

        public DefaultViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class DefaultItemDecoration extends RecyclerView.ItemDecoration{

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
            if (position > -1) {
                if (position == 0) {
                    outRect.set(0, dp2px(view.getContext(), 18), 0, 0);
                }
            }
        }
    }
}
