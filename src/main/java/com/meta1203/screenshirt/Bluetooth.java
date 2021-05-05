package com.meta1203.screenshirt;

import java.util.Set;

import org.sputnikdev.bluetooth.manager.BluetoothManager;
import org.sputnikdev.bluetooth.manager.DiscoveredAdapter;
import org.sputnikdev.bluetooth.manager.impl.BluetoothManagerBuilder;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class Bluetooth {
	private boolean connected;
	private BluetoothManager bm;
	private org.sputnikdev.bluetooth.URL adapterUrl;

	public Bluetooth() {
		bm = new BluetoothManagerBuilder()
				.withTinyBTransport(true)
				.withStarted(true)
				.build();
		
		Set<DiscoveredAdapter> adapters = bm.getDiscoveredAdapters();
		if (adapters.size() > 0) {
			for (DiscoveredAdapter x : adapters) {
				log.info(x);
			}
			adapterUrl = ((DiscoveredAdapter)adapters.toArray()[0]).getURL();
		}
	}
}
