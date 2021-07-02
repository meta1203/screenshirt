package com.meta1203.screenshirt.bluetooth;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnectionNotifier;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class BluetoothManager {
	private LocalDevice radio;
	private UUID uuid = new UUID(42069l);
	private String url = "btspp://localhost:" + uuid.toString() + ";name=RemoteBluetooth";
	private StreamConnectionNotifier notifier;
	
	public BluetoothManager() throws IOException {
		// get the local device
		radio = LocalDevice.getLocalDevice();
		// log power status
		log.info(LocalDevice.isPowerOn());
		// set generally discoverable
		radio.setDiscoverable(DiscoveryAgent.GIAC);
	}
	
	@PostConstruct
	public void start() throws IOException {
		notifier = (StreamConnectionNotifier)Connector.open(url);
		new Thread(new BluetoothListenerThread(notifier)).run();
	}
}
