package com.chat.bluetooth.business;

import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public interface IBusinessLogic {
	
	public interface OnSearchBluetoothListener{
		public abstract void onSearchBluetooth(List<BluetoothDevice> devicesFound);
	}

	public interface OnConnectionBluetoothListener{
		public abstract void onConnectionBluetooth(BluetoothSocket bluetoothSocket);
	}
	
	public interface OnBluetoothDeviceSelectedListener{
		public abstract void onBluetoothDeviceSelected(BluetoothDevice bluetoothDevice);
	}
	
}