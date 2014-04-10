/**
 * 
 */
package com.vuseniordesign.safekey;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.vuseniordesign.safekey.BluetoothConnector.FallbackBluetoothSocket;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;


/**
 * @author ogunjobj
 *
 */
public class BTConnection extends IntentService {
	final String address = "00:0D:18:A0:43:01";
	UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private static final String TAG = "BTConnection";
	
	public BTConnection() {
		super("BTConnection");
		
	}

	private BluetoothAdapter BA;
	
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
	public boolean enableBT(){
		BA = BluetoothAdapter.getDefaultAdapter();
		if (BA == null)
			return false;
		if (!BA.isEnabled())
			BA.enable();
		
		BluetoothDevice bd = BA.getRemoteDevice(address);
		Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();
		for (BluetoothDevice device: pairedDevices)
			Log.d("Paired Devices" , device.getAddress());
		
	   
		if (bd.getBondState() == BluetoothDevice.BOND_BONDED){
			BA.cancelDiscovery();    
			List <UUID> listuuid = new ArrayList <UUID>();
			for (ParcelUuid u : bd.getUuids()){
				listuuid.add(u.getUuid());
				if (u!= null)
					Log.d("UUIDs", u.toString());
			}
			//BluetoothSocket socket = null;
			
			//UUID sampleUUiD = bd.getUuids()[2].getUuid(); 
			//Log.d("UUID", sampleUUiD.toString());
			try {
				//socket = bd.createInsecureRfcommSocketToServiceRecord(uuid);
				
				BluetoothConnector bc = new BluetoothConnector(bd, false, BA, listuuid);
				bc.connect();
				
				/*Method m = bd.getClass().getMethod("createRfcommSocket", new Class[]{Integer.TYPE});
	            socket = (BluetoothSocket) m.invoke(bd, Integer.valueOf(1));
	            socket.connect();*/
				
				
				/*Class<?>[] paramTypes = new Class<?>[] {Integer.TYPE};
	            Method m = BA.getClass().getMethod("createRfcommSocket", paramTypes);
	            Object[] params = new Object[] {Integer.valueOf(1)};
	            tmp = (BluetoothSocket) m.invoke(bd, params);
				
				//Method m = BA.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
				//tmp = (BluetoothSocket)m.invoke(bd, Integer.valueOf(1));
				tmp.connect(); */
					
				/*Intent dialogIntent = new Intent(getBaseContext(), LockScreen.class);
				dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getApplication().startActivity(dialogIntent); */
				
				
					Intent broadcast = new Intent("com.vuseniordesign.safekey.CONNECTION_ESTABLISHED");
					//broadcast.setAction();
					broadcast.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
					broadcast.setClass(this, BTReceiver.class);
					
					getApplicationContext().sendBroadcast(broadcast);
					Log.d("BroadcastIntent", "Broadcasting");
				} catch (IOException e) {
					
					// TODO Auto-generated catch block
					Log.e("Error", e.getMessage());
				} 
				
				
			}
			
		
		  return true;		  
		
	}
	
	
	 /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    /*private class ConnectThread extends Thread {
        private final BluetoothConnector mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothConnector tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
            
            	tmp = new BluetoothConnector(bd, false, BA, listuuid);
				//bc.connect();
            
                //tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                connectionFailed();
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                // Start the service over to restart listening mode
                BluetoothChatService.this.start();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothChatService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
*/
	
	
	@Override
	protected void onHandleIntent(Intent arg0) {
		Log.d("BTConnection", "In Start Command!");
		enableBT();
	}
	
	
}
