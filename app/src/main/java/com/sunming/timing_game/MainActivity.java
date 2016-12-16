package com.sunming.timing_game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MainActivity extends Activity implements Runnable {
    public static Context mContext; //다른 activity에서 함수 호출되도록
    private AdView [] mAdView = new AdView[2]; //광고 담을 변수
    private InterstitialAd interstitial; //광고 담을 변수
    private Handler handler = null; //thread handler
    private TextView missionNumberTextView, countNumberTextView, stateTextView, myTopStageTextView;
    //missionNumber : 왼쪽 상단 mission 숫자 maxNumber : 미션 숫자 중 최대 숫자, currentNumber : 현재 바뀌고있는 숫자, switchNumber : 1또는 -1로 쓰레드 숫자가 늘어나거나 줄어드는거 조절
    //stageNumer : 현재 스테이지 숫자, speed : 숫자 올라가는 속도
    private int missionNumber = 0, maxNumber = 10, currentNumber = 0, switchNumber = 1, stageNumer = 0, speed = 500;
    private CountThread thread; //쓰레드 변수
    private BackPressCloseHandler backPressCloseHandler;

    //DB 관련 변수
    private MySQLiteOpenHelper helper;
    String dbName = "click50.db";
    int dbVersion = 1; // 데이터베이스 버전
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mContext = this;
        handler = new Handler();

        settingAd(); //광고 호출 함수

        //TextView 변수 초기화
        missionNumberTextView = (TextView) findViewById(R.id.mission_txt); //좌측 상단 미션 숫자
        countNumberTextView = (TextView) findViewById(R.id.count_txt); //가운데 쓰레드로 변경될 숫자
        stateTextView = (TextView) findViewById(R.id.stage_txt); // 상단 가운데 Stage Number
        myTopStageTextView = (TextView) findViewById(R.id.mytopscore_txt);

        // sqLite3 : 모바일 용으로 제작된 경량화 DB
        helper = new MySQLiteOpenHelper(
                this,  // 현재 화면의 제어권자
                dbName,// db 이름
                null,  // 커서팩토리-null : 표준커서가 사용됨
                dbVersion);       // 버전

        try {
            db = helper.getWritableDatabase(); // 읽고 쓸수 있는 DB
            //db = helper.getReadableDatabase(); // 읽기 전용 DB select문
        } catch (SQLiteException e) {
            e.printStackTrace();
            finish(); // 액티비티 종료
        }

        //내 최고 기록 셋팅
        myTopStageTextView.setText("My Top Stage : " + getMyyMaxStage());

        Intent startDailogIntent = new Intent(getApplicationContext(), StartDialogActivity.class);
        startActivity(startDailogIntent);

        findViewById(R.id.count_layout).setOnClickListener(mClickListener);
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
                case R.id.count_layout:
                    counterNumberClick();
                    break;
            }
        }
    };

    /**
     * Count Layout 부분 클릭 이벤트
     */
    public void counterNumberClick(){
        thread.falg = false;

        if (countNumberTextView.getText().toString().equals(missionNumberTextView.getText().toString())) {
            stageNumer++;
            String s = "stage" + (stageNumer + 1);

            stateTextView.setText(s);

            if (stageNumer > 0 && stageNumer <= 7)
                maxNumber = 10;

            else if (stageNumer > 7 && stageNumer <= 12)
                maxNumber = 20;

            else if (stageNumer > 12 && stageNumer <= 16)
                maxNumber = 30;

            else if (stageNumer > 16 && stageNumer <= 19)
                maxNumber = 40;

            else
                maxNumber = 50;

            if (speed > 400)
                speed -= 15;
            else if (speed > 300)
                speed -= 20;
            else if (speed > 200)
                speed -= 30;
            else if (speed > 95)
                speed -= 40;
            else if (speed > 50)
                speed -= 5;
            else if (speed>20)
                speed -= 1;
            else
                speed = 20;
            startGame();
        } else { //game over 됫을때 ...
            insertMyStageToDB();
            String topRecord = "" + getMyyMaxStage();
            String currentRecord = "" + stageNumer;
            Intent finishActivity = new Intent(getApplicationContext(), FinishActivity.class);
            finishActivity.putExtra("topRecord", topRecord);
            finishActivity.putExtra("currentRecord", currentRecord);
            startActivity(finishActivity);
            showInterstitial();
            finish();
        }
    }

    /**
     * MainActivity의 handler 넘겨주는 함
     * @return
     */
    public Handler getHandler() {
        return handler;
    }

    /**
     * DB에서 현재 최고 기록 가져오기
     */
    public int getMyyMaxStage() {
        Cursor c = db.rawQuery("select MAX(stage) from myrecords;", null);
        int stage = 0;
        while(c.moveToNext()) {
            stage = c.getInt(0);
        }
        return stage;
    }

    /**
     * DB에서 현재 Stage 집어 넣기
     */
    public void insertMyStageToDB () {
        db.execSQL("insert into myrecords values("+stageNumer+");");
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

        thread = new CountThread(this, speed);

        thread.start();
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

        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId("ca-app-pub-3628076058136751/1729796220");
        // Create ad request.
        // Begin loading your interstitial.
        interstitial.loadAd(adRequest);

    }

    /**
     * 전면 광고 실행시크는 함수
     */
    private void showInterstitial() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (interstitial.isLoaded()) {
                    interstitial.show();
                }
            }
        });
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
