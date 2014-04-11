package com.vuseniordesign.safekey;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.RelativeLayout;



public class OverlayService extends Service{
	
	 
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	 
    RelativeLayout oView;
 
    @Override
    public void onCreate() {
        super.onCreate();
        oView = new RelativeLayout(this);   
        //oView.setBackgroundColor(Color.CYAN);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                -1,
                -1,
                0, 
                (LockScreen.getNavDim()),
                 WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY | WindowManager.LayoutParams.TYPE_SYSTEM_ALERT , 
                 WindowManager.LayoutParams.FLAG_DIM_BEHIND |40
                 | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                 WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                 PixelFormat.TRANSLUCENT);
               /* WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                //WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                //WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                //WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                WindowManager.LayoutParams.FLAG_DIM_BEHIND |
                //WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
               WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
               // | WindowManager.LayoutParams.FLAG_FULLSCREEN,
                PixelFormat.TRANSLUCENT); */
        params.gravity = Gravity.CENTER;
        
    
       
        params.dimAmount=0.7F;
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        
        wm.addView(oView, params);
        
     
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
		
    	 Intent i = new Intent(this, LockScreen.class);
         i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP |Intent.FLAG_ACTIVITY_CLEAR_TOP);
         startActivity(i); 
   	
    	return START_STICKY;
    	
    }
    
   
     
    @Override
    public void onDestroy() {            
        super.onDestroy();
        
        if(oView!=null){
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.removeView(oView);
        }
    }
    
   
	

}
                 