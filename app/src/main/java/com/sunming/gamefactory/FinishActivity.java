package com.sunming.gamefactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class FinishActivity extends Activity {
    private TextView topRecordText, currentRecordText;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        topRecordText = (TextView) findViewById(R.id.topRecordTxt);
        currentRecordText = (TextView) findViewById(R.id.currentRecordTxt);

        Intent intent = getIntent();
        String topRecord = intent.getStringExtra("topRecord");
        String currentRecord = intent.getStringExtra("currentRecord");

        topRecordText.setText(topRecord);
        currentRecordText.setText(currentRecord);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView = (AdView) findViewById(R.id.finish_top_adView);
        mAdView.loadAd(adRequest);

        findViewById(R.id.restartGameBtn).setOnClickListener(mClickListener);

    }

    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.restartGameBtn:
                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                    break;
            }
        }
    };

}
