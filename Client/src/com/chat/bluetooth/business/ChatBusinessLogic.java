package com.chat.bluetooth.business;

import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;

import com.chat.bluetooth.alertdialog.AlertDialogDevicesFound;
import com.chat.bluetooth.communication.BluetoothComunication;
import com.chat.bluetooth.manager.BluetoothManager;
import com.chat.bluetooth.broadcast.EventsBluetoothReceiver;
import com.chat.bluetooth.task.BluetoothClientTask;

public class ChatBusinessLogic implements IBusinessLogic.OnConnectionBluetoothListener,
	IBusinessLogic.OnBluetoothDeviceSelectedListener,
	IBusinessLogic.OnSearchBluetoothListener {
	
	private Context context;
	private Handler handler;
	
	private BluetoothManager bluetoothManager;
	private BluetoothComunication bluetoothComunication;
	private AlertDialogDevicesFound alertDialogDevicesFound;
	private EventsBluetoothReceiver eventsBluetoothReceiver;

	/**
	 *  @param context the activity that created this object
	 *  @param handler the embedded handler of the activity that created this object
	 * */
	public ChatBusinessLogic(Context context, Handler handler){
		this.context = context;
		this.handler = handler;
		
		bluetoothManager = new BluetoothManager();
		alertDialogDevicesFound = new AlertDialogDevicesFound(context, this);
		eventsBluetoothReceiver = new EventsBluetoothReceiver(context, this);
	}
	
	public void registerFilter(){
		eventsBluetoothReceiver.registerFilters();
	}
	
	public void unregisterFilter(){
		eventsBluetoothReceiver.unregisterFilters();
	}
	
	public void startFoundDevices(){
		stopCommucanition();
		
		eventsBluetoothReceiver.showProgress();
		bluetoothManager.getBluetoothAdapter().startDiscovery();
	}


	/**
	 *  creates a task for connecting to Music Host once the user hits connect
	 *  @param bluetoothDevice overiding implemented method by sending the device to custom startClient task
	 * */
	public void startClient(BluetoothDevice bluetoothDevice){
		BluetoothClientTask bluetoothClientTask = new BluetoothClientTask(context, this);
		bluetoothClientTask.execute(bluetoothDevice);
	}
	
	public void starCommunication(BluetoothSocket bluetoothSocket){
		bluetoothComunication = new BluetoothComunication(context, handler);
		bluetoothComunication.setBluetoothSocket(bluetoothSocket);
		bluetoothComunication.start();
	}
	
	public void stopCommucanition(){
		if(bluetoothComunication != null){
			bluetoothComunication.stopComunication();
		}
	}
	
	public boolean sendMessage(String message,int whatToDo){
		if(bluetoothComunication != null){
			return bluetoothComunication.sendMessageByBluetooth(message, whatToDo);
		}else{
			return false;
		}
	}
	
	public BluetoothManager getBluetoothManager(){
		return bluetoothManager;
	}

	/**
	 *  @param bluetoothDevice overiding implemented method by sending the device to custom startClient method
	 * */
	@Override
	public void onBluetoothDeviceSelected(BluetoothDevice bluetoothDevice) {
		startClient(bluetoothDevice);
	}

	/**
	 * 	Once the client connects, this method starts a communication thread.
	 *  @param bluetoothSocket pass the connected socket communication thread
	 * */
	@Override
	public void onConnectionBluetooth(BluetoothSocket bluetoothSocket) {
		starCommunication(bluetoothSocket);
	}

	/**
	 *
	 *  @param devicesFound list of devices found from using Androids Bluetooth API
	 * */
	@Override
	public void onSearchBluetooth(List<BluetoothDevice> devicesFound) {
		alertDialogDevicesFound.settingsAlertDialog(devicesFound);
	}
}