package com.example.dont_touch_me;

import java.util.HashMap;
import java.util.Random;

import org.scut.util.MyCountDownTimer;
import org.scut.util.MyPhoneStateListener;
import org.scut.util.SMSSender;
import org.scut.util.Util;

import com.example.dont_touch_me.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener,
		SensorEventListener {

	Vibrator mVibrator;
	int i = 20;

	SensorManager mSensorManager;
	Sensor mProximity;
	MyCountDownTimer mCountDownTimer;
	TelephonyManager mTelephonyManager;
	SMSSender sms;

	Button mStartButton;
	TextView mTimeRecorder;
	TimePicker mTimePicker;

	boolean isRunning = false;
	boolean isSuspend = false;
	boolean isFail = false;
	int periodInMinute, periodInHour;
	final int readyInSeconds = 5;
	long[] vibratePattern = { 5000, 1000, 5000, 1000, 5000, 1000, 5000, 1000,
			5000, 1000, };

	MediaPlayer mediaPlayer;
	HashMap<Integer, Integer> map;
	AudioManager audioManager;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {

		case R.id.button_start:
			if (isRunning)
				return;
			mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			if ((mProximity = mSensorManager
					.getDefaultSensor(Sensor.TYPE_PROXIMITY)) != null) { // test
																			// proximity
																			// existence
				// Success! There's a proximity sensor.
				mStartButton.setText("Put Me in Pocket!");
				Toast.makeText(
						this,
						"Timer will begin in " + readyInSeconds
								+ " seconds ...", Toast.LENGTH_SHORT).show();

				if (isSuspend) {
					mCountDownTimer = new MyCountDownTimer(MainActivity.this,
							mCountDownTimer.getRemainingTime(), 1000);
					isSuspend = false;
				} else {
					mCountDownTimer = new MyCountDownTimer(MainActivity.this,
							(periodInHour * 60 + periodInMinute) * 60 * 1000,
							1000);
					isRunning = true;
				}
				new Handler().postDelayed(new Runnable() { // delay for
							// readyInSeconds

							@Override
							public void run() {
								// TODO Auto-generated method stub
								mSensorManager.registerListener(
										MainActivity.this, mProximity,
										SensorManager.SENSOR_DELAY_NORMAL); // sample
																			// rate
																			// of
																			// every
																			// 0.6s

								// set up TelephonyListener
								mTelephonyManager = (TelephonyManager) MainActivity.this
										.getSystemService(Context.TELEPHONY_SERVICE);
								// to receive notification about the changes in
								// telephony states.
								mTelephonyManager.listen(
										new MyPhoneStateListener(
												MainActivity.this),
										MyPhoneStateListener.LISTEN_CALL_STATE);

								mCountDownTimer.start();
							}

						}, readyInSeconds * 1000);
			} else {
				// Failure! No Proximity sensor
				Toast.makeText(this,
						"Sorry, your device don't support Proximity sensor",
						Toast.LENGTH_LONG).show();
			}

			break;
		default:
			break;
		}
	}

	public void refreshTimeRecorder(long millisUntilFinished) {
		long secUntilFinished = millisUntilFinished / (1000);
		mTimeRecorder.setText(new StringBuilder().append("Time Remaining: ")
				.append(secUntilFinished / (60 * 60)).append(":")
				.append(Util.pad((int) ((secUntilFinished / 60 % 60))))
				.append(":")
				.append(Util.pad((int) ((secUntilFinished % (60 * 60)) % 60)))
				.toString());
	}

	public void stopRunning() {
		isRunning = false;
		mStartButton.setText("Get Started!");
		mSensorManager.unregisterListener(MainActivity.this);
		mCountDownTimer.cancel();
		mTimeRecorder.setText("Set Timer");
		
		// stop music
		mediaPlayer.pause();

		if (isFail) {
			;
			
		} else {
			// make viration to notify user
			mVibrator.vibrate(vibratePattern, -1);
			
			// show congratulations dialog
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					MainActivity.this);

			alertDialogBuilder.setTitle("Congratulations!");
			alertDialogBuilder
					.setMessage("You have completed a tough journey of "
							+ periodInHour + " Hour and " + periodInMinute
							+ "Minutes!\nYour are sooooooo GREAT!");
			// set positive button: Yes message
			alertDialogBuilder.setPositiveButton("Go Back!",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			// set negative button: No message
			alertDialogBuilder.setNegativeButton("See You~",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// cancel the alert box and put a Toast to the user
							MainActivity.this.finish();
						}
					});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}

	}

	public void suspendRunning() {
		mSensorManager.unregisterListener(MainActivity.this);
		mCountDownTimer.cancel();
		isRunning = false;
		isSuspend = true;
		mStartButton.setText("Continue");
	}

	public void punishment() {
		isFail = true;
		Toast.makeText(this, "Don't Touch Me!", Toast.LENGTH_SHORT).show();

		// SMS
//		sms.send();

		// Play sound
		playMusic();
		
		// show Failure dialog
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				MainActivity.this);

		alertDialogBuilder.setTitle("Sorry for you!");
		alertDialogBuilder
		.setMessage("You failed to complete this tough journey of "
				+ periodInHour + " Hour and " + periodInMinute
				+ "Minutes!\nDon't give uppppppp!");
		// set positive button: Yes message
		alertDialogBuilder.setPositiveButton("Try again!",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				MainActivity.this.stopRunning();
				dialog.cancel();
			}
		});
		// set negative button: No message
		alertDialogBuilder.setNegativeButton("See You~",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// cancel the alert box and put a Toast to the user
				MainActivity.this.stopRunning();
				MainActivity.this.finish();
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {

			Log.i("Sensor", "values:" + event.values[0]);
			Log.i("Sensor", "range:" + event.sensor.getMaximumRange());
			if (event.values[0] < event.sensor.getMaximumRange()) {// NEAR
				// empty
				if (isFail) {
					mediaPlayer.pause();
				}
			} else {// FAR
				punishment();
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		// empty
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Init soundPool
		initMediaPlayer();
		// set up start button
		mStartButton = (Button) findViewById(R.id.button_start);
		mStartButton.setOnClickListener(this);
		mStartButton.getBackground().setAlpha(100);

		// set up time recorder
		mTimeRecorder = (TextView) findViewById(R.id.time_recorder);

		// set up time picker
		final int initHours = 0;
		final int initMinutes = 30;
		mTimePicker = (TimePicker) findViewById(R.id.time_picker);
		mTimePicker.setIs24HourView(true);
		mTimePicker.setCurrentHour(0);
		mTimePicker.setCurrentMinute(initMinutes);
		periodInMinute = initMinutes;
		periodInHour = initHours;
		mTimePicker.setOnTimeChangedListener(new OnTimeChangedListener() {

			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) { // invoked
																					// when
																					// user
																					// change
																					// the
																					// time
				// TODO Auto-generated method stub
				periodInHour = hourOfDay;
				periodInMinute = minute;
			}

		});

		// Get instance of Vibrator from current Context
		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		// SMS Sender
		sms = new SMSSender(this);

	}

	@SuppressLint("UseSparseArrays")
	public void initMediaPlayer() {
		Random r = new Random();
		int i = r.nextInt(2) % 2 + 1;
		// set up the hashmap
		map = new HashMap<Integer, Integer>();
		map.put(1, R.raw.a1);
		map.put(2, R.raw.a2);
		audioManager = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);
		
		System.out.println("isSpeakerPhoneOn:"
				+ audioManager.isSpeakerphoneOn());
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
				audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
				AudioManager.FLAG_PLAY_SOUND);
		audioManager.setMode(AudioManager.MODE_IN_CALL);
		mediaPlayer = MediaPlayer.create(this, map.get(i));
	}

	public void playMusic() {
		try {
			audioManager.setMicrophoneMute(true);
			audioManager.setSpeakerphoneOn(true);
			mediaPlayer.seekTo(0);
			System.out.println("isSpeakerPhoneOn:"
					+ audioManager.isSpeakerphoneOn());
			mediaPlayer.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mediaPlayer.start();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
