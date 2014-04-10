package com.vuseniordesign.safekey;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class CallReceiver extends CustomPhoneStateListener{
	
	@Override
	protected void onIncomingCallStarted(String number, Date start) {
		//turn off screen's light so that the user will not be distracted
		//ensure that the lockscreen stays on top
		Log.d("Call", "Incoming");
		if (LockScreen.enabled){
			
			
			new Handler().postDelayed(new Runnable() {

				//Implement slight delay and resume lock screen
			     @Override
			     public void run() {
			         // TODO Auto-generated method stub
			         Intent intent = new Intent(getContext(), LockScreen.class);
			         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP 
			        		 |Intent.FLAG_ACTIVITY_SINGLE_TOP );
			         getContext().startActivity(intent);
			     }
			 }, 2000);
		}
	}
	

	@Override
	protected void onOutgoingCallStarted(String number, Date start) {
		if (LockScreen.enabled){
			final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
			tg.startTone(ToneGenerator.TONE_PROP_BEEP);
		}
	}

	@Override
	protected void onIncomingCallEnded(String number, Date start, Date end) {
	}

	@Override
	protected void onOutgoingCallEnded(String number, Date start, Date end) {
	
	}

	@Override
	protected void onMissedCall(String number, Date start) {
		//check if contact exists
		
		if (contactExists(getContext(), number))
			sendSMS(number);
	}
	
	
	public void sendSMS(String number) {
		//the message is hardcoded for now

	    String message = "I apologize for missing your call, I'm currently driving.";

	    SmsManager smsManager = SmsManager.getDefault();
	    smsManager.sendTextMessage(number, null, message, null, null);
	}
	
	public boolean contactExists(Context context, String number) {
		/// number is the phone number
		Uri lookupUri = Uri.withAppendedPath(
		PhoneLookup.CONTENT_FILTER_URI, 
		Uri.encode(number));
		String[] mPhoneNumberProjection = { PhoneLookup._ID, PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME };
		Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
		try {
		   if (cur.moveToFirst()) {
		      return true;
		}
		} finally {
		if (cur != null)
		   cur.close();
		}
		return false;
		}
}