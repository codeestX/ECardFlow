package moe.codeest.ecardflowdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import moe.codeest.ecardflow.ECardFlow;
import moe.codeest.ecardflowdemo.adapter.PagerAdapter;
import moe.codeest.ecardflowdemo.fragment.PagerFragment;
import moe.codeest.ecardflowdemo.R;

/**
 * Created by codeest on 2017/1/21.
 */

public class CardFlowActivity extends AppCompatActivity implements PagerFragment.OnPageListener {

    private List<PagerFragment> mList = new ArrayList<>();
    private PagerAdapter mAdapter;
    private Toolbar mToolbar;
    private ECardFlow mEcardflow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardflow);
        init();
    }

    private void init() {
        mEcardflow = (ECardFlow) findViewById(R.id.ecardflow);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mList.add(PagerFragment.newInstance());
        mList.add(PagerFragment.newInstance());
        mList.add(PagerFragment.newInstance());
        mList.add(PagerFragment.newInstance());
        mList.add(PagerFragment.newInstance());

        mAdapter = new PagerAdapter(getSupportFragmentManager(), mList);
        mEcardflow.setAdapter(mAdapter);
        mEcardflow.setOnExpandStateListener(new ECardFlow.OnExpandStateListener() {
            @Override
            public void onExpand(View page, int position) {
                mList.get(position).loadMoreData();
            }

            @Override
            public void onShrink(View page, int position) {
                mList.get(position).hideMoreData();
            }
        });

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onShrink() {
        mEcardflow.shrink();
    }

    @Override
    public void onBackPressed() {
        if (mEcardflow.isExpanding()) {
            mEcardflow.shrink();
        } else {
            super.onBackPressed();
        }
    }
}
