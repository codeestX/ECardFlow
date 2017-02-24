package moe.codeest.ecardflowdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import moe.codeest.ecardflowdemo.R;

import static moe.codeest.ecardflowdemo.activity.CardFlowLayoutActivity.ANIM_MODE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatButton btnCard, btnLayout, btnLayoutBlur, btnLayoutMove, btnLayoutScale, btnLayoutCrossMove;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCard = (AppCompatButton) findViewById(R.id.btn_card);
        btnLayout = (AppCompatButton) findViewById(R.id.btn_layout);
        btnLayoutBlur = (AppCompatButton) findViewById(R.id.btn_layout_blur);
        btnLayoutMove = (AppCompatButton) findViewById(R.id.btn_layout_move);
        btnLayoutScale = (AppCompatButton) findViewById(R.id.btn_layout_scale);
        btnLayoutCrossMove = (AppCompatButton) findViewById(R.id.btn_layout_cross);

        btnCard.setOnClickListener(this);
        btnLayout.setOnClickListener(this);
        btnLayoutBlur.setOnClickListener(this);
        btnLayoutMove.setOnClickListener(this);
        btnLayoutScale.setOnClickListener(this);
        btnLayoutCrossMove.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnCard) {
            Intent it = new Intent(this, CardFlowActivity.class);
            startActivity(it);
        } else if (v == btnLayout) {
            Intent it = new Intent(this, CardFlowLayoutActivity.class);
            it.putExtra(CardFlowLayoutActivity.ANIM_MODE, CardFlowLayoutActivity.MODE_DEFAULT);
            startActivity(it);
        } else if (v == btnLayoutBlur) {
            Intent it = new Intent(this, CardFlowLayoutActivity.class);
            it.putExtra(CardFlowLayoutActivity.ANIM_MODE, CardFlowLayoutActivity.MODE_BLUR);
            startActivity(it);
        } else if (v == btnLayoutMove) {
            Intent it = new Intent(this, CardFlowLayoutActivity.class);
            it.putExtra(CardFlowLayoutActivity.ANIM_MODE, CardFlowLayoutActivity.MODE_MOVE);
            startActivity(it);
        } else if (v == btnLayoutScale) {
            Intent it = new Intent(this, CardFlowLayoutActivity.class);
            it.putExtra(CardFlowLayoutActivity.ANIM_MODE, CardFlowLayoutActivity.MODE_SCALE);
            startActivity(it);
        } else {
            Intent it = new Intent(this, CardFlowLayoutActivity.class);
            it.putExtra(CardFlowLayoutActivity.ANIM_MODE, CardFlowLayoutActivity.MODE_CROSS_MOVE);
            startActivity(it);
        }
    }
}
