package com.sunming.gamefactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends Activity implements Runnable {
    public static Context mContext; //다른 activity에서 함수 호출되도록
    private AdView [] mAdView = new AdView[2]; //광고 담을 변수
    private Handler handler = null; //thread handler
    private TextView missionNumberTextView, countNumberTextView, stateTextView;
    private int missionNumber = 0, maxNumber = 10, currentNumber = 0, switchNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        handler = new Handler();

        settingAd(); //광고 호출 함수

        //TextView 변수 초기화
        missionNumberTextView = (TextView) findViewById(R.id.mission_txt); //좌측 상단 미션 숫자
        countNumberTextView = (TextView) findViewById(R.id.count_txt); //가운데 쓰레드로 변경될 숫자
        stateTextView = (TextView) findViewById(R.id.stage_txt); // 상단 가운데 Stage Number

        Intent startDailogIntent = new Intent(getApplicationContext(), StartDialogActivity.class);
        startActivity(startDailogIntent);

    }

    /**
     * 게임 시작
     */
    public void startGame(){
        String missionNumberToString = "";
        missionNumber = (int) (Math.random() * (maxNumber - 5 + 1)) + 5; //random number
        missionNumberToString += missionNumber;
        missionNumberTextView.setText(missionNumberToString);

        currentNumber = 0;
        countNumberTextView.setText("0");

        CountThread thread = new CountThread(this, 1000);

        thread.start();
    }

    /**
     * MainActivity의 handler 넘겨주는 함
     * @return
     */
    public Handler getHandler() {
        return handler;
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

    /**
     * countThread 돌리는 것
     */
    @Override
    public void run() {
        if (currentNumber == maxNumber) switchNumber = -1;
        else if (currentNumber == 0) switchNumber = 1;

        currentNumber += switchNumber;

        String s = "";

        s += currentNumber;
        countNumberTextView.setText(s);
    }
}
