package listener;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class TeleListener extends PhoneStateListener {
	
	boolean isAfterRinging= false;
	
	public TeleListener(){
		
	}
	
	public void onCallStateChanged(int state, String incomingNumber) {
		super.onCallStateChanged(state, incomingNumber);
		
		switch (state) {
		case TelephonyManager.CALL_STATE_IDLE:
			// CALL_STATE_IDLE;
			if( isAfterRinging ){
				// restore app info
			}
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			// CALL_STATE_OFFHOOK;
			break;
		case TelephonyManager.CALL_STATE_RINGING:
			// CALL_STATE_RINGING
			// save app info
			// unregister sensor listener
			// remaining time
			// button changed to continue
			// set period
			// 
			
			
			isAfterRinging= true;
			break;
		default:
			break;
		}
	}
	
}