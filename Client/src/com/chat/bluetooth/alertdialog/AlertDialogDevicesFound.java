package com.chat.bluetooth.alertdialog;

import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.chat.bluetooth.business.IBusinessLogic.OnBluetoothDeviceSelectedListener;

public class AlertDialogDevicesFound extends AlertDialogGeneric implements OnClickListener{

	private List<BluetoothDevice> devicesFound;
	private OnBluetoothDeviceSelectedListener onBluetoothDeviceSelectedListener;
	private BluetoothDevice musicHostFound;
	
	public AlertDialogDevicesFound(Context context, OnBluetoothDeviceSelectedListener onBluetoothDeviceSelectedListener) {
		super(context);
		
		this.onBluetoothDeviceSelectedListener = onBluetoothDeviceSelectedListener;
	}

	/**
	 * called from ChatBusinessLogic Object
	 * @param devicesFound the devices found
	 */
	public void settingsAlertDialog(List<BluetoothDevice> devicesFound) {

	this.devicesFound = devicesFound;

	String[] MusicHostString =new String[1];

	for (int i = 0; i < devicesFound.size(); i++){
		if("U".equals(devicesFound.get(i).getName())) {
			MusicHostString[0] = "Tom's Pub";
			musicHostFound = devicesFound.get(i);
		}
	}



	alert.setTitle("Music Host found!");
	alert.setItems(MusicHostString, this);

	showAlertDialog();
}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		onBluetoothDeviceSelectedListener.onBluetoothDeviceSelected(musicHostFound);
	}
	
}