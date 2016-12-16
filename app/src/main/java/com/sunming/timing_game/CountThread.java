package com.sunming.timing_game;

/**
 * Created by minkyujo on 2016. 12. 3..
 */

public class CountThread extends Thread {
    private MainActivity owner;
    boolean falg = true;
    int speed = 0;

    public CountThread( MainActivity obj, int speed){
        owner = obj;
        this.speed = speed;
    }

    public void run() {
        while( falg ) {
            owner.getHandler().post( owner );

            try {
                sleep(speed);
            }catch(Exception e) { }
        }
    }
}
