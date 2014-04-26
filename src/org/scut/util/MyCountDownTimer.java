package org.scut.util;

import com.example.dont_touch_me.MainActivity;

import android.os.CountDownTimer;

public class MyCountDownTimer extends CountDownTimer{

	long millisInFuture, 
		 countDownInterval;
	long millisUntilFinished;
	MainActivity ac;
	
	public MyCountDownTimer(MainActivity _ac, long _millisInFuture, long _countDownInterval){
		super(_millisInFuture, _countDownInterval);
		
		ac= _ac;
		millisInFuture= _millisInFuture;
		countDownInterval= _countDownInterval;
	}

	public void onTick(long _millisUntilFinished) {
		millisUntilFinished= _millisUntilFinished;
		ac.refreshTimeRecorder(_millisUntilFinished);
	}

	public void onFinish() { // when time is up
		ac.stopRunning();
	}
	
	public long getRemainingTime(){
		return millisUntilFinished;
	}
	
}