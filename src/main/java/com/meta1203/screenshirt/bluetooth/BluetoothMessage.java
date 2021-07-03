package com.meta1203.screenshirt.bluetooth;

import lombok.Data;

@Data
public class BluetoothMessage {
	private BluetoothConnection connection;
	private String message;
	
	public BluetoothMessage(BluetoothConnection connection, String message) {
		this.connection = connection;
		this.message = message;
	}
}
