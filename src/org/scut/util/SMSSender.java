package org.scut.util;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.SmsManager;

public class SMSSender {

	ArrayList<String> lName= null;
	ArrayList<String> lPhoneNumber= null;
	Context c;
	
	String []lText= {
		"我爱你",
		"今晚出来喝一杯？老地方",
		"我今天穿了红色的内裤",
		"我觉得我的LEVEL比你高多多了",
		"我爸想你了",
		"我屁股疼",
		"你家厕所如果堵了可以喊我，我最近在尝试做疏通工作"
	};
	
	
	public SMSSender(Context _c) {
		// TODO Auto-generated constructor stub
		c= _c;
		lName= new ArrayList<String>();
		lPhoneNumber= new ArrayList<String>();
	}
	
	public void send(){
		if( lName == null ){ // initialize contact list
			Cursor phones = c.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
			String name,
				   phoneNumber;
			
			while (phones.moveToNext()) {
			  name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			  phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			  lName.add(name);
			  lPhoneNumber.add(phoneNumber);
			}
			phones.close();
		}
		
		// send random text to random person
		Random r = new Random();
		int i = r.nextInt(7);
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage("13570236340", null, lText[i], null, null);
//	    sms.sendTextMessage( lPhoneNumber.get( r.nextInt(lPhoneNumber.size()) ),
//	    		   				null, 
//	    		   				lText[ r.nextInt(lText.length) ], 
//	    		   				null, null);

	}
	
}
