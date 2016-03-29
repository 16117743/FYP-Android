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

	/*****CONSTANTS ****************************/
	final int SONG_SELECT = 1;
	final int SONG_SELECTED = 2;
	final int DJ_COMMENT = 3;
	final int SKIP_SONG  = 4;
	final int ECHO_SHARED_PREF_SONGS = 5;
	final int ECHO_BLOB_SONGS = 6;
	final int REMOTE_SELECT = 7;
	final int WANT_END = 8;
	/********************************************/
	
	public BluetoothComunication(Context context, Handler handler){
		this.context = context;
		this.handler = handler;
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
			
			// sendHandler(MainActivity.MSG_TOAST, context.getString(R.string.connected_sucessfully));
			 sendMessageByBluetooth("msg0",0);
			 while (run)
			 {
					 try
					 {
						 int switcher = dataInputStream.readInt();
						 Thread.sleep(1000);
						 switch (switcher)
						 {
							 case 0:
								 LogUtil.e("read int 1\n");
								 String rx0 = processInput();
								 sendHandler(0, rx0);
								 break;
							 case SONG_SELECT:
								 LogUtil.e("read int 1\n");
								 String rx1 = processInput();
								 sendHandler(MainActivity.SONG_SELECT, rx1);
								 break;
							 case SONG_SELECTED:
								 LogUtil.e("read int 2\n");
								 String rx2 = processInput();
								 sendHandler(MainActivity.SONG_SELECTED, rx2);
								// sendHandler(MainActivity.DJ_COMMENT, rx2);
								 break;
							 case DJ_COMMENT:
								 LogUtil.e("read int 3\n");
								 String rx3 = processInput();
								 sendHandler(MainActivity.DJ_COMMENT, rx3);
								 break;
							 case SKIP_SONG:
								 LogUtil.e("read int 4\n");
								 String rx4 = processInput();
								 sendHandler(MainActivity.SKIP_SONG, rx4);
								 break;
							 case ECHO_SHARED_PREF_SONGS:
								 LogUtil.e("read int 4\n");
								 String rx5 = processInput();
								 sendHandler(MainActivity.ECHO_SHARED_PREF_SONGS, rx5);
								 break;
							 case ECHO_BLOB_SONGS:
								 LogUtil.e("read int 2\n");
								 String rx6 = processInput();
								 sendHandler(MainActivity.ECHO_SHARED_PREF_SONGS, rx6);
								 break;
							 case REMOTE_SELECT:
								 LogUtil.e("read int 2\n");
								 String rx7 = processInput();
								 sendHandler(MainActivity.REMOTE_SELECT, rx7);
								 break;
							 case WANT_END:
								 LogUtil.e("read int 2\n");
								 String rx8 = processInput();
								 sendHandler(MainActivity.WANT_END, rx8);
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

	public String processInput(){
		try {
			if (dataInputStream.available() > 0)
            {
                LogUtil.e("dataInputStream.available(");
                byte[] msg = new byte[dataInputStream.available()];
                dataInputStream.read(msg, 0, dataInputStream.available());
                String rx = new String(msg);
                return rx;
            } else {
                LogUtil.e("not dataInputStream.available");
                return null;
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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

	/**
	 * Send "I want to select a song" to server
	 */
	@Override
	public void send1() {

	}

	/**
	 * Received the song selection from server
	 */
	@Override
	public void recv1() {

	}
	/**************************************************/

	/**
	 * Send my selected song to server
	 */
	@Override
	public void send2() {

	}

	/**
	 * received song selection ok from server
	 */
	@Override
	public void recv2() {

	}
	/**************************************************/

	/**************************************************/
	/**
	 * Send My comment to the DJ
	 */
	@Override
	public void send3() {

	}

	/**
	 * Received DJ comment history
	 */
	@Override
	public void recv3() {

	}
	/**************************************************/

	/**************************************************/
	/**
	 * I want to skip a song
	 */
	@Override
	public void send4() {

	}

	/**
	 * Received song queue from server after skipping
	 */
	@Override
	public void recv4() {

	}
	/**************************************************/

	/**************************************************/
	/**
	 * Send songs from my local shared preferences
	 */
	@Override
	public void send5() {

	}

	/**
	 * Received a list of songs that matched the host
	 */
	@Override
	public void recv5() {

	}
	/**************************************************/

	/**************************************************/
	/**
	 * send a song from the blob service
	 */
	@Override
	public void send6() {

	}

	/**
	 * Received blob song ok
	 */
	@Override
	public void recv6() {

	}
	/**************************************************/

	/**************************************************/
	/**
	 * I want to select another song after this one
	 */
	@Override
	public void send7() {

	}

	/**
	 * Received go ahead for another selection from server
	 */
	@Override
	public void recv7() {

	}
	/**************************************************/

	/**************************************************/
	/**
	 * Send I'm finished with remote access
	 */
	@Override
	public void send8() {

	}

	/**
	 * Received remote disconnected ok
	 */
	@Override
	public void recv8() {

	}
	/**************************************************/
}