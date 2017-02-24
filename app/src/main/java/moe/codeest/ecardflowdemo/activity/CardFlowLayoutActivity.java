package moe.codeest.ecardflowdemo.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import moe.codeest.ecardflow.ECardFlowLayout;
import moe.codeest.ecardflow.mode.BlurAnimMode;
import moe.codeest.ecardflow.mode.CrossMoveAnimMode;
import moe.codeest.ecardflow.mode.DefaultAnimMode;
import moe.codeest.ecardflow.mode.MoveAnimMode;
import moe.codeest.ecardflow.mode.ScaleAnimMode;
import moe.codeest.ecardflow.provider.DrawableImageProvider;
import moe.codeest.ecardflow.util.DimenUtils;
import moe.codeest.ecardflowdemo.R;
import moe.codeest.ecardflowdemo.adapter.ImageAdapter;
import moe.codeest.ecardflowdemo.fragment.ImageFragment;
import moe.codeest.ecardflowdemo.support.ZoomOutPageTransformer;

/**
 * Created by codeest on 2017/1/17.
 */

public class CardFlowLayoutActivity extends AppCompatActivity {

    public final static int MODE_DEFAULT = 0;
    public final static int MODE_BLUR = 1;
    public final static int MODE_MOVE = 2;
    public final static int MODE_SCALE = 3;
    public final static int MODE_CROSS_MOVE = 4;
    public final static String ANIM_MODE = "mode";

    private ECardFlowLayout mVPLayout;
    private ViewPager mViewPager;
    private ImageAdapter mAdapter;
    private List<ImageFragment> mList = new ArrayList<>();
    private String[] str = {"Pixiv","点兔","宮坂","君名"};
    private int[] res = {R.mipmap.cover, R.mipmap.img1, R.mipmap.img2, R.mipmap.img3};
    private int mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardflow_layout);
        mMode = getIntent().getIntExtra(ANIM_MODE, MODE_DEFAULT);
        init();
    }

    private void init() {
        mVPLayout = (ECardFlowLayout) findViewById(R.id.ecardflow_layout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        switch (mMode) {
            case 0:
                mVPLayout.setAnimMode(new DefaultAnimMode());
                break;
            case 1:
                mVPLayout.setAnimMode(new BlurAnimMode());
                break;
            case 2:
                mVPLayout.setAnimMode(new MoveAnimMode());
                break;
            case 3:
                mVPLayout.setAnimMode(new ScaleAnimMode());
                break;
            case 4:
                mVPLayout.setAnimMode(new CrossMoveAnimMode());
                break;
        }
        mVPLayout.setImageProvider(new DrawableImageProvider(this, res, DimenUtils.getScreenWidth(getApplicationContext()), DimenUtils.getScreenHeight(getApplicationContext())));
        for (int i = 0; i < 4; i++) {
            mList.add(ImageFragment.newInstance(str[i], res[i]));
        }

        mAdapter = new ImageAdapter(getSupportFragmentManager(), mList);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mViewPager.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVPLayout.onDestroy();
    }
}
