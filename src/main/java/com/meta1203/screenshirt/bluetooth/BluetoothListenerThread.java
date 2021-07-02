package com.meta1203.screenshirt.bluetooth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class BluetoothListenerThread implements Runnable {
	private StreamConnectionNotifier notifier;
	
	public BluetoothListenerThread(StreamConnectionNotifier notifier) {
		this.notifier = notifier;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				log.info("waiting for bluetooth connection...");
				StreamConnection connection = notifier.acceptAndOpen();
				InputStream is = connection.openInputStream();
				Reader reader = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(reader);
				while (true) {
					String input = br.readLine();
					log.info(input);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
