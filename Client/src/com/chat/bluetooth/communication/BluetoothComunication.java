package com.chat.bluetooth.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;

import com.chat.bluetooth.R;
import com.chat.bluetooth.activity.MainActivity;
import com.chat.bluetooth.util.LogUtil;


public class BluetoothComunication extends Thread {
	 
	private boolean run;
	
	private Context context;
	private Handler handler;
	
	private BluetoothSocket bluetoothSocket;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;
	
	public BluetoothComunication(Context context, Handler handler){
		this.context = context;
		this.handler = handler;
	//	sendHandler(MainActivity.MSG_BLUETOOTH, ": testing" );
		
		run = true;
	}
	
	public void setBluetoothSocket(BluetoothSocket bluetoothSocket){
		this.bluetoothSocket = bluetoothSocket;
	}
	
	@Override
	public void run() {
		 super.run();
		
		 try {
			 String nameBluetooth = bluetoothSocket.getRemoteDevice().getName();
			 dataInputStream = new DataInputStream(bluetoothSocket.getInputStream());
			 dataOutputStream = new DataOutputStream(bluetoothSocket.getOutputStream());
			
			 sendHandler(MainActivity.MSG_TOAST, context.getString(R.string.connected_sucessfully));
			 
			 while (run) {
				 if(dataInputStream.available() > 0){
					// sendHandler(MainActivity.MSG_TOAST, context."received something");
					 byte[] msg = new byte[dataInputStream.available()];
					 dataInputStream.read(msg, 0, dataInputStream.available());

					 String msgStr = new String(msg);
					 String firstCharStr = String.valueOf(msgStr.charAt(0));
					 //char c = msgStr.charAt(0);
					 char c = firstCharStr.charAt(0);

					 switch(c){
						 case '[':
							 sendHandler(MainActivity.JSON_BLUETOOTH, msgStr);
							 break;
						 default :
							 sendHandler(MainActivity.MSG_BLUETOOTH, nameBluetooth + ": " + msgStr);
							 break;
					 }

//					 if(msgStr.startsWith("["))
//						 sendHandler2(MainActivity.MSG_BLUETOOTH, nameBluetooth + ": " + msgStr);

					// sendHandler(MainActivity.MSG_BLUETOOTH, nameBluetooth + ": " + msgStr );
					 /***********************************************************************************/
				 }
			 }
		 }catch (IOException e) {
			 LogUtil.e(e.getMessage());
			 
			 stopComunication();
			 sendHandler(MainActivity.MSG_TOAST, context.getString(R.string.lost_connection));
		 }
	}

	public void sendHandler2(int what, Object object){
	handler.obtainMessage(what, object).sendToTarget();
	}
	
	public boolean sendMessageByBluetooth(String msg){
		try {
			if(dataOutputStream != null){
				dataOutputStream.write(msg.getBytes());
				dataOutputStream.flush();
				return true;
			}else{
				sendHandler(MainActivity.MSG_TOAST, context.getString(R.string.no_connection));
				return false;
			}
		} catch (IOException e) {
			LogUtil.e(e.getMessage());
			
			sendHandler(MainActivity.MSG_TOAST, context.getString(R.string.failed_to_send_message));
			return false;
		}
	}

	// Value to assign to the returned Message.what field.
	//* @param obj Value to assign to the returned Message.obj field.
	//	* @return A Message from the global message pool.
	public void sendHandler(int what, Object object){
		handler.obtainMessage(what, object).sendToTarget();
	}
	//calls sendToTarget()  in Message class
	//Sends this Message to the Handler specified by {@link #getTarget}.
	//	* Throws a null pointer exception if this field has not been set.



           
	 public void stopComunication(){ 
		try {
			run = false;
			
			if(bluetoothSocket != null){
				bluetoothSocket.close();
			}
			
			if(dataInputStream != null && dataOutputStream != null){
				dataInputStream.close();
				dataOutputStream.close();
				
				dataInputStream = null;
				dataOutputStream = null;
			}
		} catch (IOException e) {
			LogUtil.e(e.getMessage());
		}
	 }
	 
 }