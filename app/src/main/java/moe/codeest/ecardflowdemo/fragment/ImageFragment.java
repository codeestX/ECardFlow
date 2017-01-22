package moe.codeest.ecardflowdemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import moe.codeest.ecardflowdemo.R;

/**
 * Created by codeest on 2017/1/17.
 */

public class ImageFragment extends Fragment{

    private TextView tvDes;
    private ImageView ivImg;

    public static ImageFragment newInstance(String str, int res) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString("str", str);
        args.putInt("res", res);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.image_pager, null);
        ivImg = (ImageView) mView.findViewById(R.id.image);
        tvDes = (TextView) mView.findViewById(R.id.title);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivImg.setImageResource(getArguments().getInt("res"));
        tvDes.setText(getArguments().getString("str"));
    }
}
