package com.sunming.gamefactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends Activity {
    private AdView [] mAdView = new AdView[2]; //광고 담을 변수
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingAd(); //광고 호출 함수

    }

    /**
     * 광고 셋팅해주는 함수
     */
    public void settingAd(){
        //상단 배너 광고
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView[0] = (AdView) findViewById(R.id.adView);
        mAdView[0].loadAd(adRequest);

        //하단 배너 광고
        mAdView[1] = (AdView) findViewById(R.id.adView1);
        mAdView[1].loadAd(adRequest);

    }


}
