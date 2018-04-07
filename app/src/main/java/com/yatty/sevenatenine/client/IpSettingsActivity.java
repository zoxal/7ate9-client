package com.yatty.sevenatenine.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class IpSettingsActivity extends AppCompatActivity {

    private EditText mIpEditText;
    private Button mApplyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_settings);
        mIpEditText = findViewById(R.id.et_ip);
        mApplyButton = findViewById(R.id.button_apply);

        mApplyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String ip = mIpEditText.getText().toString();
                NettyClient.getInstance().setServerIp(ip);
                finish();
            }
        });

    }

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, IpSettingsActivity.class);
        return intent;
    }
}
