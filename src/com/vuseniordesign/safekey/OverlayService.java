package com.vuseniordesign.safekey;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.RelativeLayout;



public class OverlayService extends Service{
	
	 ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

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
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                -1,
                -1,
                0, 
                (LockScreen.getNavDim()),
                 WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, 
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
        
        
       
        //params.dimAmount=0.7F;
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(oView, params);
        
     
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
        	public void run() {
        		if (!LockScreen.isActive){
        			 Intent i = new Intent(OverlayService.this, LockScreen.class);
        		        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        		        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        		        OverlayService.this.startActivity(i); 
        		}
        	}
        }, 0, 1, TimeUnit.SECONDS);
        
       
      /*oView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
				return false;
                    // event.getAction() prints 4 and not able to get up or down event
               }

           }); */

       
    }
    
   
     
    @Override
    public void onDestroy() {            
        super.onDestroy();
        if (scheduleTaskExecutor != null){
        	scheduleTaskExecutor.shutdown();
        }
        if(oView!=null){
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.removeView(oView);
        }
    }
    
   
	

}
                 