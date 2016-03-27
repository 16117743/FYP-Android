package com.chat.bluetooth.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;

import android.util.Log;
import com.chat.bluetooth.R;
import com.chat.bluetooth.activity.MainActivity;
import com.chat.bluetooth.util.LogUtil;


public class BluetoothComunication extends Thread implements MusicHostInterface {
	 
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
			 
			 while (run)
			 {
					 try
					 {
						 int readtest = 0;
						 readtest = dataInputStream.readInt();
						 Thread.sleep(1000);
						 switch (readtest)
						 {
							 case 1:
								 LogUtil.e("read int 1\n");
								 if (dataInputStream.available() > 0)
								 {
									 LogUtil.e("dataInputStream.available(");
									 byte[] msg = new byte[dataInputStream.available()];
									 dataInputStream.read(msg, 0, dataInputStream.available());
									 String rx = new String(msg);
									 LogUtil.e(rx);
									 sendHandler(MainActivity.MSG_BLUETOOTH, Integer.toString(readtest) + ": " + rx);
									 stopComunication();
								 } else
									 LogUtil.e("not dataInputStream.available");
								 break;
							 case 2:
								 LogUtil.e("read int 2\n");
								 if (dataInputStream.available() > 0) {
									 LogUtil.e("dataInputStream.available(");
									 byte[] msg = new byte[dataInputStream.available()];
									 dataInputStream.read(msg, 0, dataInputStream.available());
									 String rx = new String(msg);
									 LogUtil.e(rx);
									 sendHandler(3, rx);
								 }
								 else
									 LogUtil.e("not dataInputStream.available");
								 break;
							 case 3:
								 LogUtil.e("read int 2\n");
								 if (dataInputStream.available() > 0) {
									 LogUtil.e("dataInputStream.available(");
									 byte[] msg = new byte[dataInputStream.available()];
									 dataInputStream.read(msg, 0, dataInputStream.available());
									 String rx = new String(msg);
									 LogUtil.e(rx);
									 sendHandler(MainActivity.MSG_BLUETOOTH, rx);
								 }
								 else
									 LogUtil.e("not dataInputStream.available");
								 break;
							 default:
								 sendHandler(MainActivity.MSG_BLUETOOTH, nameBluetooth + ": oops" );
								 break;
						 }
					 }
			 		catch (Exception e) {}
			 }//while
		 }catch (IOException e) {
			 LogUtil.e(e.getMessage());
			 
			 stopComunication();
			 sendHandler(MainActivity.MSG_TOAST, context.getString(R.string.lost_connection));
		 }
	}
	
	public boolean sendMessageByBluetooth(String msg,int whatToDo){
		try {
			if(dataOutputStream != null){
				dataOutputStream.writeInt(whatToDo);
				dataOutputStream.flush();

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

	/***********************************************************************************/
	/***********************************************************************************/
	@Override
	public void send1() {

	}

	@Override
	public int recv1() {
		return 0;
	}

	@Override
	public void send2() {

	}

	@Override
	public int recv2() {
		return 0;
	}

	@Override
	public void send3() {

	}

	@Override
	public int recv3() {
		return 0;
	}

	@Override
	public void send4() {

	}

	@Override
	public int recv4() {
		return 0;
	}

	@Override
	public void send5() {

	}

	@Override
	public int recv5() {
		return 0;
	}

	@Override
	public void send6() {

	}

	@Override
	public int recv6() {
		return 0;
	}

	@Override
	public void send7() {

	}

	@Override
	public int recv7() {
		return 0;
	}

	@Override
	public void send8() {

	}

	@Override
	public int recv8() {
		return 0;
	}
}