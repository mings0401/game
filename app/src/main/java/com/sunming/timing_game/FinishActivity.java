package com.sunming.timing_game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

public class FinishActivity extends Activity {
    String topRecord = "", currentRecord = "";
    private TextView topRecordText, currentRecordText;
    private AdView mAdView;
    private KakaoLink mKakaoLink;
    private KakaoTalkLinkMessageBuilder mKakaoTalkLinkMessageBuilder;
    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        topRecordText = (TextView) findViewById(R.id.topRecordTxt);
        currentRecordText = (TextView) findViewById(R.id.currentRecordTxt);

        Intent intent = getIntent();
        topRecord = intent.getStringExtra("topRecord");
        currentRecord = intent.getStringExtra("currentRecord");

        topRecordText.setText(topRecord);
        currentRecordText.setText(currentRecord);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView = (AdView) findViewById(R.id.finish_top_adView);
        mAdView.loadAd(adRequest);

        findViewById(R.id.restartGameBtn).setOnClickListener(mClickListener);
        findViewById(R.id.kakaoLinkBtn).setOnClickListener(mClickListener);


        backPressCloseHandler = new BackPressCloseHandler(this);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.restartGameBtn:
                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                    break;
                case R.id.kakaoLinkBtn:
                    InitKakaoLink();
                    SendKakaoMessage();
                    break;
            }
        }
    };

    private void InitKakaoLink(){
        try{
            mKakaoLink = KakaoLink.getKakaoLink(this);
            mKakaoTalkLinkMessageBuilder = mKakaoLink.createKakaoTalkLinkMessageBuilder();
        }catch(KakaoParameterException e){
            e.printStackTrace();
        }
    }
     void SendKakaoMessage(){
        try{
            mKakaoTalkLinkMessageBuilder.addText("Stage"+currentRecord+" 달성!");
            mKakaoTalkLinkMessageBuilder.addImage("http://postfiles4.naver.net/MjAxNjEyMTZfNTEg/MDAxNDgxODc0OTk3NDUz.9AdVzWLZUHH5iBPnCnixhvuCEjVI6tnQV3XhhwZ2s34g.Wrhu49vVv7ZnLKRs9_mHcfKMtx-_SNWQyx3cU1t1fkQg.PNG.mings0401/timing.png?type=w1", 128, 128);
            mKakaoTalkLinkMessageBuilder.addAppButton("Timing Game 바로가기",
                    new AppActionBuilder()
                            .setAndroidExecuteURLParam("target=main")
                            .setIOSExecuteURLParam("target=main", AppActionBuilder.DEVICE_TYPE.PHONE).build());
            mKakaoLink.sendMessage(mKakaoTalkLinkMessageBuilder, this);
        }catch(KakaoParameterException e){
            e.printStackTrace();
        }
    }
}
