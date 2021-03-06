package com.meta1203.screenshirt.bluetooth;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnectionNotifier;

import org.springframework.stereotype.Component;

import com.meta1203.screenshirt.ScreenshirtApplication;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class BluetoothManager {
	private boolean running = true;

	@SuppressWarnings("unused")
	private LocalDevice radio;
	private UUID uuid = new UUID(42069l);
	private String url = "btspp://localhost:" + uuid.toString() + ";name=RemoteBluetooth";
	private StreamConnectionNotifier notifier;
	private List<BluetoothConnection> connections = new CopyOnWriteArrayList<>();
	private Consumer<BluetoothMessage> messageListener = (BluetoothMessage s) -> {};

	public BluetoothManager() throws IOException {
		// get the local device
		radio = LocalDevice.getLocalDevice();
		// log power status
		log.info(LocalDevice.isPowerOn());
		// set generally discoverable
		// radio.setDiscoverable(DiscoveryAgent.GIAC);
	}

	@PreDestroy
	public void stop() {
		this.running = false;
		for (BluetoothConnection bc : connections) {
			try {
				bc.close();
			} catch (IOException e) {
				log.error("Failed to close connection!", e);
			}
		}
	}

	@PostConstruct
	public void start() throws IOException {
		Connection connection = Connector.open(url);
		log.info(connection.getClass().getName());
		notifier = (StreamConnectionNotifier)connection;

		// listen for and establish incoming bluetooth connections
		new Thread(() -> {
			log.info("Listening for bluetooth connections...");
			while (running) {
				try {
					BluetoothConnection bc = new BluetoothConnection(notifier.acceptAndOpen());
					log.info("Established new bluetooth connection.");
					connections.add(bc);
				} catch (IOException e) {
					log.error("failed to establish connection", e);
				}
			}
		}).start();

		// listen for incoming messages
		new Thread(() -> {
			log.info("reading from connections... " + connections.size());
			while (running) {
				for (BluetoothConnection bc : connections) {
					synchronized (bc) {
						if (bc.isClosed()) {
							this.connections.remove(bc);
							continue;
						}

						bc.read().thenApplyAsync((String s) -> new BluetoothMessage(bc, s), ScreenshirtApplication.EXECUTOR)
						.thenAcceptAsync(messageListener, ScreenshirtApplication.EXECUTOR);
					}
				}

				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					log.error("failed to wait for 10 ms");
				}
			}
		}).start();
	}

	public void setMessageListener(Consumer<BluetoothMessage> consoomer) {
		this.messageListener = consoomer;
	}
}
