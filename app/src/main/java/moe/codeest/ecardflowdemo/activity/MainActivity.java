package moe.codeest.ecardflowdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import moe.codeest.ecardflowdemo.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatButton btnCard, btnLayout, btnLayoutBlur, btnLayoutMove;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCard = (AppCompatButton) findViewById(R.id.btn_card);
        btnLayout = (AppCompatButton) findViewById(R.id.btn_layout);
        btnLayoutBlur = (AppCompatButton) findViewById(R.id.btn_layout_blur);
        btnLayoutMove = (AppCompatButton) findViewById(R.id.btn_layout_move);

        btnCard.setOnClickListener(this);
        btnLayout.setOnClickListener(this);
        btnLayoutBlur.setOnClickListener(this);
        btnLayoutMove.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnCard) {
            Intent it = new Intent(this, CardFlowActivity.class);
            startActivity(it);
        } else if (v == btnLayout) {
            Intent it = new Intent(this, CardFlowLayoutActivity.class);
            it.putExtra("mode", CardFlowLayoutActivity.MODE_DEFAULT);
            startActivity(it);
        } else if (v == btnLayoutBlur) {
            Intent it = new Intent(this, CardFlowLayoutActivity.class);
            it.putExtra("mode", CardFlowLayoutActivity.MODE_BLUR);
            startActivity(it);
        } else {
            Intent it = new Intent(this, CardFlowLayoutActivity.class);
            it.putExtra("mode", CardFlowLayoutActivity.MODE_MOVE);
            startActivity(it);
        }
    }
}
