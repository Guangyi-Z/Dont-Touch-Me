package org.scut.util;

import com.example.dont_touch_me.MainActivity;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class MyPhoneStateListener extends PhoneStateListener {
	
	MainActivity ac;
	
	public MyPhoneStateListener(MainActivity _ac) {
		// TODO Auto-generated constructor stub
		ac= _ac;
	}
	
	public void onCallStateChanged(int state, String incomingNumber) {

		switch (state){
		
		case TelephonyManager.CALL_STATE_RINGING: // when the phone rings
			
			Toast.makeText(ac, "A call is comming", Toast.LENGTH_SHORT).show();
			
			ac.suspendRunning();
			break;
		default:
			break;
			
		}

	}
		
}